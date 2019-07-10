package no.kristiania.pgr203.webshop;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {
    private DataSource dataSource;

    public ProductDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insert(Product product) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("insert into products (name, product_category) values (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, product.getName());
                statement.setLong(2, product.getCategoryId());

                statement.executeUpdate();
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    product.setId(rs.getInt(1));
                }
            }
        }
    }

    public Product retrieve(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement("select * from products where id = ?")) {
                stmt.setInt(1, id);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapToProduct(rs);
                    }
                    return null;
                }
            }
        }
    }

    private Product mapToProduct(ResultSet rs) throws SQLException {
        var product = new Product();
        product.setId(rs.getInt("id"));
        product.setName(rs.getString("name"));
        product.setCategoryId(rs.getLong("product_category"));
        return product;
    }

    public List<Product> listAll() throws SQLException {
        try (var connection = dataSource.getConnection()) {
            try (var stmt = connection.prepareStatement("select * from products")) {
                try (var rs = stmt.executeQuery()) {
                    var products = new ArrayList<Product>();
                    while (rs.next()) {
                        products.add(mapToProduct(rs));
                    }
                    return products;
                }
            }
        }
    }

    public List<Product> listByCategory(long categoryId) throws SQLException {
        try (var connection = dataSource.getConnection()) {
            try (var stmt = connection.prepareStatement("select * from products where product_category = ?")) {
                stmt.setLong(1, categoryId);
                try (var rs = stmt.executeQuery()) {
                    var products = new ArrayList<Product>();
                    while (rs.next()) {
                        products.add(mapToProduct(rs));
                    }
                    return products;
                }
            }
        }
    }
}
