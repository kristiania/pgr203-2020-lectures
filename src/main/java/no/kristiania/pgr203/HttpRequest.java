package no.kristiania.pgr203;

import java.io.IOException;
import java.net.Socket;

@SuppressWarnings("WeakerAccess")
public class HttpRequest {

    private final String hostname;
    private final int port;
    private String requestTarget;

    public HttpRequest(String hostname, int port, String requestTarget) {
        this.hostname = hostname;
        this.port = port;
        this.requestTarget = requestTarget;
    }

    public static void main(String[] args) throws IOException {
        HttpResponse response = new HttpRequest("localhost", 0, "/echo?status=200&Content-Type=text%2Fhtml&body=Hello%tgere!")
                .execute();
        System.out.println(response.getStatusCode());
    }

    public HttpResponse execute() throws IOException {
        try(Socket socket = new Socket(hostname, port)) {
            socket.getOutputStream().write(("GET " + requestTarget + " HTTP/1.1\r\nHost: " + hostname + "\r\nConnection: close\r\n\r\n").getBytes());
            socket.getOutputStream().flush();

            return new HttpResponse(socket.getInputStream());
        }
    }
}
