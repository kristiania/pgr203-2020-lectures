package no.kristiania.pgr203;

import java.io.IOException;
import java.net.Socket;

@SuppressWarnings("WeakerAccess")
public class HttpClient {

    private String requestTarget;

    public HttpClient(String requestTarget) {
        this.requestTarget = requestTarget;
    }

    public static void main(String[] args) throws IOException {
        HttpResponse response = new HttpClient("/echo?status=200&Content-Type=text%2Fhtml&body=Hello%tgere!")
                .execute();
        System.out.println(response.getStatusCode());
    }

    public HttpResponse execute() throws IOException {
        try(Socket socket = new Socket("urlecho.appspot.com", 80)) {
            socket.getOutputStream().write(("GET " + requestTarget + " HTTP/1.1\r\nHost: urlecho.appspot.com\r\nConnection: close\r\n\r\n").getBytes());
            socket.getOutputStream().flush();

            StringBuilder serverResponse = new StringBuilder();
            int c;
            while ((c = socket.getInputStream().read()) != -1) {
                serverResponse.append(((char)c));
            }
            return new HttpResponse(serverResponse);
        }
    }
}
