package no.kristiania.http;

import no.kristiania.database.ProductCategory;
import no.kristiania.database.ProductCategoryDao;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

public class ProductCategoryOptionsController implements HttpController {
    private ProductCategoryDao categoryDao;

    public ProductCategoryOptionsController(ProductCategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        String requestTarget = request.getStartLine().split(" ")[1];
        int questionPos = requestTarget.indexOf('?');
        Integer categoryId = null;
        if (questionPos != -1) {
            QueryString queryString = new QueryString(requestTarget.substring(questionPos + 1));
            categoryId = Integer.valueOf(queryString.getParameter("categoryId"));
        }
        HttpMessage response = new HttpMessage(getBody(categoryId));
        response.write(clientSocket);
    }

    public String getBody(Integer categoryId) throws SQLException {
        String body = "";
        List<ProductCategory> list = categoryDao.list();
        for (int i = 0, listSize = list.size(); i < listSize; i++) {
            ProductCategory category = list.get(i);
            if (categoryId != null && categoryId.equals(category.getId())) {
                body += "<option value=" + category.getId() + " selected='selected'>" + category.getName() + "</option>";
            } else {
                body += "<option value=" + category.getId() + ">" + category.getName() + "</option>";
            }
        }
        return body;
    }

}
