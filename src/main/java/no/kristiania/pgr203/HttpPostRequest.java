package no.kristiania.pgr203;

import java.io.IOException;

public class HttpPostRequest extends HttpRequest {
    private String content;

    public HttpPostRequest(String hostname, int port, String requestTarget) {
        super(hostname, port, requestTarget);
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public HttpResponse execute() throws IOException {
        return super.execute();
    }
}
