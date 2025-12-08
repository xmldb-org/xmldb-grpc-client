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

import org.xmldb.api.base.XMLDBException;

/**
 * Functional interface representing an operation that consumes an {@link OutputStream} and performs
 * a specific task using it. This operation may throw an {@link XMLDBException}.
 */
@FunctionalInterface
public interface StreamConsumer {
  /**
   * Consumes the provided {@link OutputStream} and performs an operation using it. This method may
   * throw an {@link XMLDBException} if an error occurs during its execution.
   *
   * @param stream the {@link OutputStream} that will be consumed by this operation
   * @throws XMLDBException if an error arises while processing the stream
   */
  void accept(OutputStream stream) throws XMLDBException;
}
