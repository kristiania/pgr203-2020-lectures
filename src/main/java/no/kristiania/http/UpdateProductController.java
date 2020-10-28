package no.kristiania.http;

import no.kristiania.database.Product;
import no.kristiania.database.ProductDao;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class UpdateProductController implements HttpController {
    private ProductDao productDao;

    public UpdateProductController(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        Integer productId = Integer.valueOf(requestParameter.getParameter("productId"));
        Integer categoryId = Integer.valueOf(requestParameter.getParameter("categoryId"));
        Product product = productDao.retrieve(productId);
        product.setCategoryId(categoryId);

        productDao.update(product);
    }
}
