package no.kristiania.pgr203.http;

import java.io.IOException;
import java.io.OutputStream;

public class HttpPostRequest extends HttpRequest {
    private String content;

    public HttpPostRequest(String hostname, int port, String requestTarget) {
        super(hostname, port, requestTarget);
    }

    public void setContent(String content) {
        this.content = content;
        headers.setContentLength(content.length());
    }

    @Override
    void writeContent(OutputStream outputStream) throws IOException {
        outputStream.write(content.getBytes());
    }

    @Override
    String getHttpMethod() {
        return "POST";
    }
}
