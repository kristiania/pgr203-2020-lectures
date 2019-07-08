package no.kristiania.pgr203;

import java.io.IOException;
import java.net.Socket;

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
        HttpHeaders headers = new HttpHeaders();
        headers.add("Host", hostname);
        headers.add("Connection", "close");
        headers.setContentLength(content.length());
        try(Socket socket = new Socket(hostname, port)) {
            socket.getOutputStream().write(("POST " + requestTarget + " HTTP/1.1\r\n").getBytes());
            headers.write(socket.getOutputStream());
            socket.getOutputStream().write("\r\n".getBytes());
            socket.getOutputStream().write(content.getBytes());

            socket.getOutputStream().flush();

            return new HttpResponse(socket.getInputStream());
        }
    }
}
