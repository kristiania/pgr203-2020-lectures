package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;

public class HttpClient {

    private String responseBody;
    private HttpMessage responseMessage;

    // Constructor - det som kalles når vi sier new
    public HttpClient(final String hostname, int port, final String requestTarget) throws IOException {
        // Connect til serven
        Socket socket = new Socket(hostname, port);

        // HTTP Request consists of request line + 0 or more request headers
        //  request line consists of "verb" (GET, POST, PUT) request target ("/echo", "/echo?status=404"), protocol (HTTP/1.1)
        HttpMessage requestMessage = new HttpMessage("GET " + requestTarget + " HTTP/1.1");
        requestMessage.setHeader("Host", hostname);
        requestMessage.write(socket);

        // The first line in the response is called status line or response line
        // response line consists of protocol ("HTTP/1.1") status code (200, 404, 401, 500) and status message
        responseMessage = HttpMessage.read(socket);

        // Response header content-length tells who many bytes the response body is
        int contentLength = Integer.parseInt(getResponseHeader("Content-Length"));
        StringBuilder body = new StringBuilder();
        for (int i = 0; i < contentLength; i++) {
            // Read content body based on content-length
            body.append((char)socket.getInputStream().read());
        }
        responseBody = body.toString();
    }

    public static void main(String[] args) throws IOException {
        HttpClient client = new HttpClient("urlecho.appspot.com", 80, "/echo?status=404&Content-Type=text%2Fhtml&body=Hello+world");
        System.out.println(client.getResponseBody());
    }

    public int getStatusCode() {
        String[] responseLineParts = responseMessage.getStartLine().split(" ");
        return Integer.parseInt(responseLineParts[1]);
    }

    public String getResponseHeader(String headerName) {
        // Implementation of HttpMessage.getHeader is left as an exercise to the reader
        return responseMessage.getHeader(headerName);
    }

    public String getResponseBody() {
        return responseBody;
    }
}
