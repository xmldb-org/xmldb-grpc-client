/*
 * Licensed under the Apache License, Version 2.0 (the "License") you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package org.xmldb.remote.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.base.ErrorCodes;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.grpc.ChildCollectionName;
import org.xmldb.api.grpc.CollectionMeta;
import org.xmldb.api.grpc.Count;
import org.xmldb.api.grpc.Empty;
import org.xmldb.api.grpc.HandleId;
import org.xmldb.api.grpc.ResourceId;
import org.xmldb.api.grpc.ResourceMeta;
import org.xmldb.api.grpc.RootCollectionName;
import org.xmldb.api.grpc.SystemInfo;
import org.xmldb.api.grpc.XmlDbServiceGrpc;

import io.grpc.CallCredentials;
import io.grpc.Channel;
import io.grpc.StatusRuntimeException;

public final class RemoteClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(RemoteClient.class);
  private static final Empty EMPTY = Empty.getDefaultInstance();

  private final XmlDbServiceGrpc.XmlDbServiceBlockingStub blockingStub;

  /**
   * Initializes a new instance of the {@code RemoteClient} class with the specified gRPC channel
   * and call credentials.
   *
   * @param channel the gRPC channel to communicate with the server
   * @param callCredentials the call credentials for the gRPC communication
   */
  RemoteClient(final Channel channel, final CallCredentials callCredentials) {
    blockingStub = XmlDbServiceGrpc.newBlockingStub(channel).withCallCredentials(callCredentials);
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
    return new RemoteClient(connectionInfo.openChannel(),
        new AuthenticationCredentials(connectionInfo::authentication));
  }

  <T> T withStub(Function<XmlDbServiceGrpc.XmlDbServiceBlockingStub, T> action)
      throws XMLDBException {
    try {
      return action.apply(blockingStub);
    } catch (StatusRuntimeException e) {
      throw handleStatusException(e);
    }
  }

  private static XMLDBException handleStatusException(StatusRuntimeException e) {
    LOGGER.info("RPC failed: {}", e.getStatus());
    return new XMLDBException(ErrorCodes.VENDOR_ERROR, e.getStatus().getDescription(), e);
  }

  /**
   * version from server.
   */
  public SystemInfo systemInfo() throws XMLDBException {
    LOGGER.debug("systemInfo()");
    return withStub(stub -> stub.systemInfo(EMPTY));
  }

  public CollectionMeta openRootCollection(String uri, Properties info) throws XMLDBException {
    LOGGER.debug("openRootCollection({}, {}})", uri, info);
    return withStub(stub -> {
      final Map<String, String> infoMap = new HashMap<>();
      info.forEach((key, value) -> infoMap.put(String.valueOf(key), String.valueOf(value)));
      return stub.openRootCollection(
          RootCollectionName.newBuilder().setUri(uri).putAllInfo(infoMap).build());
    });
  }

  public CollectionMeta openChildCollection(HandleId collectionHandle, String collectionName)
      throws XMLDBException {
    LOGGER.debug("openChildCollection({}, {}})", collectionHandle, collectionName);
    return withStub(stub -> stub.openChildCollection(ChildCollectionName.newBuilder()
        .setCollectionId(collectionHandle).setChildName(collectionName).build()));
  }

  public Count resourceCount(HandleId collectionHandle) throws XMLDBException {
    LOGGER.debug("resourceCount({})", collectionHandle);
    return withStub(stub -> stub.resourceCount(collectionHandle));
  }

  public Iterator<ResourceId> listResources(HandleId collectionHandle) throws XMLDBException {
    LOGGER.debug("listResources({})", collectionHandle);
    return withStub(stub -> stub.listResources(collectionHandle));
  }

  public Count collectionCount(HandleId collectionHandle) throws XMLDBException {
    LOGGER.debug("collectionCount({})", collectionHandle);
    return withStub(stub -> stub.collectionCount(collectionHandle));
  }

  public Iterator<ChildCollectionName> childCollections(HandleId collectionHandle)
      throws XMLDBException {
    LOGGER.debug("childCollections({})", collectionHandle);
    return withStub(stub -> stub.childCollections(collectionHandle));
  }

  public void closeCollection(HandleId collectionHandle) throws XMLDBException {
    LOGGER.debug("closeCollection({})", collectionHandle);
    withStub(stub -> stub.closeCollection(collectionHandle));
  }

  public ResourceMeta openResource(HandleId collectionHandle, String resourceId)
      throws XMLDBException {
    LOGGER.debug("openResource({}, {})", collectionHandle, resourceId);
    return withStub(stub -> stub.openResource(ResourceId.newBuilder()
        .setCollectionId(collectionHandle).setResourceId(resourceId).build()));
  }

  public void closeResource(HandleId resourceHandle) throws XMLDBException {
    LOGGER.debug("closeResource({})", resourceHandle);
    withStub(stub -> stub.closeResource(resourceHandle));
  }
}
