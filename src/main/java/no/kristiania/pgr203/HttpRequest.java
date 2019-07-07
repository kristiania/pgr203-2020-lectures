package no.kristiania.pgr203;

import java.io.IOException;
import java.net.Socket;

@SuppressWarnings("WeakerAccess")
public class HttpRequest {

    private String requestTarget;

    public HttpRequest(String requestTarget) {
        this.requestTarget = requestTarget;
    }

    public static void main(String[] args) throws IOException {
        HttpResponse response = new HttpRequest("/echo?status=200&Content-Type=text%2Fhtml&body=Hello%tgere!")
                .execute();
        System.out.println(response.getStatusCode());
    }

    public HttpResponse execute() throws IOException {
        try(Socket socket = new Socket("urlecho.appspot.com", 80)) {
            socket.getOutputStream().write(("GET " + requestTarget + " HTTP/1.1\r\nHost: urlecho.appspot.com\r\nConnection: close\r\n\r\n").getBytes());
            socket.getOutputStream().flush();

            return new HttpResponse(socket.getInputStream());
        }
    }
}
