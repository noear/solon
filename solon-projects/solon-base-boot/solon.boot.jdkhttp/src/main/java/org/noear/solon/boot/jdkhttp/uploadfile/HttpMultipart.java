package org.noear.solon.boot.jdkhttp.uploadfile;

import org.noear.solon.boot.ServerProps;

import java.io.IOException;
import java.io.InputStream;

public class HttpMultipart {
    public String name;
    public String filename;
    public HttpHeaderCollection headers;
    public InputStream body;

    public String getName() { return name; }

    public String getFilename() { return filename; }

    public HttpHeaderCollection getHeaders() { return headers; }

    public InputStream getBody() { return body; }

    public String getString() throws IOException {
        String charset = headers.getParams("Content-Type").get("charset");
        return Utils.readToken(body, -1, charset == null ? ServerProps.request_encoding : charset, 8192);
    }
}