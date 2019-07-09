package no.kristiania.pgr203.webshop;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class ProductDaoTest {

    @Test
    void shouldRetrieveInsertedProduct() throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();

        Product product = sampleProduct();

        ProductDao dao = new ProductDao(dataSource);
        dao.insert(product);

        assertThat(dao.retrieve(product.getId()))
                .isEqualToComparingFieldByField(product);
    }

    private Product sampleProduct() {
        Product product = new Product();
        product.setName(pickOne("Apples", "Bananas", "Coconuts", "Dates", "Emons", "Figs"));
        return product;
    }

    private static Random random = new Random();

    @SafeVarargs
    private <T> T pickOne(T... options) {
        return options[random.nextInt(options.length)];
    }
}