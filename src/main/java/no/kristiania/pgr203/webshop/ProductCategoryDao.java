package no.kristiania.pgr203.webshop;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class ProductCategoryDao {
    private DataSource dataSource;

    public ProductCategoryDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insert(ProductCategory category) throws SQLException {
        try (var connection = dataSource.getConnection()) {
            try (var stmt = connection.prepareStatement("insert into product_categories (name) values (?)", RETURN_GENERATED_KEYS)) {
                stmt.setString(1, category.getName());
                stmt.executeUpdate();

                try (var generatedKeys = stmt.getGeneratedKeys()) {
                    generatedKeys.next();
                    category.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    public List<ProductCategory> listAll() throws SQLException {
        try (var connection = dataSource.getConnection()) {
            try (var stmt = connection.prepareStatement("select * from product_categories")) {
                try (var rs = stmt.executeQuery()) {
                    var productCategories = new ArrayList<ProductCategory>();
                    while (rs.next()) {
                        var category = new ProductCategory();
                        category.setId(rs.getInt("id"));
                        category.setName(rs.getString("name"));
                        productCategories.add(category);
                    }
                    return productCategories;
                }
            }
        }
    }
}
