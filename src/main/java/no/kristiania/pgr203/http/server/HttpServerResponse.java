package no.kristiania.pgr203.http.server;

import java.io.IOException;
import java.io.OutputStream;

public interface HttpServerResponse {
    void write(OutputStream outputStream, String authority) throws IOException;
}
