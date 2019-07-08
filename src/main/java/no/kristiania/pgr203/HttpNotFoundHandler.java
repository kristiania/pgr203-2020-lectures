package no.kristiania.pgr203;

import java.io.IOException;
import java.io.InputStream;

public class HttpNotFoundHandler implements HttpRequestHandler {
    @Override
    public HttpServerResponse execute(String requestMethod, String absolutePath, String query, HttpHeaders requestHeaders, InputStream inputStream) throws IOException {
        return new HttpNotFoundResponse();
    }

    @Override
    public boolean canHandle(String requestMethod, String absolutePath) {
        return true;
    }
}
