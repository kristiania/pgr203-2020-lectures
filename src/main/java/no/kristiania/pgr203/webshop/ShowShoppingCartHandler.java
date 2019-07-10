package no.kristiania.pgr203.webshop;

import no.kristiania.pgr203.http.HttpHeaders;
import no.kristiania.pgr203.http.server.HttpContentResponse;
import no.kristiania.pgr203.http.server.HttpRequestHandler;
import no.kristiania.pgr203.http.server.HttpServerResponse;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Map;

public class ShowShoppingCartHandler implements HttpRequestHandler {
    private Map<Integer, Integer> shoppingCart;
    private final ProductDao products;

    public ShowShoppingCartHandler(Map<Integer, Integer> shoppingCart, ProductDao products) {
        this.shoppingCart = shoppingCart;
        this.products = products;
    }

    @Override
    public HttpServerResponse execute(String requestMethod, String absolutePath, String query, HttpHeaders requestHeaders, InputStream inputStream) throws IOException {
        try {
            return new HttpContentResponse(shoppingCartHtml());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean canHandle(String requestMethod, String absolutePath) {
        return requestMethod.equals("GET") && absolutePath.equals("/shoppingCart");
    }

    public String shoppingCartHtml() throws SQLException {
        StringBuilder shoppingCartContent = new StringBuilder("<div>");
        for (Map.Entry<Integer, Integer> entry : shoppingCart.entrySet()) {
            String productName = products.retrieve(entry.getKey()).getName();
            shoppingCartContent.append("<div>")
                    .append(entry.getValue())
                    .append(" x ")
                    .append(productName)
                    .append("</div>");
        }
        shoppingCartContent.append("</div>");
        return shoppingCartContent.toString();
    }
}
