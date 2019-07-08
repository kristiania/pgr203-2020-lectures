package no.kristiania.pgr203;

import java.io.IOException;
import java.io.InputStream;

interface HttpRequestHandler {
    HttpServerResponse execute(String requestMethod, String absolutePath, String query, HttpHeaders requestHeaders, InputStream inputStream) throws IOException;

    boolean canHandle(String requestMethod, String absolutePath);
}
