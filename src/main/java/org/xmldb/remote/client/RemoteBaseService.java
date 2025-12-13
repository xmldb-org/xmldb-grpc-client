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
import org.xmldb.api.base.ErrorCodes;
import org.xmldb.api.base.Service;
import org.xmldb.api.base.XMLDBException;

/**
 * An abstract base class that extends the functionality of {@link RemoteConfigurable} and
 * implements the {@link Service} interface. This class provides a foundation for remote service
 * implementations in XML:DB-based systems, offering common functionality for service management
 * tasks.
 */
public abstract class RemoteBaseService extends RemoteConfigurable implements Service {
  private final ServiceInfo info;

  private RemoteCollection collection;

  RemoteBaseService(ServiceInfo info, RemoteCollection collection) {
    this.info = info;
    this.collection = collection;
  }

  RemoteCollection collection() {
    return collection;
  }

  @Override
  public final String getName() throws XMLDBException {
    return info.name();
  }

  @Override
  public final String getVersion() throws XMLDBException {
    return info.version();
  }

  @Override
  public final void setCollection(Collection col) throws XMLDBException {
    if (col instanceof RemoteCollection remoteCollection) {
      this.collection = remoteCollection;
    } else {
      throw new XMLDBException(ErrorCodes.INVALID_COLLECTION);
    }
  }
}
