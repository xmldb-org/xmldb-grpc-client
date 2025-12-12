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

import java.io.OutputStream;

import org.xmldb.api.grpc.ResourceStoreRequest;

import com.google.protobuf.ByteString;

import io.grpc.stub.StreamObserver;

/**
 * A specialized OutputStream implementation for streaming resource data through a gRPC observer.
 * This class is designed to work with {@link ResourceStoreRequest} builders and a
 * {@link StreamObserver} to handle resource data transfer.
 */
class ResourceTransferOutputStream extends OutputStream {
  private final ResourceStoreRequest.Builder builder;
  private final StreamObserver<ResourceStoreRequest> observer;
  private final byte[] single_byte_buffer = new byte[1];

  ResourceTransferOutputStream(ResourceStoreRequest.Builder builder,
      StreamObserver<ResourceStoreRequest> observer) {
    this.builder = builder;
    this.observer = observer;
  }

  @Override
  public void write(int b) {
    single_byte_buffer[0] = (byte) b;
    write(single_byte_buffer, 0, 1);
  }

  @Override
  public void write(byte[] buffer, int off, int len) {
    observer.onNext(builder.setDataChunk(ByteString.copyFrom(buffer, off, len)).build());
  }

  @Override
  public void close() {
    // no action
  }
}
