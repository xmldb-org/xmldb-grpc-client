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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

@RemoteTest
class RemoteDatabaseIT {
  @ParameterizedTest
  @MethodSource("serverUrls")
  void getCollection(String serverUrl) throws XMLDBException {
    assertThat(DatabaseManager.getCollection(serverUrl)).isNotNull()
        .satisfies(RemoteDatabaseIT::assertCollection);
  }

  @ParameterizedTest
  @MethodSource("serverUrls")
  void getCollectionWithCredentials(String serverUrl) throws XMLDBException {
    assertThat(DatabaseManager.getCollection(serverUrl, "guest", "guest")).isNotNull()
        .satisfies(RemoteDatabaseIT::assertCollection);
  }

  @ParameterizedTest
  @MethodSource("serverUrls")
  void getUnkownCollection(String serverUrl) throws XMLDBException {
    assertThat(DatabaseManager.getCollection(serverUrl + "Blup")).isNull();
    assertThat(DatabaseManager.getCollection(serverUrl).getChildCollection("Blup")).isNull();
  }

  static void assertCollection(Collection collection) throws XMLDBException {
    assertResources(collection, new Resource("test1.xml", "<root1/>"),
        new Resource("test2.bin", "content2"));
    assertThat(collection.getChildCollectionCount()).isEqualTo(1);
    assertThat(collection.listChildCollections()).containsExactlyInAnyOrder("child");
    assertThat(collection.createId()).isNotNull().isNotBlank();
    assertThat(collection.getChildCollection("child")).isNotNull().satisfies(childCol -> {
      assertResources(childCol, new Resource("test3.xml", "<root3/>"),
          new Resource("test4.xml", "<root4/>"), new Resource("test5.bin", "content5"));
      assertThat(childCol.getChildCollectionCount()).isZero();
      assertThat(childCol.listChildCollections()).isEmpty();
      assertThat(childCol.createId()).isNotNull().isNotBlank();
      assertThatNoException().isThrownBy(childCol::close);
    });
    assertThatNoException().isThrownBy(collection::close);
  }

  static void assertResources(Collection collection, Resource... expectedResources)
      throws XMLDBException {
    assertThat(collection.getResourceCount()).isEqualTo(expectedResources.length);
    assertThat(collection.listResources()).containsExactlyInAnyOrder(
        Stream.of(expectedResources).map(Resource::id).toArray(String[]::new));
    for (Resource expectedResource : expectedResources) {
      final String id = expectedResource.id();
      assertThat(collection.getResource(id)).isNotNull().satisfies(res -> {
        assertThat(res.getId()).isEqualTo(id);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        res.getContentAsStream(baos);
        assertThat(baos.toString(UTF_8)).isEqualTo(expectedResource.content());
      });
    }
  }

  record Resource(String id, String content) {
  }

  static Stream<String> serverUrls() {
    // return Stream.of("xmldb:grpc://127.0.0.1:8080/db", "xmldb:grpc://[::1]:8080/db");
    return Stream.of("xmldb:grpc://127.0.0.1:8080/db");
  }

}
