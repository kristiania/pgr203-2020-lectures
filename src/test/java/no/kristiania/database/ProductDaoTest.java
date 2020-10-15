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
    private Random random = new Random();

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        productDao = new ProductDao(dataSource);
    }

    @Test
    void shouldListInsertedProducts() throws SQLException {
        Product product1 = exampleProduct();
        Product product2 = exampleProduct();
        productDao.insert(product1);
        productDao.insert(product2);
        assertThat(productDao.list())
                .contains(product1.getName(), product2.getName());
    }

    @Test
    void shouldRetrieveAllProductProperties() throws SQLException {
        productDao.insert(exampleProduct());
        productDao.insert(exampleProduct());
        Product product = exampleProduct();
        productDao.insert(product);
        assertThat(product).hasNoNullFieldsOrProperties();
        assertThat(productDao.retrieve(product.getId()))
                .usingRecursiveComparison()
                .isEqualTo(product);
    }

    private Product exampleProduct() {
        Product product = new Product();
        product.setName(exampleProductName());
        product.setPrice(10.50 + random.nextInt(20));
        return product;
    }

    private String exampleProductName() {
        String[] options = {"Apples", "Bananas", "Coconuts", "Dates"};
        return options[random.nextInt(options.length)];
    }
}