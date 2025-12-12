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

import static org.xmldb.api.base.ErrorCodes.VENDOR_ERROR;
import static org.xmldb.remote.client.Constants.DEFAULT_BUFFER_SIZE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.grpc.ResourceData;
import org.xmldb.api.grpc.ResourceLoadRequest;
import org.xmldb.api.grpc.ResourceMeta;

/**
 * Represents a base class for remote resources, providing common functionality for resources
 * managed in a remote resource collection. This class is parameterized to handle specific content
 * types defined by its concrete subclass.
 *
 * @param <R> The type of the resource content handled by the subclass.
 */
public abstract class RemoteBaseResource<R> implements Resource<R> {
  private final String id;
  private final AtomicBoolean open;
  private final ResourceMeta resourceMeta;
  private final RemoteCollection parentCollection;

  private Instant lastModification;
  private byte[] content;

  /**
   * Initializes a new instance of the {@code RemoteBaseResource} class.
   *
   * @param id the resource ID
   * @param resourceMeta the metadata associated with the resource
   * @param parentCollection the parent collection containing this resource
   */
  protected RemoteBaseResource(String id, ResourceMeta resourceMeta,
      RemoteCollection parentCollection) {
    this.id = id;
    this.open = new AtomicBoolean(true);
    this.resourceMeta = resourceMeta;
    this.parentCollection = parentCollection;
    lastModification = Instant.ofEpochMilli(resourceMeta.getLastModificationTime());
  }

  /**
   * Retrieves the metadata associated with the resource.
   *
   * @return the {@code ResourceMeta} object containing metadata of this resource
   */
  protected final ResourceMeta getResourceMeta() {
    return resourceMeta;
  }

  /**
   * Sets the last modification time for the resource.
   *
   * @param lastModification the {@code Instant} representing the new last modification time of the
   *        resource
   */
  protected final void setLastModification(Instant lastModification) {
    this.lastModification = lastModification;
  }

  /**
   * Sets the content of the resource using the provided input stream. The content is read from the
   * input stream and stored internally as a byte array.
   *
   * @param content the {@code InputStream} representing the new content to be set for the resource
   * @throws XMLDBException if an error occurs while reading or processing the input stream
   */
  protected final void setContent(InputStream content) throws XMLDBException {
    try (content; ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      content.transferTo(outputStream);
      this.content = outputStream.toByteArray();
    } catch (IOException e) {
      throw new XMLDBException(VENDOR_ERROR, e);
    }
  }

  @Override
  public final Collection getParentCollection() {
    return parentCollection;
  }

  @Override
  public final String getId() {
    return id;
  }

  @Override
  public final void getContentAsStream(OutputStream stream) throws XMLDBException {
    try {
      if (content == null) {
        // TODO: buffer locally to only download once?
        loadContent(stream);
      } else {
        stream.write(content);
      }
    } catch (IOException e) {
      throw new XMLDBException(VENDOR_ERROR, e);
    }
  }

  private void loadContent(OutputStream stream) throws XMLDBException {
    parentCollection.call(client -> {
      final ResourceLoadRequest request =
          ResourceLoadRequest.newBuilder().setResourceId(getResourceMeta().getResourceId())
              .setChunkSize(DEFAULT_BUFFER_SIZE).build();
      for (Iterator<ResourceData> resourceDataIterator =
          client.loadResource(request); resourceDataIterator.hasNext();) {
        try {
          stream.write(resourceDataIterator.next().getDataChunk().toByteArray());
        } catch (IOException e) {
          throw new XMLDBException(VENDOR_ERROR, e);
        }
      }
    });
  }

  @Override
  public final boolean isClosed() {
    return !open.get();
  }

  @Override
  public final void close() throws XMLDBException {
    if (open.compareAndSet(true, false)) {
      parentCollection
          .call(remoteClient -> remoteClient.closeResource(resourceMeta.getResourceId()));
    }
  }

  @Override
  public final Instant getCreationTime() {
    return Instant.ofEpochMilli(resourceMeta.getCreationTime());
  }

  @Override
  public final Instant getLastModificationTime() {
    return lastModification;
  }
}
