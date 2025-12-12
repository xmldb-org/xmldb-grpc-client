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

import static org.xmldb.api.base.ErrorCodes.VENDOR_ERROR;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.base.XMLDBException;

import io.grpc.stub.StreamObserver;

/**
 * A generic implementation of the {@link StreamObserver} interface that provides methods for
 * handling streaming operations. This observer is capable of awaiting the completion of a stream
 * and handling errors during the process.
 *
 * @param <T> the type of the elements observed by this {@code GenericStreamObserver}
 */
public class GenericStreamObserver<T> implements StreamObserver<T> {
  private static final Logger LOGGER = LoggerFactory.getLogger(GenericStreamObserver.class);
  private final CountDownLatch completionLatch;

  private Throwable cause;

  /**
   * Constructs a new instance of ResourceTransferStatusObserver. This constructor initializes the
   * internal CountDownLatch to manage synchronization for observing the completion of the resource
   * transfer.
   */
  public GenericStreamObserver() {
    this.completionLatch = new CountDownLatch(1);
  }

  /**
   * Waits for the completion of the operation observed by this instance. This method uses a
   * synchronization mechanism to block until the associated CountDownLatch signals completion or
   * the timeout elapses.
   *
   * @throws XMLDBException if the waiting thread is interrupted, the timeout occurs before
   *         completion, or if an error (cause) occurred in the observed process.
   */
  public void awaitCompletion() throws XMLDBException {
    try {
      completionLatch.await();
      if (cause != null) {
        throw new XMLDBException(VENDOR_ERROR, cause);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      if (cause != null) {
        XMLDBException xmldbException = new XMLDBException(VENDOR_ERROR, cause);
        xmldbException.addSuppressed(e);
        throw xmldbException;
      } else {
        throw new XMLDBException(VENDOR_ERROR, e);
      }
    }
  }

  @Override
  public void onNext(T resourceStoreRequest) {
    LOGGER.info("onNext({})", resourceStoreRequest);
  }

  @Override
  public void onError(Throwable throwable) {
    LOGGER.debug("onError({})", throwable, throwable);
    cause = throwable;
    completionLatch.countDown();
  }

  @Override
  public void onCompleted() {
    LOGGER.debug("onCompleted()");
    completionLatch.countDown();
  }
}
