package no.kristiania.db;

import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProductDao {

    private DataSource dataSource;

    public ProductDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insert(Product product) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement insertStatement = connection.prepareStatement(
                    "insert into products (product_name, price) values (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                insertStatement.setString(1, product.getName());
                insertStatement.setDouble(2, product.getPrice());
                insertStatement.execute();

                ResultSet generatedKeys = insertStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    public List<Product> list() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from products")) {
                try (ResultSet rs = statement.executeQuery()) {
                    List<Product> products = new ArrayList<>();
                    while (rs.next()) {
                        products.add(mapToProduct(rs));
                    }
                    return products;
                }
            }
        }
    }

    public Product retrieve(long id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from products where id = ?")) {
                statement.setDouble(1, id);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return mapToProduct(rs);
                    }
                    throw new EntityNotFoundException("Not Found: Product with id " + id);
                }
            }
        }
    }

    private Product mapToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setName(rs.getString("product_name"));
        product.setPrice(rs.getDouble("price"));
        return product;
    }


    public static void main(String[] args) throws SQLException {
        ProductDao productDao = new ProductDao(getDataSource());

        Scanner scanner = new Scanner(System.in);

        Product newProduct = new Product();
        System.out.println("New product name to insert");
        newProduct.setName(scanner.nextLine());
        System.out.println("Price");
        newProduct.setPrice(scanner.nextDouble());

        productDao.insert(newProduct);

        for (Product product : productDao.list()) {
            System.out.println(product);
        }
    }

    private static PGSimpleDataSource getDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/kristianiashop");
        dataSource.setUser("kristianiashop");
        dataSource.setPassword("Hahahaha");
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }

}
