package no.kristiania.http;

import no.kristiania.database.Product;

import java.io.IOException;
import java.net.Socket;

public class ProductCategoryGetController implements ControllerMcControllerface {
    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException {
        String body = "<ul>";
        body += "</ul>";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                body;

        // Write the response back to the client
        clientSocket.getOutputStream().write(response.getBytes());

    }
}
