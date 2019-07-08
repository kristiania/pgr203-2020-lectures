package no.kristiania.pgr203.http.server;

import no.kristiania.pgr203.http.HttpHeaders;

import java.io.InputStream;

public class HttpNotFoundHandler implements HttpRequestHandler {
    @Override
    public HttpServerResponse execute(String requestMethod, String absolutePath, String query, HttpHeaders requestHeaders, InputStream inputStream) {
        return new HttpNotFoundResponse();
    }

    @Override
    public boolean canHandle(String requestMethod, String absolutePath) {
        return true;
    }
}
