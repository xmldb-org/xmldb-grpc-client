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

import org.xmldb.api.base.CompiledExpression;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XQueryService;

/**
 * Implements the {@link XQueryService} interface to provide remote execution of XQuery functions
 * and operations. This class extends {@link RemoteBaseService} and serves as a concrete
 * implementation of a remote service for XQuery functionalities, including the management of
 * namespaces, query execution, variable declaration, and handling of module paths.
 */
public class RemoteXQueryService extends RemoteQueryService implements XQueryService {

  RemoteXQueryService(RemoteCollection collection) {
    super(new ServiceInfo("XQueryService", "1.0"), collection);
  }

  @Override
  public ResourceSet query(String query) throws XMLDBException {
    return null;
  }

  @Override
  public ResourceSet queryResource(String id, String query) throws XMLDBException {
    return null;
  }

  @Override
  public CompiledExpression compile(String query) throws XMLDBException {
    return null;
  }

  @Override
  public ResourceSet execute(CompiledExpression expression) throws XMLDBException {
    return null;
  }

  @Override
  public void declareVariable(String qname, Object initialValue) throws XMLDBException {}

  @Override
  public void setXPathCompatibility(boolean backwardsCompatible) {}

  @Override
  public void setModuleLoadPath(String path) {}
}
