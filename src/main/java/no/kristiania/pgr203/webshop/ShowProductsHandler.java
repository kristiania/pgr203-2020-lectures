package no.kristiania.pgr203.webshop;

import no.kristiania.pgr203.http.HttpHeaders;
import no.kristiania.pgr203.http.HttpQuery;
import no.kristiania.pgr203.http.server.HttpContentResponse;
import no.kristiania.pgr203.http.server.HttpRequestHandler;
import no.kristiania.pgr203.http.server.HttpServerResponse;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class ShowProductsHandler implements HttpRequestHandler {
    private ProductDao products;

    public ShowProductsHandler(ProductDao products) {
        this.products = products;
    }

    @Override
    public HttpServerResponse execute(String requestMethod, String absolutePath, String query, HttpHeaders requestHeaders, InputStream inputStream) throws IOException {
        try {
            return new HttpContentResponse(content(HttpQuery.parse(query)));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String content(HttpQuery query) throws SQLException {
        Integer categoryId = null;
        if (query != null && query.containsKey("productCategory") && !query.get("productCategory").isEmpty()) {
            categoryId = Integer.parseInt(query.get("productCategory"));
        }
        return content(categoryId);
    }

    private String content(Integer categoryId) throws SQLException {
        StringBuilder productListing = new StringBuilder("<div>");
        for (Product product : products.listAll()) {
            if (categoryId != null && product.getCategoryId() != categoryId) {
                continue;
            }
            productListing.append("<div><label>")
                    .append("<input type='radio' name='productId' value='")
                    .append(product.getId())
                    .append("'>")
                    .append(product.getName())
                    .append("</label></div>")
            ;
        }
        productListing.append("</div>");
        return productListing.toString();
    }

    @Override
    public boolean canHandle(String requestMethod, String absolutePath) {
        return requestMethod.equals("GET") && absolutePath.equals("/products");
    }
}
