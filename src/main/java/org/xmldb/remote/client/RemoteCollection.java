/*
 * Licensed under the Apache License, Version 2.0 (the "License") you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package org.xmldb.remote.client;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.Service;
import org.xmldb.api.base.XMLDBException;

public class RemoteCollection extends RemoteConfigurable implements Collection {
  private static final Logger LOGGER = LoggerFactory.getLogger(RemoteCollection.class);

  private final RemoteCollection parent;
  private final RemoteClient remoteClient;

  RemoteCollection(RemoteCollection parent, RemoteClient remoteClient) {
    this.parent = parent;
    this.remoteClient = remoteClient;
    LOGGER.info("Created collection {}", this);
  }

  @Override
  public String getName() throws XMLDBException {
    LOGGER.info("getName() with {}", remoteClient);
    return null;
  }

  @Override
  public Collection getParentCollection() throws XMLDBException {
    return parent;
  }

  @Override
  public int getChildCollectionCount() throws XMLDBException {
    return 0;
  }

  @Override
  public List<String> listChildCollections() throws XMLDBException {
    return null;
  }

  @Override
  public Collection getChildCollection(String collectionName) throws XMLDBException {
    return null;
  }

  @Override
  public int getResourceCount() throws XMLDBException {
    return 0;
  }

  @Override
  public List<String> listResources() throws XMLDBException {
    return null;
  }

  @Override
  public <R extends Resource> R createResource(String id, Class<R> type) throws XMLDBException {
    return null;
  }

  @Override
  public void removeResource(Resource res) throws XMLDBException {

  }

  @Override
  public void storeResource(Resource res) throws XMLDBException {

  }

  @Override
  public Resource getResource(String id) throws XMLDBException {
    return null;
  }

  @Override
  public String createId() throws XMLDBException {
    return null;
  }

  @Override
  public boolean isOpen() throws XMLDBException {
    return false;
  }

  @Override
  public void close() throws XMLDBException {
    LOGGER.debug("close()");
  }

  @Override
  public Instant getCreationTime() throws XMLDBException {
    return null;
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
