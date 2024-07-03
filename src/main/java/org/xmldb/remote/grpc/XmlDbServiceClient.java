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

import java.util.concurrent.TimeUnit;

import org.xmldb.api.grpc.Messages.EmptyRequest;
import org.xmldb.api.grpc.Messages.VersionResponse;
import org.xmldb.api.grpc.XmlDbServiceGrpc;

import io.grpc.ChannelCredentials;
import io.grpc.Grpc;
import io.grpc.ManagedChannel;

public class XmlDbServiceClient implements AutoCloseable {
  private final ManagedChannel channel;
  private final XmlDbServiceGrpc.XmlDbServiceBlockingStub blockingStub;

  XmlDbServiceClient(String host, int port, ChannelCredentials credentials) {
    channel = Grpc.newChannelBuilderForAddress(host, port, credentials).build();
    blockingStub = XmlDbServiceGrpc.newBlockingStub(channel);
  }

  public VersionResponse getVersion() {
    return blockingStub.versionCall(EmptyRequest.getDefaultInstance());
  }

  @Override
  public void close() {
    try {
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    }
  }
}
