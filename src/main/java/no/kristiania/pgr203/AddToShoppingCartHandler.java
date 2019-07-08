package no.kristiania.pgr203;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class AddToShoppingCartHandler implements HttpRequestHandler {
    private final Map<Integer, Integer> shoppingCart;

    public AddToShoppingCartHandler(Map<Integer, Integer> shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    @Override
    public HttpServerResponse execute(String requestMethod, String absolutePath, String query, HttpHeaders requestHeaders, InputStream inputStream) throws IOException {
        String requestBody = HttpMessage.readBytes(inputStream, requestHeaders.getContentLength());

        HttpQuery parameters = HttpQuery.parse(requestBody);

        int quantity = parameters.containsKey("quantity") ? Integer.parseInt(parameters.get("quantity")) : 1;
        int productId = Integer.parseInt(parameters.get("productId"));
        if (!shoppingCart.containsKey(productId)) {
            shoppingCart.put(productId, 0);
        }
        shoppingCart.put(productId, shoppingCart.get(productId) + quantity);

        return new HttpServerRedirectResponse("/products.html");
    }

    @Override
    public boolean canHandle(String requestMethod, String absolutePath) {
        return requestMethod.equals("POST") && absolutePath.equals("/shoppingCart");
    }
}
