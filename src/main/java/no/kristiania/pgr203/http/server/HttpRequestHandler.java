package no.kristiania.pgr203.http.server;

import no.kristiania.pgr203.http.HttpHeaders;

import java.io.IOException;
import java.io.InputStream;

public interface HttpRequestHandler {
    HttpServerResponse execute(String absolutePath, String query, HttpHeaders requestHeaders, InputStream inputStream) throws IOException;

    boolean canHandle(String requestMethod, String absolutePath);
}
