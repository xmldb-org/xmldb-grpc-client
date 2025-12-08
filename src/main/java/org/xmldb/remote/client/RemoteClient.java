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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.base.ErrorCodes;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.grpc.ChildCollectionName;
import org.xmldb.api.grpc.CollectionMeta;
import org.xmldb.api.grpc.Count;
import org.xmldb.api.grpc.Empty;
import org.xmldb.api.grpc.HandleId;
import org.xmldb.api.grpc.ResourceData;
import org.xmldb.api.grpc.ResourceId;
import org.xmldb.api.grpc.ResourceLoadRequest;
import org.xmldb.api.grpc.ResourceMeta;
import org.xmldb.api.grpc.RootCollectionName;
import org.xmldb.api.grpc.SystemInfo;
import org.xmldb.api.grpc.XmlDbServiceGrpc;

import io.grpc.CallCredentials;
import io.grpc.Channel;
import io.grpc.InsecureChannelCredentials;
import io.grpc.StatusException;

/**
 * The {@code RemoteClient} class provides a client for interacting with a remote XML database over
 * gRPC. It encapsulates operations to manage and query the database system, collections, and
 * resources.
 */
public final class RemoteClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(RemoteClient.class);
  private static final Empty EMPTY = Empty.getDefaultInstance();

  private final XmlDbServiceGrpc.XmlDbServiceBlockingV2Stub blockingStub;

  /**
   * Initializes a new instance of the {@code RemoteClient} class with the specified gRPC channel
   * and call credentials.
   *
   * @param channel the gRPC channel to communicate with the server
   * @param callCredentials the call credentials for the gRPC communication
   */
  RemoteClient(final Channel channel, final CallCredentials callCredentials) {
    blockingStub = XmlDbServiceGrpc.newBlockingV2Stub(channel).withCallCredentials(callCredentials);
  }

  private static XMLDBException handleStatusException(StatusException e) {
    LOGGER.info("RPC failed: {}", e.getStatus());
    return new XMLDBException(ErrorCodes.VENDOR_ERROR, e.getStatus().getDescription(), e);
  }

  /**
   * Creates and returns a new instance of {@code RemoteClient} using the specified connection
   * information.
   *
   * @param connectionInfo the connection details including host, port, database path, and
   *        authentication properties
   * @return a newly created instance of {@code RemoteClient}
   */
  public static RemoteClient create(ConnectionInfo connectionInfo) {
    return new RemoteClient(connectionInfo.openChannel(InsecureChannelCredentials::create),
        new AuthenticationCredentials(connectionInfo::authentication));
  }

  <T> T withStub(RemoteAction<T> action) throws XMLDBException {
    try {
      return action.apply(blockingStub);
    } catch (StatusException e) {
      throw handleStatusException(e);
    }
  }

  /**
   * Retrieves system information from the remote XML database.
   *
   * @return an instance of {@code SystemInfo} containing details about the system configuration and
   *         status.
   * @throws XMLDBException if there is an error during the communication with the database or if
   *         the system information retrieval fails.
   */
  public SystemInfo systemInfo() throws XMLDBException {
    LOGGER.debug("systemInfo()");
    return withStub(stub -> stub.systemInfo(EMPTY));
  }

  CollectionMeta openRootCollection(String uri, Properties info) throws XMLDBException {
    LOGGER.debug("openRootCollection({}, {}})", uri, info);
    return withStub(stub -> {
      final Map<String, String> infoMap = new HashMap<>();
      info.forEach((key, value) -> infoMap.put(String.valueOf(key), String.valueOf(value)));
      return stub.openRootCollection(
          RootCollectionName.newBuilder().setUri(uri).putAllInfo(infoMap).build());
    });
  }

  CollectionMeta openChildCollection(HandleId collectionHandle, String collectionName)
      throws XMLDBException {
    LOGGER.debug("openChildCollection({}, {}})", collectionHandle, collectionName);
    return withStub(stub -> stub.openChildCollection(ChildCollectionName.newBuilder()
        .setCollectionId(collectionHandle).setChildName(collectionName).build()));
  }

  Count resourceCount(HandleId collectionHandle) throws XMLDBException {
    LOGGER.debug("resourceCount({})", collectionHandle);
    return withStub(stub -> stub.resourceCount(collectionHandle));
  }

  Iterator<ResourceId> listResources(HandleId collectionHandle) throws XMLDBException {
    LOGGER.debug("listResources({})", collectionHandle);
    return withStub(stub -> new ClientCallIterator<>(stub.listResources(collectionHandle)));
  }

  Count collectionCount(HandleId collectionHandle) throws XMLDBException {
    LOGGER.debug("collectionCount({})", collectionHandle);
    return withStub(stub -> stub.collectionCount(collectionHandle));
  }

  Iterator<ChildCollectionName> childCollections(HandleId collectionHandle) throws XMLDBException {
    LOGGER.debug("childCollections({})", collectionHandle);
    return withStub(stub -> new ClientCallIterator<>(stub.childCollections(collectionHandle)));
  }

  void closeCollection(HandleId collectionHandle) throws XMLDBException {
    LOGGER.debug("closeCollection({})", collectionHandle);
    withStub(stub -> stub.closeCollection(collectionHandle));
  }

  ResourceMeta openResource(HandleId collectionHandle, String resourceId) throws XMLDBException {
    LOGGER.debug("openResource({}, {})", collectionHandle, resourceId);
    return withStub(stub -> stub.openResource(ResourceId.newBuilder()
        .setCollectionId(collectionHandle).setResourceId(resourceId).build()));
  }

  void closeResource(HandleId resourceHandle) throws XMLDBException {
    LOGGER.debug("closeResource({})", resourceHandle);
    withStub(stub -> stub.closeResource(resourceHandle));
  }

  void removeResource(HandleId resourceHandle) throws XMLDBException {
    LOGGER.debug("removeResource({})", resourceHandle);
    withStub(stub -> stub.removeResource(resourceHandle));
  }

  String createId(HandleId collectionHandle) throws XMLDBException {
    LOGGER.debug("createId({})", collectionHandle);
    return withStub(stub -> stub.createId(collectionHandle)).getResourceId();
  }

  Iterator<ResourceData> loadResource(ResourceLoadRequest request) throws XMLDBException {
    LOGGER.info("loadResource({})", request);
    return withStub(stub -> new ClientCallIterator<>(stub.loadResourceData(request)));
  }

  void storeResource(HandleId resourceHandle, StreamConsumer resourceConsumer)
      throws XMLDBException {
    LOGGER.info("storeResource({})", resourceHandle);
    withStub(stub -> {

      return null;
    });
  }
}
