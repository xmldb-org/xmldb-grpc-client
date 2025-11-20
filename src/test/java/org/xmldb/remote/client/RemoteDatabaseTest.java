/*
 * Licensed under the Apache License, Version 2.0 (the "License") you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package org.xmldb.remote.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.xmldb.api.base.XMLDBException;

class RemoteDatabaseTest {
  RemoteDatabase db = new RemoteDatabase();

  @Test
  void getConformanceLevel() throws XMLDBException {
    assertThat(db.getConformanceLevel()).isEmpty();
  }

  @Test
  void getName() throws XMLDBException {
    assertThat(db.getName()).isEmpty();
  }

  @ParameterizedTest
  @CsvSource(textBlock = """
      gugus:grpc:,            false
      xmldb:exist:,           false
      xmldb:grpc:,            false
      xmldb:grpc://,          false
      xmldb:grpc:// :123,     false
      xmldb:grpc://host,      false
      xmldb:grpc://host:0,    false
      xmldb:grpc://host:9000, true
      """)
  void acceptsURI(String uri, boolean expected) {
    assertThat(db.acceptsURI(uri)).isEqualTo(expected);
  }

  @ParameterizedTest
  @CsvSource(textBlock = """
      xmldb:grpc://[::1]:9000/db,     guest,   guest
      xmldb:grpc://127.0.0.1:9000/db, johnDoe, mySecret
      """)
  void getCollection(String uri, String user, String secret) throws XMLDBException {
    var collection = db.getCollection(uri, properties(user, secret));
    assertThat(collection).isNotNull();
    assertThatNoException().isThrownBy(collection::close);
  }

  Properties properties(String user, String pwd) {
    Properties properties = new Properties();
    properties.setProperty("user", user);
    properties.setProperty("password", pwd);
    return properties;
  }
}
