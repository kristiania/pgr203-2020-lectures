package no.kristiania.pgr203;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ShowProductsHandler implements HttpRequestHandler {
    private List<Product> products;

    public ShowProductsHandler(List<Product> products) {
        this.products = products;
    }

    @Override
    public HttpServerResponse execute(String requestMethod, String absolutePath, String query, HttpHeaders requestHeaders, InputStream inputStream) throws IOException {
        return new HttpContentResponse(content(HttpQuery.parse(query)));
    }

    private String content(HttpQuery query) {
        Integer categoryId = null;
        if (query != null && query.containsKey("productCategory") && !query.get("productCategory").isEmpty()) {
            categoryId = Integer.parseInt(query.get("productCategory"));
        }
        return content(categoryId);
    }

    private String content(Integer categoryId) {
        StringBuilder productListing = new StringBuilder("<div>");
        for (Product product : products) {
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
