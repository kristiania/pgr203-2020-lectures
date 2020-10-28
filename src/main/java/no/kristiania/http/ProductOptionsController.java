package no.kristiania.http;

import no.kristiania.database.Product;
import no.kristiania.database.ProductDao;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class ProductOptionsController implements HttpController {
    private ProductDao productDao;

    public ProductOptionsController(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        HttpMessage response = new HttpMessage(getBody());
        response.write(clientSocket);
    }

    public String getBody() throws SQLException {
        String body = "";
        for (Product product : productDao.list()) {
            body += "<option value=" + product.getId() + ">" + product.getName() + "</option>";
        }
        return body;
    }
}
