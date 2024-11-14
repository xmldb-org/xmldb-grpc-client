/*
 * Licensed under the Apache License, Version 2.0 (the "License") you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package org.xmldb.remote.client;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.CallCredentials;
import io.grpc.Metadata;

public final class AuthenticationCredentials extends CallCredentials {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationCredentials.class);
  private static final Metadata.Key<String> AUTHENTICATION =
      Metadata.Key.of("Authentication", ASCII_STRING_MARSHALLER);

  private final Supplier<String> authenticationSupplier;

  public AuthenticationCredentials(final Supplier<String> authenticationSupplier) {
    this.authenticationSupplier = authenticationSupplier;
  }

  private void applyAuthentication(final RequestInfo requestInfo,
      final MetadataApplier metadataApplier) {
    LOGGER.info("applyRequestMetadata: {}", requestInfo);
    final var metadata = new Metadata();
    metadata.put(AUTHENTICATION, authenticationSupplier.get());
    metadataApplier.apply(metadata);
  }

  @Override
  public void applyRequestMetadata(final RequestInfo requestInfo, final Executor executor,
      final MetadataApplier metadataApplier) {
    executor.execute(() -> applyAuthentication(requestInfo, metadataApplier));
  }
}
