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
package plus.kat.caller;

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.kernel.*;
import plus.kat.stream.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author kraity
 * @since 0.0.3
 */
public class Client extends Chain {

    protected int code;
    protected Supplier supplier;
    protected HttpURLConnection conn;

    public Client(
        @NotNull String url
    ) throws IOException {
        this(
            new URL(url)
        );
    }

    public Client(
        @NotNull URL url
    ) throws IOException {
        this(
            (HttpURLConnection) url.openConnection()
        );
    }

    public Client(
        @NotNull HttpURLConnection conn
    ) throws IOException {
        super(Buffer.INS);
        if (conn != null) {
            this.conn = conn;
            this.supplier = Supplier.ins();
        } else {
            throw new UnexpectedCrash(
                "HttpURLConnection must not be null"
            );
        }
    }

    public Client with(
        @NotNull Supplier target
    ) {
        if (target != null) {
            supplier = target;
        } else {
            throw new NullPointerException(
                "Supplier must not be null"
            );
        }
        return this;
    }

    @Nullable
    public <E, T extends E> T to(
        @NotNull Class<E> klass
    ) {
        if (count == 0) {
            return null;
        }

        String type = head(
            "content-type"
        );
        if (type == null ||
            type.length() < 7) {
            throw new RunCrash(
                "The content type(" + type + ") is illegal"
            );
        }

        Job job = null;
        switch (type.charAt(0)) {
            // text
            case 't': {
                if (type.startsWith("text/kat")) {
                    job = Job.KAT;
                } else if (type.startsWith("text/xml")) {
                    job = Job.DOC;
                }
                break;
            }
            // application
            case 'a': {
                if (type.startsWith("application/json")) {
                    job = Job.JSON;
                } else if (type.startsWith("application/kat")) {
                    job = Job.KAT;
                } else if (type.startsWith("application/xml")) {
                    job = Job.DOC;
                }
                break;
            }
        }

        if (job == null) {
            throw new RunCrash(
                "Cannot find " + type + "'s Job"
            );
        }

        return supplier.solve(
            klass, job, new Event<>(
                new ByteReader(
                    value, 0, count
                )
            )
        );
    }

    @Nullable
    public <E, T extends E> T to(
        @NotNull Job job,
        @NotNull Class<E> klass
    ) {
        if (count == 0) {
            return null;
        }
        return supplier.solve(
            klass, job, new Event<>(
                new ByteReader(
                    value, 0, count
                )
            )
        );
    }

    @Override
    public Value subSequence(
        int start, int end
    ) {
        return new Value(
            copyBytes(start, end)
        );
    }

    /**
     * Returns the code of {@code status}
     */
    public int code() {
        return code;
    }

    /**
     * Returns the value for the {@code n}<sup>th</sup> header field
     *
     * @param n an index, where {@code n>=0}
     * @return {@link String} or {@code null}
     */
    public String head(
        int n
    ) {
        return conn.getHeaderField(n);
    }

    /**
     * Returns the value of the named header field
     *
     * @param key the name of a header field
     * @return {@link String} or {@code null}
     */
    public String head(
        @NotNull String key
    ) {
        return conn.getHeaderField(key);
    }

    /**
     * Set the method for the URL request
     */
    public Client method(
        @NotNull String method
    ) throws ProtocolException {
        conn.setRequestMethod(method);
        return this;
    }

    /**
     * Sets a specified timeout value, in milliseconds
     */
    public Client timeout(
        int timeout
    ) {
        conn.setConnectTimeout(timeout);
        return this;
    }

    /**
     * Sets the request property of {@code user-agent}
     */
    public Client agent(
        @Nullable String value
    ) {
        conn.setRequestProperty(
            "user-agent", value
        );
        return this;
    }

    /**
     * Sets the request property of {@code referer}
     */
    public Client referer(
        @Nullable String value
    ) {
        conn.setRequestProperty(
            "referer", value
        );
        return this;
    }

    /**
     * Sets the general request property
     */
    public Client header(
        @NotNull String key,
        @Nullable String value
    ) {
        conn.setRequestProperty(
            key, value
        );
        return this;
    }

    /**
     * Sets the general request property
     */
    public Client header(
        @NotNull String key,
        @Nullable Object value
    ) {
        conn.setRequestProperty(
            key, value.toString()
        );
        return this;
    }

    /**
     * Sets the request property of {@code content-type}
     */
    public Client contentType(
        @NotNull Job job
    ) {
        String type;
        switch (job) {
            case KAT: {
                type = "application/kat";
                break;
            }
            case DOC: {
                type = "application/xml";
                break;
            }
            case JSON: {
                type = "application/json";
                break;
            }
            default: {
                throw new RunCrash(
                    "Unexpectedly, Client did not find " + job
                );
            }
        }
        conn.setRequestProperty(
            "content-type", type
        );
        return this;
    }

    /**
     * Sets the request property of {@code content-type}
     */
    public Client contentType(
        @Nullable String value
    ) {
        conn.setRequestProperty(
            "content-type", value
        );
        return this;
    }

    /**
     * Returns {@code true} if and only if {@code code} in {@code [100,200)}
     */
    public boolean isInfo() {
        return 100 <= code && code < 200;
    }

    /**
     * Returns {@code true} if and only if {@code code} in {@code [200,300)}
     */
    public boolean isSuccess() {
        return 200 <= code && code < 300;
    }

    /**
     * Returns {@code true} if and only if {@code code} in {@code [300,400)}
     */
    public boolean isRedirect() {
        return 300 <= code && code < 400;
    }

    /**
     * Returns {@code true} if and only if {@code code} in {@code [400,600)}
     */
    public boolean isError() {
        return 400 <= code && code < 600;
    }

    /**
     * Returns {@code true} if and only if {@code code} in {@code [400,500)}
     */
    public boolean isClientError() {
        return 400 <= code && code < 500;
    }

    /**
     * Returns {@code true} if and only if {@code code} in {@code [500,600)}
     */
    public boolean isServerError() {
        return 500 <= code && code < 600;
    }

    /**
     * <pre>{@code
     *  String url = "https://kat.plus/test/user.json";
     *  User user = new Client(url).get().to(User.class);
     * }</pre>
     *
     * @throws IOException If an I/O error occurs
     */
    public Client get()
        throws IOException {
        request("GET");
        return this;
    }

    /**
     * @param content the specified content
     * @throws IOException If an I/O error occurs
     */
    public Client put(
        byte[] content
    ) throws IOException {
        return send(
            "PUT", content
        );
    }

    /**
     * @param paper the specified paper
     * @throws IOException If an I/O error occurs
     */
    public Client put(
        Paper paper
    ) throws IOException {
        return send(
            "PUT", paper
        );
    }

    /**
     * @param content the specified content
     * @throws IOException If an I/O error occurs
     */
    public Client put(
        String content
    ) throws IOException {
        return send(
            "PUT", content
        );
    }

    /**
     * @param content the specified content
     * @throws IOException If an I/O error occurs
     */
    public Client post(
        byte[] content
    ) throws IOException {
        return send(
            "POST", content
        );
    }

    /**
     * <pre>{@code
     *  String url = "https://kat.plus/test/add-user";
     *  String data = "{:id(1):name(kraity)}";
     *  User user = new Client(url).post(data).to(User.class);
     * }</pre>
     *
     * @param content the specified content
     * @throws IOException If an I/O error occurs
     */
    public Client post(
        String content
    ) throws IOException {
        return send(
            "POST", content
        );
    }

    /**
     * <pre>{@code
     *  String url = "https://kat.plus/test/add-user";
     *  Chan chan = ...;
     *  User user = new Client(url).post(chan).to(User.class);
     * }</pre>
     *
     * @param chan the specified chan
     * @throws IOException If an I/O error occurs
     */
    public Client post(
        Chan chan
    ) throws IOException {
        return post(
            chan.getFlow()
        );
    }

    /**
     * @param paper the specified paper
     * @throws IOException If an I/O error occurs
     */
    public Client post(
        Paper paper
    ) throws IOException {
        return send(
            "POST", paper
        );
    }

    /**
     * @param content the specified content
     * @throws IOException If an I/O error occurs
     */
    public Client post(
        byte[] content, int offset, int length
    ) throws IOException {
        return send(
            "POST", content, offset, length
        );
    }

    /**
     * @param paper the specified paper
     * @throws IOException If an I/O error occurs
     */
    public Client send(
        String method, Paper paper
    ) throws IOException {
        try {
            contentType(
                paper.getJob()
            );
            request(
                method, paper
            );
        } finally {
            paper.close();
        }
        return this;
    }

    /**
     * @param content the specified content
     * @throws IOException If an I/O error occurs
     */
    public Client send(
        String method, String content
    ) throws IOException {
        if (content == null ||
            content.isEmpty()) {
            request(
                method, null, 0, 0
            );
        } else {
            byte[] data = content
                .getBytes(UTF_8);
            request(
                method, data, 0, data.length
            );
        }
        return this;
    }

    /**
     * @param data the specified content
     * @throws IOException If an I/O error occurs
     */
    public Client send(
        String method, byte[] data
    ) throws IOException {
        request(
            method, data, 0, data.length
        );
        return this;
    }

    /**
     * @param data the specified content
     * @throws IOException If an I/O error occurs
     */
    public Client send(
        String method, byte[] data, int i, int l
    ) throws IOException {
        check(data, i, l);
        request(
            method, data, i, l
        );
        return this;
    }

    /**
     * Check Bounds
     */
    protected void check(
        byte[] d, int i, int l
    ) {
        if (d == null || i < 0 ||
            l < 0 || i + l > d.length) {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * @param conn the specified {@link URLConnection}
     * @throws IOException If an I/O error occurs
     */
    protected void resolve(
        @NotNull URLConnection conn
    ) throws IOException {
        InputStream in = null;
        Exception crash = null;
        try {
            in = conn.getInputStream();
        } catch (Exception e) {
            crash = e;
        }

        String status =
            conn.getHeaderField(0);
        if (status == null) {
            if (crash == null) {
                code = -1;
                return;
            }
            if (crash instanceof IOException) {
                throw (IOException) crash;
            } else {
                throw (RuntimeException) crash;
            }
        }

        int index;
        if (!status.startsWith("HTTP/1.") ||
            (index = status.indexOf(' ')) <= 0) {
            code = -1;
            return;
        }

        int offset = status.indexOf(
            ' ', ++index
        );
        if (offset < 0) {
            offset = status.length();
        }

        int num = 0;
        int lim = -Integer.MAX_VALUE;
        int mul = lim / 10;

        while (index < offset) {
            int dig = status
                .charAt(
                    index++
                );
            if (dig < 58) {
                dig -= 48;
            } else if (dig < 91) {
                dig -= 55;
            } else {
                dig -= 87;
            }
            if (dig < 0 ||
                num < mul ||
                dig >= 10) {
                code = -1;
                return;
            }
            num *= 10;
            if (num < lim + dig) {
                code = -1;
                return;
            }
            num -= dig;
        }
        this.code = -num;

        if (in != null && isSuccess()) {
            byte[] data;
            try {
                chain(in, 1024);
                data = copyBytes();
            } catch (Exception e) {
                data = EMPTY_BYTES;
            } finally {
                try {
                    close();
                    in.close();
                } catch (Exception e) {
                    // Nothing
                }
            }

            value = data;
            count = data.length;
        }
    }

    /**
     * @param method the specified method
     * @throws IOException If an I/O error occurs
     */
    protected void request(
        String method
    ) throws IOException {
        conn.setRequestMethod(method);
        conn.connect();
        resolve(conn);
        conn.disconnect();
    }

    /**
     * @param method the specified method
     * @param chain  the specified chain
     * @throws IOException If an I/O error occurs
     */
    protected void request(
        String method, Chain chain
    ) throws IOException {
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod(method);

        // connect
        conn.connect();

        if (chain != null) {
            chain.update(
                conn.getOutputStream()
            );
        }

        // resolve
        resolve(conn);

        // disconnect
        conn.disconnect();
    }

    /**
     * @param method the specified method
     * @param data   the specified content
     * @throws IOException If an I/O error occurs
     */
    protected void request(
        String method, byte[] data, int i, int l
    ) throws IOException {
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod(method);

        // connect
        conn.connect();

        if (data != null) {
            OutputStream out = conn.getOutputStream();
            out.write(
                data, i, l
            );
        }

        // resolve
        resolve(conn);

        // disconnect
        conn.disconnect();
    }
}
