package no.kristiania.database;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProductDao {

    private DataSource dataSource;

    public ProductDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static void main(String[] args) throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/kristianiashop");
        dataSource.setUser("kristianiashop");
        dataSource.setPassword("nice try!");

        ProductDao productDao = new ProductDao(dataSource);

        System.out.println("Please enter product name:");
        Scanner scanner = new Scanner(System.in);
        String productName = scanner.nextLine();

        Product product = new Product();
        product.setName(productName);
        productDao.insert(product);
        for (Product p : productDao.list()) {
            System.out.println(p);
        }
    }

    public void insert(Product product) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO products (product_name) VALUES (?)")) {
                statement.setString(1, product.getName());
                statement.executeUpdate();
            }
        }
    }

    public List<Product> list() throws SQLException {
        List<Product> products = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from products")) {
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        Product product = new Product();
                        product.setName(rs.getString("product_name"));
                        products.add(product);
                    }
                }
            }
        }
        return products;
    }
}
