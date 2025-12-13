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

import org.xmldb.api.base.ErrorCodes;
import org.xmldb.api.base.XMLDBException;

/**
 * This class extends {@link RemoteBaseService} to provide common remote service functionalities
 * specifically related to namespace management. It serves as a base class for remote query services
 * such as XQuery or XPath handling.
 * <p>
 * RemoteQueryService offers the capability to manage namespaces, including defining, retrieving,
 * and removing namespace prefixes and URIs. These functionalities are useful for remote query
 * execution that relies on namespaces for XML data and query processing.
 */
public class RemoteQueryService extends RemoteBaseService {

  RemoteQueryService(ServiceInfo info, RemoteCollection collection) {
    super(info, collection);
  }

  public void setNamespace(String prefix, String uri) throws XMLDBException {
    throw new XMLDBException(ErrorCodes.NOT_IMPLEMENTED, "Namespace management is not supported.");
  }

  public String getNamespace(String prefix) throws XMLDBException {
    throw new XMLDBException(ErrorCodes.NOT_IMPLEMENTED, "Namespace management is not supported.");
  }

  public void removeNamespace(String prefix) throws XMLDBException {
    throw new XMLDBException(ErrorCodes.NOT_IMPLEMENTED, "Namespace management is not supported.");
  }

  public void clearNamespaces() throws XMLDBException {
    throw new XMLDBException(ErrorCodes.NOT_IMPLEMENTED, "Namespace management is not supported.");
  }
}
