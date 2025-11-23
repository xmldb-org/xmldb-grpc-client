/*
 * Licensed under the Apache License, Version 2.0 (the "License") you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.xmldb.remote.client;

import static org.xmldb.api.base.ErrorCodes.NOT_IMPLEMENTED;

import java.io.OutputStream;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.grpc.ResourceMeta;

public abstract class RemoteBaseResource<R> implements Resource<R> {
  private final String id;
  private final AtomicBoolean open;
  private final ResourceMeta resourceMeta;
  private final RemoteCollection parentCollection;


  protected RemoteBaseResource(String id, ResourceMeta resourceMeta,
      RemoteCollection parentCollection) {
    this.id = id;
    this.open = new AtomicBoolean(true);
    this.resourceMeta = resourceMeta;
    this.parentCollection = parentCollection;
  }

  @Override
  public Collection getParentCollection() {
    return parentCollection;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void getContentAsStream(OutputStream stream) throws XMLDBException {
    throw new XMLDBException(NOT_IMPLEMENTED);
  }

  @Override
  public boolean isClosed() {
    return !open.get();
  }

  @Override
  public void close() throws XMLDBException {
    if (open.compareAndSet(true, false)) {
      parentCollection
          .call(remoteClient -> remoteClient.closeResource(resourceMeta.getResourceId()));
    }
  }

  @Override
  public Instant getCreationTime() {
    return Instant.ofEpochMilli(resourceMeta.getCreationTime());
  }

  @Override
  public Instant getLastModificationTime() {
    return Instant.ofEpochMilli(resourceMeta.getLastModificationTime());
  }
}
