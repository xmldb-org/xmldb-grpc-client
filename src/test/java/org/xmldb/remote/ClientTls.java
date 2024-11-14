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

package org.xmldb.remote;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.remote.client.AuthenticationCredentials;
import org.xmldb.remote.client.RemoteClient;

import io.grpc.ChannelCredentials;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.TlsChannelCredentials;

public class ClientTls {
  private static final Logger LOGGER = LoggerFactory.getLogger(ClientTls.class);

  /**
   * Greet server. If provided, the first element of {@code args} is the name to use in the
   * greeting.
   */
  public static void main(String[] args) throws Exception {

    if (args.length < 2 || args.length == 4 || args.length > 5) {
      System.out.println("USAGE: HelloWorldClientTls host port [trustCertCollectionFilePath "
          + "[clientCertChainFilePath clientPrivateKeyFilePath]]\n  Note: clientCertChainFilePath and "
          + "clientPrivateKeyFilePath are only needed if mutual auth is desired.");
      System.exit(0);
    }
    try {
      final ChannelCredentials channelCredentials;
      if (args.length == 2) {
        channelCredentials = InsecureChannelCredentials.create();
      } else {
        // If only defaults are necessary, you can use TlsChannelCredentials.create() instead of
        // interacting with the Builder.
        TlsChannelCredentials.Builder tlsBuilder = TlsChannelCredentials.newBuilder();
        switch (args.length) {
          case 5:
            tlsBuilder.keyManager(new File(args[3]), new File(args[4]));
            // fallthrough
          case 3:
            tlsBuilder.trustManager(new File(args[2]));
            // fallthrough
          default:
        }
        channelCredentials = tlsBuilder.build();
      }
      String host = args[0];
      int port = Integer.parseInt(args[1]);
      ManagedChannel channel = Grpc.newChannelBuilderForAddress(host, port, channelCredentials)
          /* Only for using provided test certs. */
          .overrideAuthority("foo.test.google.fr").build();
      try {
        String authentication = Base64.getEncoder().encodeToString("guest:guest".getBytes(UTF_8));
        RemoteClient client =
            new RemoteClient(channel, new AuthenticationCredentials(() -> authentication));
        client.systemInfo();
      } finally {
        channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
      }
    } catch (Exception e) {
      LOGGER.error("Failed to start client", e);
    }
  }
}
