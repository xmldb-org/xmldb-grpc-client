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

import org.xmldb.api.grpc.XmlDbServiceGrpc;

import io.grpc.StatusException;

/**
 * Represents a remote action that can be performed using a gRPC service stub.
 *
 * @param <T> the type of the result produced by this action
 */
@FunctionalInterface
public interface RemoteAction<T> {
  /**
   * Executes a remote action using the provided gRPC service stub.
   *
   * @param stub the gRPC service stub used to perform the remote action
   * @return the result of the remote action
   * @throws StatusException if an error occurs during the execution of the remote action
   */
  T apply(XmlDbServiceGrpc.XmlDbServiceBlockingV2Stub stub) throws StatusException;
}
