package no.kristiania.pgr203.webshop;

import no.kristiania.pgr203.http.server.HttpContentResponse;
import no.kristiania.pgr203.http.HttpHeaders;
import no.kristiania.pgr203.http.server.HttpRequestHandler;
import no.kristiania.pgr203.http.server.HttpServerResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class ShowShoppingCartHandler implements HttpRequestHandler {
    private Map<Integer, Integer> shoppingCart;
    private final List<Product> products;

    public ShowShoppingCartHandler(Map<Integer, Integer> shoppingCart, List<Product> products) {
        this.shoppingCart = shoppingCart;
        this.products = products;
    }

    @Override
    public HttpServerResponse execute(String requestMethod, String absolutePath, String query, HttpHeaders requestHeaders, InputStream inputStream) throws IOException {
        return new HttpContentResponse(shoppingCartHtml());
    }

    @Override
    public boolean canHandle(String requestMethod, String absolutePath) {
        return requestMethod.equals("GET") && absolutePath.equals("/shoppingCart");
    }

    public String shoppingCartHtml() {
        StringBuilder shoppingCartContent = new StringBuilder("<div>");
        for (Map.Entry<Integer, Integer> entry : shoppingCart.entrySet()) {
            String productName = null;
            for (Product product : products) {
                if (product.getId() == entry.getKey()) {
                    productName = product.getName();
                }
            }
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
