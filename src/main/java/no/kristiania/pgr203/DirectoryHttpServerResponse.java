package no.kristiania.pgr203;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirectoryHttpServerResponse implements HttpServerResponse {
    private Path path;

    public DirectoryHttpServerResponse(Path path) {
        this.path = path;
    }

    @Override
    public void write(OutputStream outputStream, String authority) throws IOException {
        outputStream.write("HTTP/1.1 200 OK\r\n".getBytes());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentLength(Files.size(path));
        responseHeaders.add("Connection", "close");
        responseHeaders.write(outputStream);

        outputStream.write("\r\n".getBytes());
        Files.copy(path, outputStream);
        outputStream.flush();
    }
}
