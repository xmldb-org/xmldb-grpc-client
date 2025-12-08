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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.grpc.StatusException;
import io.grpc.stub.BlockingClientCall;

final class ClientCallIterator<T> implements Iterator<T> {
  private final BlockingClientCall<?, T> blockingClientCall;

  ClientCallIterator(BlockingClientCall<?, T> blockingClientCall) {
    this.blockingClientCall = blockingClientCall;
  }

  @Override
  public boolean hasNext() {
    try {
      return blockingClientCall.hasNext();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    } catch (StatusException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public T next() {
    if (hasNext()) {
      try {
        return blockingClientCall.read(1, TimeUnit.SECONDS);
      } catch (InterruptedException | TimeoutException e) {
        Thread.currentThread().interrupt();
        throw new IllegalStateException(e);
      } catch (StatusException e) {
        throw new IllegalStateException(e);
      }
    } else {
      throw new NoSuchElementException();
    }
  }
}
