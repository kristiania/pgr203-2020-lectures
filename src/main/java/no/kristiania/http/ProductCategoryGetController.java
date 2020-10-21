package no.kristiania.http;

import no.kristiania.database.ProductCategory;
import no.kristiania.database.ProductCategoryDao;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProductCategoryGetController implements ControllerMcControllerface {
    private ProductCategoryDao productCategoryDao;

    public ProductCategoryGetController(ProductCategoryDao productCategoryDao) {
        this.productCategoryDao = productCategoryDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        String body = "<ul>";
        for (ProductCategory category : productCategoryDao.list()) {
            body += "<li>" + category.getName() + "</li>";
        }

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
