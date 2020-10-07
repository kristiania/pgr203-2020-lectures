package no.kristiania.database;


import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductDao {

    public static void main(String[] args) throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/kristianiashop");
        dataSource.setUser("kristianiashopuser");
        // TODO: database passwords should never be checked in!
        dataSource.setPassword("5HGQ[f_t2D}^?");

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM products")) {
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        System.out.println(rs.getString("product_name"));
                    }
                }
            }
        }
    }
}
