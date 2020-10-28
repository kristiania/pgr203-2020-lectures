package no.kristiania.http;

import no.kristiania.database.Product;
import no.kristiania.database.ProductCategory;
import no.kristiania.database.ProductCategoryDao;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProductCategoryOptionsController implements HttpController {
    private ProductCategoryDao categoryDao;

    public ProductCategoryOptionsController(ProductCategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        HttpMessage response = new HttpMessage(getBody());
        response.write(clientSocket);
    }

    public String getBody() throws SQLException {
        String body = "";
        for (ProductCategory category : categoryDao.list()) {
            body += "<option value=" + category.getId() + ">" + category.getName() + "</option>";
        }
        return body;
    }

}
