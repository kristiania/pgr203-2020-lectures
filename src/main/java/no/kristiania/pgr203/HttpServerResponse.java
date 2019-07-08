package no.kristiania.pgr203;

import java.io.IOException;
import java.io.OutputStream;

interface HttpServerResponse {
    void write(OutputStream outputStream, String authority) throws IOException;
}
