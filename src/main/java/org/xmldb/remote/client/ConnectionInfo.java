/*
 * Licensed under the Apache License, Version 2.0 (the "License") you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package org.xmldb.remote.client;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Base64;
import java.util.Properties;

import io.grpc.ChannelCredentials;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

public record ConnectionInfo(String host, int port, String dbPath, Properties info) {
  String authentication() {
    var username = info.getProperty("user", "");
    var password = info.getProperty("password", "");
    var authentication = "%s:%s".formatted(username, password);
    return "Basic %s".formatted(Base64.getEncoder().encodeToString(authentication.getBytes(UTF_8)));
  }

  ManagedChannel openChannel() {
    ChannelCredentials channelCredentials = InsecureChannelCredentials.create();
    return Grpc.newChannelBuilderForAddress(host, port, channelCredentials).build();
  }
}
