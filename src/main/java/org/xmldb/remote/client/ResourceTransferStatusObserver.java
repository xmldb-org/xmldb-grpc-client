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

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.grpc.ResourceTransferStatus;

import io.grpc.stub.StreamObserver;

/**
 * Implementation of the StreamObserver interface for handling status updates during a resource
 * transfer. This observer listens for status events, logs the received information, and tracks the
 * completion of the operation using a CountDownLatch.
 */
public class ResourceTransferStatusObserver implements StreamObserver<ResourceTransferStatus> {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(ResourceTransferStatusObserver.class);
  private final CountDownLatch completionLatch;

  /**
   * Default constructor for the ResourceTransferStatusObserver class. Initializes the observer with
   * a CountDownLatch to track the completion of resource transfer operations.
   */
  public ResourceTransferStatusObserver() {
    this.completionLatch = new CountDownLatch(1);;
  }

  /**
   * Waits for the completion signal with a timeout of 30 seconds. This method uses a
   * {@code CountDownLatch} to block the current thread until the latch is counted down, or the
   * timeout expires.
   *
   * @return {@code true} if the latch was counted down within the timeout, {@code false} if the
   *         timeout elapsed before the latch was counted down.
   * @throws InterruptedException if the current thread is interrupted while waiting.
   */
  public boolean awaitCompletion() throws InterruptedException {
    return completionLatch.await(30, SECONDS);
  }

  @Override
  public void onNext(ResourceTransferStatus resourceStoreRequest) {
    LOGGER.debug("onNext({})", resourceStoreRequest.getStatus());
  }

  @Override
  public void onError(Throwable throwable) {
    LOGGER.debug("onError({})", throwable, throwable);
    completionLatch.countDown();
  }

  @Override
  public void onCompleted() {
    LOGGER.debug("onCompleted()");
    completionLatch.countDown();
  }
}
