/*
 * Licensed under the Apache License, Version 2.0 (the "License") you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
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

/**
 * Represents a collection of resources stored remotely and capable of interacting with a remote
 * client for various collection operations. This class provides functionality for managing
 * collections and their resources, including creation, retrieval, and listing of resources and
 * child collections.
 * <p>
 * This class extends {@code RemoteConfigurable} to allow storing and retrieving properties for
 * configuration and implements the {@code Collection} interface to define operations associated
 * with remote collections.
 */
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
    LOGGER.debug("Created remote collection {}", this);
  }

  interface RemoteClientConsumer {
    void accept(RemoteClient remoteClient) throws XMLDBException;
  }

  interface RemoteClientFunction<T> {
    T accept(RemoteClient remoteClient) throws XMLDBException;
  }

  void call(RemoteClientConsumer action) throws XMLDBException {
    action.accept(remoteClient);
  }

  <T> T execute(RemoteClientFunction<T> function) throws XMLDBException {
    return function.accept(remoteClient);
  }

  @Override
  public String getName() throws XMLDBException {
    LOGGER.debug("getName()");
    return metaData.getName();
  }

  @Override
  public Instant getCreationTime() throws XMLDBException {
    LOGGER.debug("getCreationTime()");
    return Instant.ofEpochMilli(metaData.getCreationTime());
  }

  @Override
  public Collection getParentCollection() throws XMLDBException {
    LOGGER.debug("getParentCollection()");
    return parent;
  }

  @Override
  public int getChildCollectionCount() throws XMLDBException {
    LOGGER.debug("getChildCollectionCount()");
    return Math.toIntExact(remoteClient.collectionCount(metaData.getCollectionId()).getCount());
  }

  @Override
  public List<String> listChildCollections() throws XMLDBException {
    LOGGER.debug("listChildCollections()");
    return StreamSupport
        .stream(spliteratorUnknownSize(remoteClient.childCollections(metaData.getCollectionId()),
            IMMUTABLE), false)
        .map(ChildCollectionName::getChildName).toList();
  }

  @Override
  public Collection getChildCollection(String childCollectionName) throws XMLDBException {
    LOGGER.debug("getChildCollection({})", childCollectionName);
    final CollectionMeta collectionMeta =
        remoteClient.openChildCollection(metaData.getCollectionId(), childCollectionName);
    if (collectionMeta.getName().isEmpty()) {
      LOGGER.warn("Child collection {} not found", childCollectionName);
      return null;
    } else {
      return new RemoteCollection(this, remoteClient, collectionMeta);
    }
  }

  @Override
  public int getResourceCount() throws XMLDBException {
    LOGGER.debug("getResourceCount()");
    return Math.toIntExact(remoteClient.resourceCount(metaData.getCollectionId()).getCount());
  }

  @Override
  public List<String> listResources() throws XMLDBException {
    LOGGER.debug("listResources()");
    return StreamSupport.stream(
        spliteratorUnknownSize(remoteClient.listResources(metaData.getCollectionId()), IMMUTABLE),
        false).map(ResourceId::getResourceId).toList();
  }

  @Override
  public <T, R extends Resource<T>> R createResource(String id, Class<R> type)
      throws XMLDBException {
    LOGGER.debug("createResource({}, {})", id, type);
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
  public Resource<?> getResource(String id) throws XMLDBException {
    LOGGER.debug("getResource({})", id);
    final ResourceMeta resourceMeta = remoteClient.openResource(metaData.getCollectionId(), id);
    return switch (resourceMeta.getType()) {
      case XML -> new RemoteXMLResource(id, resourceMeta, this);
      case BINARY -> new RemoteBinaryResource(id, resourceMeta, this);
      case UNRECOGNIZED -> throw new XMLDBException(INVALID_RESOURCE);
    };
  }

  @Override
  public void removeResource(Resource<?> res) throws XMLDBException {
    LOGGER.debug("removeResource() with {}", res);
    remoteClient.removeResource(metaData.getCollectionId(), res.getId());
  }

  @Override
  public void storeResource(Resource<?> res) throws XMLDBException {
    LOGGER.warn("storeResource() with {}", res);
  }

  @Override
  public String createId() throws XMLDBException {
    LOGGER.debug("createId()");
    return remoteClient.createId(metaData.getCollectionId());
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
    LOGGER.warn("hasService({})", serviceType);
    return false;
  }

  @Override
  public <S extends Service> Optional<S> findService(Class<S> serviceType) {
    LOGGER.warn("findService({})", serviceType);
    return Optional.empty();
  }

  @Override
  public String toString() {
    return "/%s".formatted(metaData.getName());
  }
}
