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

    private final DataSource dataSource;

    public ProductDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insert(String product) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO products (product_name) values (?)")) {
                statement.setString(1, product);
                statement.executeUpdate();
            }
        }
    }

    public List<String> list() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM products")) {
                try (ResultSet rs = statement.executeQuery()) {
                    List<String> products = new ArrayList<>();
                    while (rs.next()) {
                        products.add(rs.getString("product_name"));
                    }
                    return products;
                }
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/kristianiashop");
        dataSource.setUser("kristianiashopuser");
        // TODO: database passwords should never be checked in!
        dataSource.setPassword("5HGQ[f_t2D}^?");

        ProductDao productDao = new ProductDao(dataSource);

        System.out.println("What's the name of the new product");
        Scanner scanner = new Scanner(System.in);
        String productName = scanner.nextLine();

        productDao.insert(productName);
        System.out.println(productDao.list());
    }
}
