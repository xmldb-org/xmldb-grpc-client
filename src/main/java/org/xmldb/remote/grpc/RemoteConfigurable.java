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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.xmldb.api.base.Configurable;

public class RemoteConfigurable implements Configurable {
  private final Map<String, String> properties;

  protected RemoteConfigurable() {
    properties = new HashMap<>();
  }

  @Override
  public final String getProperty(String name) {
    return properties.get(name);
  }

  @Override
  public final String getProperty(String name, String defaultValue) {
    return properties.getOrDefault(name, defaultValue);
  }

  @Override
  public final void setProperty(String name, String value) {
    Objects.requireNonNull(name, "name must not be null");
    if (value == null) {
      properties.remove(name);
    } else {
      properties.put(name, value);
    }
  }
}
