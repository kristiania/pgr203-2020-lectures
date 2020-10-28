package no.kristiania.database;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductDao extends AbstractDao<Product> {

    public ProductDao(DataSource dataSource) {
        super(dataSource);
    }

    public void insert(Product product) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO products (product_name, price) values (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, product.getName());
                statement.setDouble(2, product.getPrice());
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    generatedKeys.next();
                    product.setId(generatedKeys.getInt("id"));
                }
            }
        }
    }

    public void update(Product product) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE products SET category_id = ? WHERE id = ?"
            )) {
                statement.setInt(1, product.getCategoryId());
                statement.setInt(2, product.getId());
                statement.executeUpdate();
            }
        }
    }

    public Product retrieve(Integer id) throws SQLException {
        return retrieve(id, "SELECT * FROM products WHERE id = ?");
    }

    public List<Product> list() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM products")) {
                try (ResultSet rs = statement.executeQuery()) {
                    List<Product> products = new ArrayList<>();
                    while (rs.next()) {
                        products.add(mapRow(rs));
                    }
                    return products;
                }
            }
        }
    }

    @Override
    protected Product mapRow(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setCategoryId((Integer) rs.getObject("category_id"));
        product.setName(rs.getString("product_name"));
        product.setPrice(rs.getDouble("price"));
        return product;
    }

}
