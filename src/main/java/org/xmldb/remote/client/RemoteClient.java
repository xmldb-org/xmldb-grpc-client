/*
 * Licensed under the Apache License, Version 2.0 (the "License") you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package org.xmldb.remote.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.grpc.Messages;
import org.xmldb.api.grpc.XmlDbServiceGrpc;

import io.grpc.Channel;
import io.grpc.StatusRuntimeException;

public final class RemoteClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(RemoteClient.class);

  private final XmlDbServiceGrpc.XmlDbServiceBlockingStub blockingStub;

  public RemoteClient(final ConnectionInfo connectionInfo) {
    this(connectionInfo.openChannel());
  }

  public RemoteClient(final Channel channel) {
    blockingStub = XmlDbServiceGrpc.newBlockingStub(channel);
  }

  /**
   * version from server.
   */
  public void version() {
    LOGGER.info("Will try to version ...");
    Messages.EmptyRequest request = Messages.EmptyRequest.getDefaultInstance();
    Messages.Version response;
    try {
      response = blockingStub.versionCall(request);
    } catch (StatusRuntimeException e) {
      LOGGER.info("RPC failed: {}", e.getStatus());
      return;
    }
    LOGGER.info("Greeting: {}", response.getMajor());
  }
}
