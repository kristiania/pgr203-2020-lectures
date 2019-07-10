package no.kristiania.pgr203.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

@SuppressWarnings("WeakerAccess")
public class HttpRequest {

    protected final String hostname;
    final int port;
    protected final String requestTarget;
    final HttpHeaders headers = new HttpHeaders();

    public HttpRequest(String hostname, int port, String requestTarget) {
        this.hostname = hostname;
        this.port = port;
        this.requestTarget = requestTarget;
        headers.add("Host", hostname);
        headers.add("Connection", "close");
    }

    public static void main(String[] args) throws IOException {
        HttpResponse response = new HttpRequest("localhost", 0, "/echo?status=200")
                .execute();
        System.out.println(response.getStatusCode());
    }

    public HttpResponse execute() throws IOException {
        try(Socket socket = new Socket(hostname, port)) {
            socket.getOutputStream().write((getHttpMethod() + " " + requestTarget + " HTTP/1.1\r\n").getBytes());
            headers.write(socket.getOutputStream());
            socket.getOutputStream().write("\r\n".getBytes());
            writeContent(socket.getOutputStream());

            socket.getOutputStream().flush();

            return new HttpResponse(socket.getInputStream());
        }
    }

    void writeContent(OutputStream outputStream) throws IOException {

    }

    String getHttpMethod() {
        return "GET";
    }
}
