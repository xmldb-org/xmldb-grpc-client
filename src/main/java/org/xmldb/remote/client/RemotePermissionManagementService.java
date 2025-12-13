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

import java.util.List;
import java.util.Set;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.security.AclEntry;
import org.xmldb.api.security.Attributes;
import org.xmldb.api.security.GroupPrincipal;
import org.xmldb.api.security.Permission;
import org.xmldb.api.security.PermissionManagementService;
import org.xmldb.api.security.UserPrincipal;

/**
 * A service class that provides remote access to permission and access control management
 * functionalities in XML:DB-based systems. This class implements the
 * {@link PermissionManagementService} interface and extends the features of
 * {@link RemoteBaseService}.
 * <p>
 * The service manages permissions, ACL (Access Control Lists), ownership, and group information for
 * collections and resources within a remote database system. It integrates advanced access control
 * mechanisms to ensure security and proper management of resources.
 */
public class RemotePermissionManagementService extends RemoteBaseService
    implements PermissionManagementService {
  RemotePermissionManagementService(RemoteCollection collection) {
    super(new ServiceInfo("PermissionManagementService", "1.0"), collection);
  }

  @Override
  public Attributes getAttributes(Collection collection) throws XMLDBException {
    return null;
  }

  @Override
  public Attributes getAttributes(Resource resource) throws XMLDBException {
    return null;
  }

  @Override
  public Set<Permission> getPermissions(Collection collection) throws XMLDBException {
    return Set.of();
  }

  @Override
  public void setPermissions(Collection collection, Set<Permission> perms) throws XMLDBException {

  }

  @Override
  public Set<Permission> getPermissions(Resource resource) throws XMLDBException {
    return Set.of();
  }

  @Override
  public void setPermissions(Resource resource, Set<Permission> perms) throws XMLDBException {

  }

  @Override
  public List<AclEntry> getAcl(Collection collection) throws XMLDBException {
    return List.of();
  }

  @Override
  public void setAcl(Collection collection, List<AclEntry> aclEntries) throws XMLDBException {

  }

  @Override
  public List<AclEntry> getAcl(Resource resource) throws XMLDBException {
    return List.of();
  }

  @Override
  public void setAcl(Resource resource, List<AclEntry> aclEntries) throws XMLDBException {

  }

  @Override
  public UserPrincipal getOwner(Collection collection) throws XMLDBException {
    return null;
  }

  @Override
  public void setOwner(Collection collection, UserPrincipal owner) throws XMLDBException {

  }

  @Override
  public UserPrincipal getOwner(Resource resource) throws XMLDBException {
    return null;
  }

  @Override
  public void setOwner(Resource resource, UserPrincipal owner) throws XMLDBException {

  }

  @Override
  public GroupPrincipal getGroup(Collection collection) throws XMLDBException {
    return null;
  }

  @Override
  public void setGroup(Collection collection, GroupPrincipal group) throws XMLDBException {

  }

  @Override
  public GroupPrincipal getGroup(Resource resource) throws XMLDBException {
    return null;
  }

  @Override
  public void setGroup(Resource resource, GroupPrincipal group) throws XMLDBException {

  }
}
