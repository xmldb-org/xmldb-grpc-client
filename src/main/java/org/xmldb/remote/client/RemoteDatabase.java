/*
 * Licensed under the Apache License, Version 2.0 (the "License") you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package org.xmldb.remote.client;

import static org.xmldb.api.DatabaseManager.URI_PREFIX;

import java.net.URI;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;

public final class RemoteDatabase extends RemoteConfigurable implements Database {
  private static final Logger LOGGER = LoggerFactory.getLogger(RemoteDatabase.class);

  @Override
  public String getConformanceLevel() throws XMLDBException {
    return "";
  }

  @Override
  public String getName() throws XMLDBException {
    return "";
  }

  @Override
  public Collection getCollection(String uri, Properties info) throws XMLDBException {
    ConnectionInfo connectionInfo = null;
    try {
      connectionInfo = parseConnectionInfo(uri, info);
    } catch (RuntimeException e) {
      LOGGER.error("Error getting collection for URI {}", uri, e);
    }
    if (connectionInfo != null) {
      final var channel = connectionInfo.openChannel();
      final var credentials = new AuthenticationCredentials(connectionInfo::authentication);
      final var remoteClient = new RemoteClient(channel, credentials);
      remoteClient.systemInfo();
      channel.shutdownNow();
      return new RemoteCollection(null, remoteClient);
    }
    return null;
  }

  @Override
  public boolean acceptsURI(String uri) {
    try {
      return parseConnectionInfo(uri, null) != null;
    } catch (RuntimeException e) {
      LOGGER.error("Error accepting URI {}", uri, e);
    }
    return false;
  }

  private static ConnectionInfo parseConnectionInfo(final String uri, final Properties info) {
    if (uri.startsWith(URI_PREFIX)) {
      final URI dbUri = URI.create(uri.substring(URI_PREFIX.length()));
      if ("grpc".equals(dbUri.getScheme())) {
        final int port = dbUri.getPort();
        if (port > 0) {
          final String host = dbUri.getHost();
          if (!host.isEmpty()) {
            return new ConnectionInfo(host, port, dbUri.getPath(), info);
          }
        }
      }
    }
    return null;
  }
}
