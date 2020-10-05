package no.kristiania.database;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class ProductDaoTest {

    @Test
    void shouldListInsertedProducts() throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();

        ProductDao productDao = new ProductDao(dataSource);
        String product = exampleProduct();
        productDao.insert(product);
        assertThat(productDao.list()).contains(product);
    }

    /** Returns a random product name */
    private String exampleProduct() {
        String[] options = {"Apples", "Bananas", "Coconuts", "Dates", "Eggplant"};
        Random random = new Random();
        return options[random.nextInt(options.length)];
    }
}