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

    public Product retrieve(int id) {
        for (Product product : tmpProducts) {
            if (product.getId() == id) {
                return product;
            }
        }

        return null;
    }
}
