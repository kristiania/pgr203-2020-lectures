package no.kristiania.database;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class ProductDaoTest {

    private ProductDao productDao;

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        productDao = new ProductDao(dataSource);
    }

    @Test
    void shouldListInsertedProducts() throws SQLException {
        Product product = exampleProduct();
        productDao.insert(product);
        assertThat(productDao.list()).contains(product.getName());
    }

    @Test
    void shouldRetrieveAllProductProperties() throws SQLException {
        Product product = exampleProduct();
        productDao.insert(product);
        assertThat(productDao.retrieve(product.getId()))
                .isEqualTo(product);
    }

    private Product exampleProduct() {
        return new Product();
    }

    private String exampleProductName() {
        String[] options = {"Apples", "Bananas", "Coconuts", "Dates"};
        Random random = new Random();
        return options[random.nextInt(options.length)];
    }
}