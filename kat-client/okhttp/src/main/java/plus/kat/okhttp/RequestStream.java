/*
 * Copyright 2022 Kat+ Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package plus.kat.okhttp;

import plus.kat.*;
import plus.kat.stream.*;

import okio.BufferedSink;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.io.IOException;

import static plus.kat.okhttp.MediaTypes.of;

/**
 * @author kraity
 * @since 0.0.3
 */
public class RequestStream extends RequestBody {

    protected final byte[] data;
    protected final MediaType mediaType;

    public RequestStream(
        Chan chan
    ) throws IOException {
        this(
            chan, of(chan.algo())
        );
    }

    public RequestStream(
        Chan chan,
        MediaType mediaType
    ) throws IOException {
        try (Chan in = chan) {
            this.data = in.toBytes();
            this.mediaType = mediaType;
        }
    }

    public RequestStream(
        Stream stream
    ) {
        this(
            stream, of(stream.algo())
        );
    }

    public RequestStream(
        Stream stream,
        MediaType mediaType
    ) {
        try (Stream in = stream) {
            this.data = in.toBytes();
            this.mediaType = mediaType;
        }
    }

    @Override
    public void writeTo(
        BufferedSink sink
    ) throws IOException {
        sink.write(data);
    }

    @Override
    public long contentLength() {
        return data.length;
    }

    @Override
    public MediaType contentType() {
        return mediaType;
    }
}
