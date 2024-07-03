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

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.grpc.Messages.EmptyRequest;
import org.xmldb.api.grpc.Messages.VersionResponse;
import org.xmldb.api.grpc.XmlDbServiceGrpc;

import io.grpc.Grpc;
import io.grpc.Server;
import io.grpc.ServerCredentials;
import io.grpc.TlsServerCredentials;
import io.grpc.stub.StreamObserver;

public class ServerTls {
  private static final Logger LOGGER = LoggerFactory.getLogger(ServerTls.class);

  private Server server;

  private final int port;
  private final ServerCredentials creds;

  public ServerTls(int port, ServerCredentials creds) {
    this.port = port;
    this.creds = creds;
  }

  private void start() throws IOException {
    server = Grpc.newServerBuilderForPort(port, creds).addService(new XmlDbServiceImpl()).build()
        .start();
    LOGGER.info("Server started, listening on {}", port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        ServerTls.this.stop();
        System.err.println("*** server shut down");
      }
    });
  }

  private void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  /**
   * Main launches the server from the command line.
   */
  public static void main(String[] args) throws IOException, InterruptedException {

    if (args.length < 3 || args.length > 4) {
      System.out.println(
          """
              USAGE: ServerTls port certChainFilePath privateKeyFilePath [trustCertCollectionFilePath]
              Note: You only need to supply trustCertCollectionFilePath if you want to enable Mutual TLS.
              """);
      System.exit(0);
    }

    // If only providing a private key, you can use TlsServerCredentials.create() instead of
    // interacting with the Builder.
    TlsServerCredentials.Builder tlsBuilder =
        TlsServerCredentials.newBuilder().keyManager(new File(args[1]), new File(args[2]));
    if (args.length == 4) {
      tlsBuilder.trustManager(new File(args[3]));
      tlsBuilder.clientAuth(TlsServerCredentials.ClientAuth.REQUIRE);
    }
    final ServerTls server = new ServerTls(Integer.parseInt(args[0]), tlsBuilder.build());
    server.start();
    server.blockUntilShutdown();
  }

  static class XmlDbServiceImpl extends XmlDbServiceGrpc.XmlDbServiceImplBase {
    @Override
    public void versionCall(EmptyRequest request,
        StreamObserver<VersionResponse> responseObserver) {
      VersionResponse response = VersionResponse.newBuilder().setMajor(1).setMinor(0).build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }
  }
}
