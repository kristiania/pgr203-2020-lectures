package no.kristiania.pgr203.http.server;

import no.kristiania.pgr203.http.HttpHeaders;

import java.io.IOException;
import java.io.OutputStream;

public class HttpContentResponse implements HttpServerResponse {
    private String content;

    public HttpContentResponse(String content) {
        this.content = content;
    }

    @Override
    public void write(OutputStream outputStream, String authority) throws IOException {
        outputStream.write("HTTP/1.1 200 OK\r\n".getBytes());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentLength(content.length());
        responseHeaders.add("Connection", "close");
        responseHeaders.write(outputStream);

        outputStream.write("\r\n".getBytes());
        outputStream.write(content.getBytes());
        outputStream.flush();

    }
}
