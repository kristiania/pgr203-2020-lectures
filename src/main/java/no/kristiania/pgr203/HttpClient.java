package no.kristiania.pgr203;

import java.io.IOException;
import java.net.Socket;

public class HttpClient {

    public static void main(String[] args) throws IOException {
        try(Socket socket = new Socket("urlecho.appspot.com", 80)) {
            socket.getOutputStream().write("GET /echo?status=200&Content-Type=text%2Fhtml&body=Hello%20world! HTTP/1.1\r\nHost: urlecho.appspot.com\r\n\r\n".getBytes());
            socket.getOutputStream().flush();

            int c;
            while ((c = socket.getInputStream().read()) != -1) {
                System.out.print((char)c);
            }
        }
    }
}
