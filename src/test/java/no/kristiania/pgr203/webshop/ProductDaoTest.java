package no.kristiania.pgr203.webshop;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class ProductDaoTest {

    private JdbcDataSource dataSource;
    private ProductDao dao;
    private List<ProductCategory> categories = new ArrayList<>();

    @BeforeEach
    void setUp() throws SQLException {
        dataSource = createDataSource();
        dao = new ProductDao(dataSource);

        var productCategoryDao = new ProductCategoryDao(dataSource);
        for (String categoryName : new String[]{"Fruits", "Breakfasts", "Dinners", "Candies", "Veggies", "Dairy", "Bread", "Non-food"}) {
            ProductCategory category = new ProductCategory();
            category.setName(categoryName);
            productCategoryDao.insert(category);
            categories.add(category);
        }
    }

    @Test
    void shouldRetrieveInsertedProduct() throws SQLException {
        Product product = sampleProduct();
        dao.insert(product);
        assertThat(product).hasNoNullFieldsOrProperties();

        assertThat(dao.retrieve(product.getId()))
                .isEqualToComparingFieldByField(product);
    }

    @Test
    void shouldListAllProducts() throws SQLException {
        Product product = sampleProduct();
        dao.insert(product);
        Product product2 = sampleProduct();
        dao.insert(product2);

        assertThat(dao.listAll())
                .contains(product, product2);
    }

    @Test
    void shouldListProductsByCategory() throws SQLException {
        Product matchingProduct = sampleProduct();
        matchingProduct.setCategoryId(categories.get(0).getId());
        dao.insert(matchingProduct);
        Product nonMatchingProduct = sampleProduct();
        nonMatchingProduct.setCategoryId(categories.get(1).getId());
        dao.insert(nonMatchingProduct);

        assertThat(dao.listByCategory(categories.get(0).getId()))
                .contains(matchingProduct)
                .doesNotContain(nonMatchingProduct);
    }

    static JdbcDataSource createDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }

    private Product sampleProduct() {
        Product product = new Product();
        product.setName(pickOne("Apples", "Bananas", "Coconuts", "Dates", "Emons", "Figs"));
        product.setCategoryId(pickOneItem(categories).getId());
        return product;
    }

    private static Random random = new Random();

    @SafeVarargs
    static <T> T pickOne(T... options) {
        return options[random.nextInt(options.length)];
    }

    static <T> T pickOneItem(List<T> options) {
        return options.get(random.nextInt(options.size()));
    }
}