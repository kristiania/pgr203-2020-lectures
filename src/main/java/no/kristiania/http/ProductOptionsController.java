package no.kristiania.http;

import no.kristiania.database.ProductDao;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProductOptionsController implements HttpController {
    public ProductOptionsController(ProductDao productDao) {
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        String body = getBody();
        String response = "HTTP/1.1 200 OK\r\n" +
                "Connection: close\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "\r\n" +
                body;
        // Write the response back to the client
        clientSocket.getOutputStream().write(response.getBytes());
    }

    public String getBody() {
        return "<option>A</option><option>B</option>";
    }
}
