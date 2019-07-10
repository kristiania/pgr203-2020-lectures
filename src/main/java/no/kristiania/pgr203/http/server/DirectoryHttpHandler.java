package no.kristiania.pgr203.http.server;

import no.kristiania.pgr203.http.HttpHeaders;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirectoryHttpHandler implements HttpRequestHandler {
    private final Path directory;

    public DirectoryHttpHandler(Path directory) {
        this.directory = directory;
    }

    @Override
    public HttpServerResponse execute(String absolutePath, String query, HttpHeaders requestHeaders, InputStream inputStream) {
        return new DirectoryHttpServerResponse(directory.resolve(absolutePath.substring(1)));
    }

    @Override
    public boolean canHandle(String requestMethod, String absolutePath) {
        Path targetFile = directory.resolve(absolutePath.substring(1));
        return Files.isRegularFile(targetFile);
    }
}
