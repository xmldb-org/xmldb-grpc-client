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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.URI;
import java.util.Base64;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Supplier;

import io.grpc.ChannelCredentials;
import io.grpc.Grpc;
import io.grpc.ManagedChannel;

/**
 * Represents connection details for establishing a connection to a remote service or database. This
 * record encapsulates the host, port, database path, and a set of additional connection properties.
 *
 * @param host the hostname or IP address of the remote server
 * @param port the port number on which the remote server is listening to
 * @param dbPath the database path or resource path on the remote server
 * @param info additional connection properties, including authentication credentials such as "user"
 *        and "password"
 */
public record ConnectionInfo(String host, int port, String dbPath, Properties info) {
  /**
   * Validates that the provided parameters for a {@code ConnectionInfo} object are not null. This
   * constructor ensures that the mandatory fields required to establish a connection are correctly
   * defined and non-empty.
   */
  public ConnectionInfo {
    Objects.requireNonNull(host, "host must not be null");
    Objects.requireNonNull(dbPath, "dbPath must not be null");
    info = Objects.requireNonNullElseGet(info, Properties::new);
  }

  static ConnectionInfo create(final URI dbUri, final Properties info) {
    return new ConnectionInfo(dbUri.getHost(), dbUri.getPort(), dbUri.getPath(), info);
  }

  /**
   * Constructs a Basic Authentication header value by encoding the user and password properties
   * from the associated connection information into a Base64 string. If a password is not provided,
   * only the username will be included.
   *
   * @return a string representation of the Basic Authentication header value in the format "Basic
   *         <base64-encoded-credentials>" where credentials are formatted as "user:password".
   */
  String authentication() {
    final StringBuilder authenticationBuilder = new StringBuilder(20);
    final String username = info.getProperty("user", System.getProperty("user.name"));
    if (username.contains(":")) {
      throw new IllegalArgumentException("Username cannot contain a colon character");
    }
    authenticationBuilder.append(username);
    final String password = info.getProperty("password");
    if (password != null) {
      authenticationBuilder.append(":").append(password);
    }
    return "Basic %s".formatted(
        Base64.getEncoder().encodeToString(authenticationBuilder.toString().getBytes(UTF_8)));
  }

  /**
   * Opens a gRPC channel to the specified host and port using insecure channel credentials.
   *
   * @return a {@code ManagedChannel} instance that represents the communication channel to the
   *         remote server.
   */
  ManagedChannel openChannel(Supplier<ChannelCredentials> credentialsSupplier) {
    return Grpc.newChannelBuilderForAddress(host, port, credentialsSupplier.get()).build();
  }
}
