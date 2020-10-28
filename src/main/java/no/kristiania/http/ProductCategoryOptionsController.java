package no.kristiania.http;

import no.kristiania.database.ProductCategoryDao;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProductCategoryOptionsController implements HttpController {
    public ProductCategoryOptionsController(ProductCategoryDao productCategoryDao) {
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        String body = "<option>A</option><option>B</option>";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Connection: close\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "\r\n" +
                body;
        // Write the response back to the client
        clientSocket.getOutputStream().write(response.getBytes());

    }
}
