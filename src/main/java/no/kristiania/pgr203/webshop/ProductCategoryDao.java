package no.kristiania.pgr203.webshop;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class ProductCategoryDao extends AbstractDao<ProductCategory> {

    public ProductCategoryDao(DataSource dataSource) {
        super(dataSource);
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

    protected ProductCategory mapResultSet(ResultSet rs) throws SQLException {
        var category = new ProductCategory();
        category.setId(rs.getInt("id"));
        category.setName(rs.getString("name"));
        return category;
    }

    public List<ProductCategory> listAll() throws SQLException {
        return listAll("select * from product_categories");
    }
}
