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

import java.io.*;
import java.net.*;
import java.net.Proxy;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author kraity
 * @since 0.0.3
 */
public class Client extends Caller {

    protected int code;
    protected HttpURLConnection conn;

    /**
     * Inherit and expand
     *
     * @since 0.0.4
     */
    protected Client() {
        super(Memory.INS);
    }

    /**
     * @param url the url to parse as a URL
     * @throws IOException           If an I/O error occurs
     * @throws MalformedURLException If no protocol is specified, or an unknown protocol is found, or spec is null
     */
    public Client(
        @NotNull String url
    ) throws IOException {
        this(
            new URL(url)
        );
    }

    /**
     * @throws IOException If an I/O error occurs
     * @since 0.0.4
     */
    public Client(
        @NotNull Query query
    ) throws IOException {
        this(
            query.toUrl()
        );
    }

    /**
     * @throws IOException If an I/O error occurs
     */
    public Client(
        @NotNull URL url
    ) throws IOException {
        this(
            url.openConnection()
        );
    }

    /**
     * @param proxy the specified proxy
     * @throws IOException If an I/O error occurs
     */
    public Client(
        @NotNull URL url,
        @NotNull Proxy proxy
    ) throws IOException {
        this(
            url.openConnection(proxy)
        );
    }

    /**
     * @param url   the url to parse as a URL
     * @param proxy the specified proxy
     * @throws IOException           If an I/O error occurs
     * @throws MalformedURLException If no protocol is specified, or an unknown protocol is found, or spec is null
     */
    public Client(
        @NotNull String url,
        @NotNull Proxy proxy
    ) throws IOException {
        this(
            new URL(url).openConnection(proxy)
        );
    }

    /**
     * @param conn the specified {@link URLConnection}
     * @throws IOException If an I/O error occurs
     */
    public Client(
        @NotNull URLConnection conn
    ) throws IOException {
        super(Memory.INS);
        if (conn instanceof HttpURLConnection) {
            this.conn = (HttpURLConnection) conn;
        } else {
            throw new IOException(
                conn + " is not an HttpURLConnection"
            );
        }
    }

    /**
     * @param conn the specified {@link HttpURLConnection}
     * @throws IOException If an I/O error occurs
     */
    public Client(
        @NotNull HttpURLConnection conn
    ) throws IOException {
        super(Memory.INS);
        if (conn != null) {
            this.conn = conn;
            this.supplier = Supplier.ins();
        } else {
            throw new IOException(
                "HttpURLConnection must not be null"
            );
        }
    }

    /**
     * Returns the value of the response header field
     *
     * @param key the name of a header field
     * @return {@link String} or {@code null}
     * @see Client#head(String)
     * @since 0.0.4
     */
    public String get(
        @NotNull String key
    ) {
        return head(key);
    }

    /**
     * Sets the general request property
     *
     * @see Client#header(String, Object)
     * @since 0.0.4
     */
    public Client set(
        @NotNull String key,
        @Nullable Object value
    ) {
        return header(
            key, value
        );
    }

    /**
     * Returns the code of {@code status}
     *
     * @see Client#resolve(URLConnection)
     * @see HttpURLConnection#getResponseCode()
     */
    public int code() {
        return code;
    }

    /**
     * Returns the value for the {@code n}<sup>th</sup> header field
     *
     * @param n an index, where {@code n>=0}
     * @return {@link String} or {@code null}
     * @see HttpURLConnection#getHeaderField(int)
     */
    @Nullable
    public String head(int n) {
        return conn.getHeaderField(n);
    }

    /**
     * Returns the value of the response header field
     *
     * @param key the name of a header field
     * @return {@link String} or {@code null}
     * @see HttpURLConnection#getHeaderField(String)
     */
    @Nullable
    public String head(
        @NotNull String key
    ) {
        return conn.getHeaderField(key);
    }

    /**
     * Sets a specified read timeout value, in milliseconds
     *
     * @see URLConnection#setReadTimeout(int)
     */
    public Client readout(
        int timeout
    ) {
        conn.setReadTimeout(timeout);
        return this;
    }

    /**
     * Sets a specified connect timeout value, in milliseconds
     *
     * @see URLConnection#setConnectTimeout(int)
     */
    public Client timeout(
        int timeout
    ) {
        conn.setConnectTimeout(timeout);
        return this;
    }

    /**
     * Sets the value of the {@code useCaches} field
     *
     * @see URLConnection#setUseCaches(boolean)
     */
    public Client cache(
        boolean status
    ) {
        conn.setUseCaches(status);
        return this;
    }

    /**
     * Sets whether HTTP redirects (code 3xx) should be automatically followed
     *
     * @see HttpURLConnection#setInstanceFollowRedirects(boolean)
     */
    public Client redirect(
        boolean status
    ) {
        conn.setInstanceFollowRedirects(status);
        return this;
    }

    /**
     * Sets the request property of {@code User-Agent}
     *
     * @see HttpURLConnection#setRequestProperty(String, String)
     */
    public Client agent(
        @Nullable String value
    ) {
        conn.setRequestProperty(
            "User-Agent", value
        );
        return this;
    }

    /**
     * Sets the request property of {@code Referer}
     *
     * @see HttpURLConnection#setRequestProperty(String, String)
     */
    public Client referer(
        @Nullable String value
    ) {
        conn.setRequestProperty(
            "Referer", value
        );
        return this;
    }

    /**
     * Returns the value of the named general request property for this connection
     *
     * @return {@link String} or {@code null}
     * @see URLConnection#getRequestProperty(String)
     */
    @Nullable
    public String header(
        @NotNull String key
    ) {
        return conn.getRequestProperty(key);
    }

    /**
     * Sets the general request property
     *
     * @see HttpURLConnection#setRequestProperty(String, String)
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
     * Sets the request property of {@code Accept}
     *
     * @see HttpURLConnection#setRequestProperty(String, String)
     */
    public Client accept(
        @Nullable String value
    ) {
        conn.setRequestProperty(
            "Accept", value
        );
        return this;
    }

    /**
     * Sets the request property of {@code Accept}
     *
     * @see Client#accept(String)
     */
    public Client accept(
        @NotNull Algo algo
    ) {
        this.algo = algo;
        return contentType(algo);
    }

    /**
     * Returns the value of the {@code Content-Type} header field
     *
     * @return {@link String} or {@code null}
     * @see URLConnection#getHeaderField(String)
     */
    @Nullable
    public String contentType() {
        return conn.getHeaderField(
            "Content-Type"
        );
    }

    /**
     * Sets the request property of {@code Content-Type}
     *
     * @see HttpURLConnection#setRequestProperty(String, String)
     */
    public Client contentType(
        @Nullable String value
    ) {
        conn.setRequestProperty(
            "Content-Type", value
        );
        return this;
    }

    /**
     * Sets the request property of {@code Content-Type}
     *
     * @see Client#contentType(String)
     */
    public Client contentType(
        @NotNull Algo algo
    ) {
        switch (algo.name()) {
            case "kat": {
                return contentType(
                    "application/kat; charset=utf-8"
                );
            }
            case "xml": {
                return contentType(
                    "application/xml; charset=utf-8"
                );
            }
            case "json": {
                return contentType(
                    "application/json; charset=utf-8"
                );
            }
            default: {
                throw new FatalCrash(
                    "Unexpectedly, Client does not support " + algo.name()
                );
            }
        }
    }

    /**
     * Returns the specified {@link Algo}
     *
     * @throws FatalCrash If the specified algo is null
     */
    @Override
    public Algo algo() {
        Algo a = algo;
        if (a != null) {
            return a;
        }

        String type = contentType();
        if (type == null ||
            type.length() < 7) {
            throw new FatalCrash(
                "The content type(" + type + ") is illegal"
            );
        }

        char c = type.charAt(0);
        // text
        if (c == 't') {
            if (type.startsWith("text/kat")) {
                return Algo.KAT;
            }

            if (type.startsWith("text/xml")) {
                return Algo.DOC;
            }
        }

        // application
        else if (c == 'a') {
            if (type.startsWith("application/json")) {
                return Algo.JSON;
            }

            if (type.startsWith("application/kat")) {
                return Algo.KAT;
            }

            if (type.startsWith("application/xml")) {
                return Algo.DOC;
            }
        }

        throw new FatalCrash(
            "Could not find the specified Algo of " + type
        );
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
        try {
            // connect
            conn.connect();
            // resolve
            resolve(conn);
        } finally {
            // disconnect
            conn.disconnect();
        }
        return this;
    }

    /**
     * @throws IOException If an I/O error occurs
     * @since 0.0.4
     */
    public Client post()
        throws IOException {
        return send("POST");
    }

    /**
     * <pre>{@code
     *  new Client("https://kat.plus/test/user").view();
     * }</pre>
     *
     * @throws IOException If an I/O error occurs
     */
    public Client view()
        throws IOException {
        return send("HEAD");
    }

    /**
     * <pre>{@code
     *  new Client("https://kat.plus/test/del-user").delete();
     * }</pre>
     *
     * @throws IOException If an I/O error occurs
     */
    public Client delete()
        throws IOException {
        return send("DELETE");
    }

    /**
     * <pre>{@code
     *  String url = "https://kat.plus/test/add-user";
     *  String data = "{:id(1):name(kraity)}";
     *  User user = new Client(url).put(data).to(User.class);
     * }</pre>
     *
     * @param data the specified content
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the {@code data} is null
     */
    public Client put(
        String data
    ) throws IOException {
        return put(
            data.getBytes(UTF_8)
        );
    }

    /**
     * @param data the specified content
     * @throws IOException If an I/O error occurs
     */
    public Client put(
        byte[] data
    ) throws IOException {
        return request(
            "PUT", data, 0, data.length
        );
    }

    /**
     * @param data the specified content
     * @throws IOException If an I/O error occurs
     */
    public Client put(
        byte[] data, int i, int l
    ) throws IOException {
        check(data, i, l);
        return request(
            "PUT", data, i, l
        );
    }

    /**
     * @param alpha the specified alpha
     * @throws IOException If an I/O error occurs
     * @since 0.0.4
     */
    public Client put(
        Alpha alpha
    ) throws IOException {
        return request(
            "PUT", alpha
        );
    }

    /**
     * @param chan the specified chan
     * @throws IOException If an I/O error occurs
     */
    public Client put(
        Chan chan
    ) throws IOException {
        return put(
            chan.getSteam()
        );
    }

    /**
     * @param flow the specified paper
     * @throws IOException If an I/O error occurs
     */
    public Client put(
        Steam flow
    ) throws IOException {
        try {
            contentType(
                flow.algo()
            );
            return request(
                "PUT", flow
            );
        } finally {
            flow.close();
        }
    }

    /**
     * <pre>{@code
     *  String url = "https://kat.plus/test/add-user";
     *  String data = "{:id(1):name(kraity)}";
     *  User user = new Client(url).post(data).to(User.class);
     * }</pre>
     *
     * @param data the specified content
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the {@code data} is null
     */
    public Client post(
        String data
    ) throws IOException {
        return post(
            data.getBytes(UTF_8)
        );
    }

    /**
     * @param alpha the specified alpha
     * @throws IOException If an I/O error occurs
     * @since 0.0.4
     */
    public Client post(
        Alpha alpha
    ) throws IOException {
        return request(
            "POST", alpha
        );
    }

    /**
     * <pre>{@code
     *  String url = "https://kat.plus/test/add-user";
     *  Chan chan = ...;
     *  User user = new Client(url).post(chan).to(User.class);
     *
     *  User user = new Client(url)
     *      .post(Sugar.Json(c -> {
     *           c.set("id", 1);
     *           c.set("name", "kraity");
     *       })).to(User.class);
     * }</pre>
     *
     * @param chan the specified chan
     * @throws IOException If an I/O error occurs
     */
    public Client post(
        Chan chan
    ) throws IOException {
        return post(
            chan.getSteam()
        );
    }

    /**
     * @param flow the specified paper
     * @throws IOException If an I/O error occurs
     */
    public Client post(
        Steam flow
    ) throws IOException {
        try {
            contentType(
                flow.algo()
            );
            return request(
                "POST", flow
            );
        } finally {
            flow.close();
        }
    }

    /**
     * @param data the specified content
     * @throws IOException If an I/O error occurs
     */
    public Client post(
        byte[] data
    ) throws IOException {
        return request(
            "POST", data, 0, data.length
        );
    }

    /**
     * @param data the specified content
     * @throws IOException If an I/O error occurs
     */
    public Client post(
        byte[] data, int i, int l
    ) throws IOException {
        check(data, i, l);
        return request(
            "POST", data, i, l
        );
    }

    /**
     * @throws IOException If an I/O error occurs
     * @since 0.0.4
     */
    public Client send(
        @NotNull String method
    ) throws IOException {
        try {
            // method
            conn.setRequestMethod(method);
            // connect
            conn.connect();
            // resolve
            resolve(conn);
        } finally {
            // disconnect
            conn.disconnect();
        }
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
                throw new IOException();
            }
            if (crash instanceof IOException) {
                throw (IOException) crash;
            } else {
                throw (RuntimeException) crash;
            }
        }

        if (!status.startsWith("HTTP/1.")) {
            throw new IOException(
                "Not support status(" + status + ") currently"
            );
        }

        int index = status.indexOf(' ');
        if (index < 0) {
            throw new IOException(
                "Response status(" + status + ") is incomplete"
            );
        }

        int offset = status
            .indexOf(
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
                throw new IOException(
                    "Status(" + status + ": '" + (char) dig + "')"
                );
            }
            num *= 10;
            if (num < lim + dig) {
                throw new IOException(
                    "Status(" + status + ": " + -num + ")  is out of range"
                );
            }
            num -= dig;
        }

        this.code = -num;
        if (isSuccess()) {
            if (in != null) {
                stream(in);
            }
        } else {
            throw new IOException(
                "Unexpectedly, code: " + code + ", message:" + status.substring(offset)
            );
        }
    }

    /**
     * @param alpha  the specified alpha
     * @param method the specified method
     * @throws IOException If an I/O error occurs
     */
    protected Client request(
        String method, Alpha alpha
    ) throws IOException {
        try {
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod(method);

            // connect
            conn.connect();

            if (alpha != null) {
                OutputStream out;
                alpha.each(
                    out = conn.getOutputStream()
                );
                out.close();
            }

            // resolve
            resolve(conn);
        } finally {
            // disconnect
            conn.disconnect();
        }
        return this;
    }

    /**
     * @param method the specified method
     * @param data   the specified content
     * @throws IOException If an I/O error occurs
     */
    protected Client request(
        String method, byte[] data, int i, int l
    ) throws IOException {
        try {
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod(method);

            // connect
            conn.connect();

            if (data != null) {
                OutputStream out =
                    conn.getOutputStream();
                out.write(
                    data, i, l
                );
                out.close();
            }

            // resolve
            resolve(conn);
        } finally {
            // disconnect
            conn.disconnect();
        }
        return this;
    }
}
