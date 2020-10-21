package no.kristiania.database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductCategoryDao {
    private DataSource dataSource;

    public ProductCategoryDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<ProductCategory> list() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM products")) {
                try (ResultSet rs = statement.executeQuery()) {
                    List<ProductCategory> products = new ArrayList<>();
                    while (rs.next()) {
                        products.add(mapRowToCategory(rs));
                    }
                    return products;
                }
            }
        }
    }

    private ProductCategory mapRowToCategory(ResultSet rs) {
        return new ProductCategory();
    }

    public void insert(ProductCategory category) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO products (product_name, price) values (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, category.getName());
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    generatedKeys.next();
                    category.setId(generatedKeys.getLong("id"));
                }
            }
        }
    }
}
