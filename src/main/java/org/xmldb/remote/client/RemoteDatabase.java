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
import org.xmldb.api.grpc.CollectionMeta;

/**
 * RemoteDatabase is an immutable, thread-safe implementation of the {@link Database} interface. It
 * extends the {@link RemoteConfigurable} class, enabling configurable settings to interact with a
 * remote database system. This class serves as a client gateway to manage database collections over
 * a remote connection, such as a gRPC-enabled database service.
 */
public final class RemoteDatabase extends RemoteConfigurable implements Database {
  private static final Logger LOGGER = LoggerFactory.getLogger(RemoteDatabase.class);

  /**
   * Initializes a new instance of the {@code RemoteDatabase} class.
   */
  public RemoteDatabase() {
    super();
    LOGGER.debug("RemoteDatabase()");
  }

  @Override
  public String getConformanceLevel() {
    LOGGER.debug("getConformanceLevel()");
    return "";
  }

  @Override
  public String getName() {
    LOGGER.debug("getName()");
    return "";
  }

  @Override
  public Collection getCollection(String uri, Properties info) throws XMLDBException {
    LOGGER.debug("getCollection({}, {})", uri, info);
    try {
      final var connectionInfo = parseConnectionInfo(uri, info);
      if (connectionInfo != null) {
        final RemoteClient remoteClient = RemoteClient.create(connectionInfo);
        final CollectionMeta metaData = remoteClient.openRootCollection(uri, info);
        if (metaData.getName().isEmpty()) {
          LOGGER.warn("Collection for URI {} not found", uri);
        } else {
          return new RemoteCollection(null, remoteClient, metaData);
        }
      }
    } catch (RuntimeException e) {
      LOGGER.error("Error getting collection for URI {}", uri, e);
    }
    return null;
  }

  @Override
  public boolean acceptsURI(String uri) {
    LOGGER.debug("acceptsURI({})", uri);
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
