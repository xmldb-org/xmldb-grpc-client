/*
 * Licensed under the Apache License, Version 2.0 (the "License") you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package org.xmldb.remote.client;

import static java.util.Spliterator.IMMUTABLE;
import static java.util.Spliterators.spliteratorUnknownSize;
import static org.xmldb.api.base.ErrorCodes.INVALID_RESOURCE;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.Service;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.grpc.ChildCollectionName;
import org.xmldb.api.grpc.CollectionMeta;
import org.xmldb.api.grpc.HandleId;
import org.xmldb.api.grpc.ResourceId;
import org.xmldb.api.grpc.ResourceMeta;
import org.xmldb.api.grpc.ResourceType;
import org.xmldb.api.modules.BinaryResource;
import org.xmldb.api.modules.XMLResource;

public class RemoteCollection extends RemoteConfigurable implements Collection {
  private static final Logger LOGGER = LoggerFactory.getLogger(RemoteCollection.class);

  private final AtomicBoolean open;
  private final RemoteCollection parent;
  private final RemoteClient remoteClient;
  private final CollectionMeta metaData;

  RemoteCollection(RemoteCollection parent, RemoteClient remoteClient, CollectionMeta metaData) {
    this.parent = parent;
    this.remoteClient = remoteClient;
    this.metaData = metaData;
    open = new AtomicBoolean(true);
    LOGGER.info("Created collection {}", this);
  }

  @Override
  public String getName() throws XMLDBException {
    LOGGER.info("getName() with {}", remoteClient);
    return metaData.getName();
  }

  @Override
  public Instant getCreationTime() throws XMLDBException {
    return Instant.ofEpochMilli(metaData.getCreationTime());
  }

  @Override
  public Collection getParentCollection() throws XMLDBException {
    return parent;
  }

  @Override
  public int getChildCollectionCount() throws XMLDBException {
    return Math.toIntExact(remoteClient.collectionCount(metaData.getCollectionId()).getCount());
  }

  @Override
  public List<String> listChildCollections() throws XMLDBException {
    return StreamSupport
        .stream(spliteratorUnknownSize(remoteClient.childCollections(metaData.getCollectionId()),
            IMMUTABLE), false)
        .map(ChildCollectionName::getChildName).toList();
  }

  @Override
  public Collection getChildCollection(String collectionName) throws XMLDBException {
    return new RemoteCollection(this, remoteClient,
        remoteClient.openChildCollection(metaData.getCollectionId(), collectionName));
  }

  @Override
  public int getResourceCount() throws XMLDBException {
    return Math.toIntExact(remoteClient.resourceCount(metaData.getCollectionId()).getCount());
  }

  @Override
  public List<String> listResources() throws XMLDBException {
    return StreamSupport.stream(
        spliteratorUnknownSize(remoteClient.listResources(metaData.getCollectionId()), IMMUTABLE),
        false).map(ResourceId::getResourceId).toList();
  }

  @Override
  public <R extends Resource> R createResource(String id, Class<R> type) throws XMLDBException {
    if (BinaryResource.class.equals(type)) {
      return type.cast(new RemoteBinaryResource(id, createResourceMeta(ResourceType.BINARY), this));
    } else if (XMLResource.class.equals(type)) {
      return type.cast(new RemoteXMLResource(id, createResourceMeta(ResourceType.XML), this));
    }
    throw new XMLDBException(INVALID_RESOURCE);
  }

  private ResourceMeta createResourceMeta(ResourceType resourceType) {
    long now = System.currentTimeMillis();
    return ResourceMeta.newBuilder().setResourceId(HandleId.getDefaultInstance())
        .setType(resourceType).setCreationTime(now).setLastModificationTime(now).build();
  }

  @Override
  public Resource getResource(String id) throws XMLDBException {
    final ResourceMeta resourceMeta = remoteClient.resource(metaData.getCollectionId(), id);
    return switch (resourceMeta.getType()) {
      case XML -> new RemoteXMLResource(id, resourceMeta, this);
      case BINARY -> new RemoteBinaryResource(id, resourceMeta, this);
      case UNRECOGNIZED -> throw new XMLDBException(INVALID_RESOURCE);
    };
  }

  @Override
  public void removeResource(Resource res) throws XMLDBException {}

  @Override
  public void storeResource(Resource res) throws XMLDBException {}


  @Override
  public String createId() throws XMLDBException {
    return null;
  }

  @Override
  public boolean isOpen() throws XMLDBException {
    return open.get();
  }

  @Override
  public void close() throws XMLDBException {
    if (open.compareAndSet(true, false)) {
      remoteClient.closeCollection(metaData.getCollectionId());
    }
  }

  @Override
  public <S extends Service> boolean hasService(Class<S> serviceType) {
    return false;
  }

  @Override
  public <S extends Service> Optional<S> findService(Class<S> serviceType) {
    return Optional.empty();
  }
}
