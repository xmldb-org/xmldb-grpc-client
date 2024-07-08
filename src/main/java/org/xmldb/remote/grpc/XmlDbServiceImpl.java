/*
 * Licensed under the Apache License, Version 2.0 (the "License") you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.xmldb.remote.grpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.grpc.Messages;
import org.xmldb.api.grpc.XmlDbServiceGrpc;

import io.grpc.stub.StreamObserver;

public class XmlDbServiceImpl extends XmlDbServiceGrpc.XmlDbServiceImplBase {
  private static final Logger LOGGER = LoggerFactory.getLogger(XmlDbServiceImpl.class);

  @Override
  public void versionCall(Messages.EmptyRequest request,
      StreamObserver<Messages.VersionResponse> responseObserver) {
    LOGGER.info("versionCall({})", request);
    Messages.VersionResponse response =
        Messages.VersionResponse.newBuilder().setMajor(1).setMinor(0).build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
