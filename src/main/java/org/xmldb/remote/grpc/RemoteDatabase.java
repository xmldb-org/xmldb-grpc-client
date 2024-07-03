/*
 * Licensed under the Apache License, Version 2.0 (the "License") you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.xmldb.remote.grpc;

import java.util.Properties;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;

import io.grpc.ManagedChannelRegistry;

public class RemoteDatabase extends RemoteConfigurable implements Database {
  @Override
  public String getName() throws XMLDBException {
    return "";
  }

  @Override
  public Collection getCollection(String uri, Properties info) throws XMLDBException {
    ManagedChannelRegistry.getDefaultRegistry();
    return null;
  }

  @Override
  public boolean acceptsURI(String uri) {
    return false;
  }

  @Override
  public String getConformanceLevel() throws XMLDBException {
    return "";
  }
}
