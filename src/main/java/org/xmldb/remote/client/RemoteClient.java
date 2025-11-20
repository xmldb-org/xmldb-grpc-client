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
import org.xmldb.api.grpc.EmptyRequest;
import org.xmldb.api.grpc.SystemInfo;
import org.xmldb.api.grpc.XmlDbServiceGrpc;

import io.grpc.CallCredentials;
import io.grpc.Channel;
import io.grpc.StatusRuntimeException;

public final class RemoteClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(RemoteClient.class);

  private final XmlDbServiceGrpc.XmlDbServiceBlockingStub blockingStub;

  public RemoteClient(final Channel channel, final CallCredentials callCredentials) {
    blockingStub = XmlDbServiceGrpc.newBlockingStub(channel).withCallCredentials(callCredentials);
  }

  /**
   * version from server.
   */
  public SystemInfo systemInfo() {
    LOGGER.info("Will try to version ...");
    EmptyRequest request = EmptyRequest.getDefaultInstance();
    try {
      SystemInfo response = blockingStub.systemInfo(request);
      LOGGER.info("Greeting: {}", response.getJavaVersion());
      return response;
    } catch (StatusRuntimeException e) {
      LOGGER.info("RPC failed: {}", e.getStatus());
      return null;
    }
  }

}
