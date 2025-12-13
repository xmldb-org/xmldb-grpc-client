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

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;

/**
 * Provides remote management functionality for collections in an XML:DB-based system. This class
 * implements the {@link CollectionManagementService} interface and extends the
 * {@link RemoteBaseService} class to handle remote operations on collections such as creation,
 * removal, movement, and copying.
 */
public class RemoteCollectionManagementService extends RemoteBaseService
    implements CollectionManagementService {

  RemoteCollectionManagementService(RemoteCollection collection) {
    super(new ServiceInfo("CollectionManagementService", "1.0"), collection);
  }

  @Override
  public Collection createCollection(String name) throws XMLDBException {
    return null;
  }

  @Override
  public void removeCollection(String name) throws XMLDBException {}

  @Override
  public void move(String collection, String destination, String newName) throws XMLDBException {}

  @Override
  public void moveResource(String resourcePath, String destinationPath, String newName)
      throws XMLDBException {}

  @Override
  public void copyResource(String resourcePath, String destinationPath, String newName)
      throws XMLDBException {}

  @Override
  public void copy(String collection, String destination, String newName) throws XMLDBException {}
}
