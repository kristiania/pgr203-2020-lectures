package no.kristiania.pgr203;

import java.io.IOException;
import java.io.OutputStream;

public class HttpNotFoundResponse implements HttpServerResponse {
    @Override
    public void write(OutputStream outputStream, String authority) throws IOException {
        HttpHeaders responseHeaders = new HttpHeaders();
        outputStream.write("HTTP/1.1 404 Not found\r\n".getBytes());

        responseHeaders.setContentLength(0);
        responseHeaders.add("Connection", "close");
        responseHeaders.write(outputStream);

        outputStream.write("\r\n".getBytes());
        outputStream.flush();
    }
}
