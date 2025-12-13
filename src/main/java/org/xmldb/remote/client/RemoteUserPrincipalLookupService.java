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

import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.security.GroupPrincipal;
import org.xmldb.api.security.UserPrincipal;
import org.xmldb.api.security.UserPrincipalLookupService;

/**
 * A service implementation that provides remote lookup functionality for user principals and group
 * principals within an XML:DB system. This class extends {@link RemoteBaseService} and implements
 * the {@link UserPrincipalLookupService} interface to enable the retrieval of user and group
 * identity information by name.
 */
public class RemoteUserPrincipalLookupService extends RemoteBaseService
    implements UserPrincipalLookupService {
  RemoteUserPrincipalLookupService(RemoteCollection collection) {
    super(new ServiceInfo("UserPrincipalLookupService", "1.0"), collection);
  }

  @Override
  public UserPrincipal lookupPrincipalByName(String name) throws XMLDBException {
    return null;
  }

  @Override
  public GroupPrincipal lookupPrincipalByGroupName(String group) throws XMLDBException {
    return null;
  }
}
