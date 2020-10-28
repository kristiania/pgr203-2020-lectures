package no.kristiania.http;

import no.kristiania.database.Product;
import no.kristiania.database.ProductDao;
import no.kristiania.database.ProductDaoTest;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class ProductOptionsControllerTest {

    private ProductDao productDao;

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        productDao = new ProductDao(dataSource);
    }

    @Test
    void shouldReturnProductsAsOptions() throws SQLException {
        ProductOptionsController controller = new ProductOptionsController(productDao);
        Product product = ProductDaoTest.exampleProduct();
        productDao.insert(product);

        assertThat(controller.getBody())
                .contains("<option value=" + product.getId() + ">" + product.getName() + "</option>");
    }
}