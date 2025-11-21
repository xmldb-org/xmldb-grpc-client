/*
 * Licensed under the Apache License, Version 2.0 (the "License") you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.xmldb.remote.client;


import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

@RemoteTest
class RemoteDatabaseRT {
  @ParameterizedTest
  @MethodSource("serverUrls")
  void getCollection(String serverUrl) throws XMLDBException {
    assertThat(DatabaseManager.getCollection(serverUrl)).isNotNull()
        .satisfies(RemoteDatabaseRT::assertCollection);
  }

  @ParameterizedTest
  @MethodSource("serverUrls")
  void getCollectionWithCredentials(String serverUrl) throws XMLDBException {
    assertThat(DatabaseManager.getCollection(serverUrl, "guest", "guest")).isNotNull()
        .satisfies(RemoteDatabaseRT::assertCollection);
  }

  static void assertCollection(Collection collection) throws XMLDBException {
    assertResources(collection, "test1.xml", "test2.xml");
    assertThat(collection.getChildCollectionCount()).isEqualTo(1);
    assertThat(collection.listChildCollections()).containsExactlyInAnyOrder("child");
    assertThat(collection.getChildCollection("child")).isNotNull().satisfies(childCol -> {
      assertResources(childCol, "test3.xml");
      assertThat(childCol.getChildCollectionCount()).isZero();
      assertThat(childCol.listChildCollections()).isEmpty();
      assertThatNoException().isThrownBy(childCol::close);
    });
    assertThatNoException().isThrownBy(collection::close);
  }

  static void assertResources(Collection collection, String... expectedResourcesIds)
      throws XMLDBException {
    assertThat(collection.getResourceCount()).isEqualTo(expectedResourcesIds.length);
    assertThat(collection.listResources()).containsExactlyInAnyOrder(expectedResourcesIds);
    for (String expectedResourcesId : expectedResourcesIds) {
      assertThat(collection.getResource(expectedResourcesId)).isNotNull().satisfies(res -> {
        assertThat(res.getId()).isEqualTo(expectedResourcesId);
      });
    }
  }

  static Stream<String> serverUrls() {
    // return Stream.of("xmldb:grpc://127.0.0.1:8080/db", "xmldb:grpc://[::1]:8080/db");
    return Stream.of("xmldb:grpc://127.0.0.1:8080/db");
  }
}
