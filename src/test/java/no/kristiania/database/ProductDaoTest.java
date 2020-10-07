package no.kristiania.database;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class ProductDaoTest {

    @Test
    void shouldListInsertedProducts() throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test");
        dataSource.getConnection()
                .prepareStatement("create table products (product_name varchar)")
                .executeUpdate();

        ProductDao productDao = new ProductDao(dataSource);
        String product = exampleProductName();
        productDao.insert(product);
        assertThat(productDao.list()).contains(product);
    }

    private String exampleProductName() {
        String[] options = {"Apples", "Bananas", "Coconuts", "Dates"};
        Random random = new Random();
        return options[random.nextInt(options.length)];
    }
}