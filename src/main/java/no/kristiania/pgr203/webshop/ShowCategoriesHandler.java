package no.kristiania.pgr203.webshop;

import no.kristiania.pgr203.http.HttpHeaders;
import no.kristiania.pgr203.http.HttpQuery;
import no.kristiania.pgr203.http.server.HttpContentResponse;
import no.kristiania.pgr203.http.server.HttpRequestHandler;
import no.kristiania.pgr203.http.server.HttpServerResponse;

import java.io.InputStream;
import java.sql.SQLException;

public class ShowCategoriesHandler implements HttpRequestHandler {
    private final ProductCategoryDao categoriesDao;

    public ShowCategoriesHandler(ProductCategoryDao categoriesDao) {
        this.categoriesDao = categoriesDao;
    }

    @Override
    public HttpServerResponse execute(String absolutePath, String query, HttpHeaders requestHeaders, InputStream inputStream) {
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
        StringBuilder result = new StringBuilder("<option value=''>None</option>");
        for (ProductCategory category : categoriesDao.listAll()) {
            result.append("<option value='")
                    .append(category.getId()).append("'");
            if (categoryId != null && category.getId() == categoryId) {
                result.append(" selected='selected'");
            }
            result.append(">")
                    .append(category.getName())
                    .append("</option>");
        }
        return result.toString();
    }

    @Override
    public boolean canHandle(String requestMethod, String absolutePath) {
        return requestMethod.equals("GET") && absolutePath.equals("/productCategories");
    }
}
