package no.kristiania.pgr203;

import java.io.IOException;
import java.io.OutputStream;

public class HttpServerRedirectResponse implements HttpServerResponse {
    private String location;

    public HttpServerRedirectResponse(String location) {
        this.location = location;
    }

    @Override
    public void write(OutputStream outputStream, String authority) throws IOException {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Location", "http://" + authority + location);
        outputStream.write("HTTP/1.1 302 Redirect\r\n".getBytes());
        responseHeaders.write(outputStream);
        outputStream.write("\r\n".getBytes());
        outputStream.flush();

    }
}
