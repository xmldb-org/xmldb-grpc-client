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

/**
 * A utility class that holds constant values used across the application. This class is final and
 * cannot be instantiated to ensure that the constants remain globally accessible and immutable.
 */
public final class Constants {
  /**
   * A constant specifying the default buffer size used in various operations such as data
   * streaming, processing, or file handling. The buffer size is set to optimize performance and
   * resource usage when transferring data or performing I/O tasks.
   */
  public static final int DEFAULT_BUFFER_SIZE = 4096;

  private Constants() {}
}
