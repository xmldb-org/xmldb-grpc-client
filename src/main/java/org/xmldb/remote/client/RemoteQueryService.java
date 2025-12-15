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

  /**
   * Sets a namespace mapping in the internal namespace map used to evaluate queries. If
   * {@code prefix} is null or empty the default namespace is associated with the provided URI. A
   * null or empty {@code uri} results in an exception being thrown.
   *
   * @param prefix The prefix to set in the map. If {@code prefix} is empty or null the default
   *        namespace will be associated with the provided URI.
   * @param uri The URI for the namespace to be associated with prefix.
   * @throws XMLDBException if an error occurs whilst setting the namespace.
   */
  public void setNamespace(String prefix, String uri) throws XMLDBException {
    throw new XMLDBException(ErrorCodes.NOT_IMPLEMENTED, "Namespace management is not supported.");
  }

  /**
   * Returns the URI string associated with {@code prefix} from the internal namespace map. If
   * {@code prefix} is null or empty the URI for the default namespace will be returned. If a
   * mapping for the {@code prefix} can not be found null is returned.
   *
   * @param prefix The prefix to retrieve from the namespace map.
   * @return The URI associated with {@code prefix}
   * @throws org.xmldb.api.base.XMLDBException with expected error codes.
   *         {@code ErrorCodes.VENDOR_ERROR} for any vendor specific errors that occur.
   * @throws XMLDBException if an error occurs whilst getting the namespace.
   */
  public String getNamespace(String prefix) throws XMLDBException {
    throw new XMLDBException(ErrorCodes.NOT_IMPLEMENTED, "Namespace management is not supported.");
  }

  /**
   * Removes the namespace mapping associated with {@code prefix} from the internal namespace map.
   * If {@code prefix} is null or empty the mapping for the default namespace will be removed.
   *
   * @param prefix The prefix to remove from the namespace map. If {@code prefix} is null or empty
   *        the mapping for the default namespace will be removed.
   * @throws org.xmldb.api.base.XMLDBException with expected error codes.
   *         {@code ErrorCodes.VENDOR_ERROR} for any vendor specific errors that occur.
   */
  public void removeNamespace(String prefix) throws XMLDBException {
    throw new XMLDBException(ErrorCodes.NOT_IMPLEMENTED, "Namespace management is not supported.");
  }

  /**
   * Clears all namespace mappings defined.
   *
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  public void clearNamespaces() throws XMLDBException {
    throw new XMLDBException(ErrorCodes.NOT_IMPLEMENTED, "Namespace management is not supported.");
  }
}
