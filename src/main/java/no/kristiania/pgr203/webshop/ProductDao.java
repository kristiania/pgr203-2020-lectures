package no.kristiania.pgr203.webshop;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {
    private List<Product> tmpProducts = new ArrayList<>();
    private DataSource dataSource;

    public ProductDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insert(Product product) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("insert into products (name, category_id) values (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, product.getName());
                statement.setLong(2, product.getCategoryId());

                statement.executeUpdate();
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    product.setId(rs.getInt(1));
                }
            }
        }
        tmpProducts.add(product);
    }

    public Product retrieve(int id) throws SQLException {

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement("select * from products where id = ?")) {
                stmt.setInt(1, id);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Product product = new Product();
                        product.setId(rs.getInt("id"));
                        product.setName(rs.getString("name"));

                        return product;
                    }
                    return null;
                }
            }
        }
/*
        for (Product product : tmpProducts) {
            if (product.getId() == id) {
                return product;
            }
        }

        return null;

 */
    }
}
