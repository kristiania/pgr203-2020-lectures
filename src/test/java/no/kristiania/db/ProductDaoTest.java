package no.kristiania.db;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.SQLException;
import java.util.Random;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductDaoTest {

    private ProductDao productDao = new ProductDao(createTestDataSource());

    @Test
    void shouldListSavedProducts() throws SQLException {
        Product product = sampleProduct();
        productDao.insert(product);
        assertThat(productDao.list())
                .extracting(Product::getName)
                .contains(product.getName());
    }

    @Test
    void shouldRetrieveSingleProduct() throws SQLException {
        Product product = sampleProduct();
        productDao.insert(product);
        assertThat(productDao.retrieve(product.getId()))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(product);
    }

    @Test
    void shouldThrowExceptionOnMissingProduct() {
        assertThatThrownBy(() -> productDao.retrieve(-1))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private JdbcDataSource createTestDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }


    private Product sampleProduct() {
        Product product = new Product();
        product.setName(sampleProductName());
        product.setPrice(10.0);
        return product;
    }

    private String sampleProductName() {
        String[] options = { "Apple", "Banana", "Coconut", "Dates", "Eggplant" };
        Random random = new Random();
        return options[random.nextInt(options.length)];
    }
}
