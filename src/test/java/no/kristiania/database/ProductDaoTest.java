package no.kristiania.database;

import no.kristiania.http.HttpMessage;
import no.kristiania.http.ProductOptionsController;
import no.kristiania.http.UpdateProductController;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductDaoTest {

    private ProductDao productDao;
    private static Random random = new Random();
    private ProductCategoryDao categoryDao;

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        productDao = new ProductDao(dataSource);
        categoryDao = new ProductCategoryDao(dataSource);
    }

    @Test
    void shouldListInsertedProducts() throws SQLException {
        Product product1 = exampleProduct();
        Product product2 = exampleProduct();
        productDao.insert(product1);
        productDao.insert(product2);
        assertThat(productDao.list())
                .extracting(Product::getName)
                .contains(product1.getName(), product2.getName());
    }

    @Test
    void shouldQueryProductsByCategory() throws SQLException {
        ProductCategory category = CategoryDaoTest.exampleCategory();
        categoryDao.insert(category);
        ProductCategory otherCategory = CategoryDaoTest.exampleCategory();
        categoryDao.insert(otherCategory);

        Product matchingProduct = exampleProduct();
        matchingProduct.setCategoryId(category.getId());
        productDao.insert(matchingProduct);
        Product nonMatchingProduct = exampleProduct();
        nonMatchingProduct.setCategoryId(otherCategory.getId());
        productDao.insert(nonMatchingProduct);

        assertThat(productDao.queryProductsByCategoryId(category.getId()))
                .extracting(Product::getId)
                .contains(matchingProduct.getId())
                .doesNotContain(nonMatchingProduct.getId());
    }

    @Test
    void shouldRetrieveAllProductProperties() throws SQLException {
        productDao.insert(exampleProduct());
        productDao.insert(exampleProduct());
        Product product = exampleProduct();
        productDao.insert(product);
        assertThat(product).hasNoNullFieldsOrPropertiesExcept("categoryId");
        assertThat(productDao.retrieve(product.getId()))
                .usingRecursiveComparison()
                .isEqualTo(product);
    }

    @Test
    void shouldReturnProductsAsOptions() throws SQLException {
        ProductOptionsController controller = new ProductOptionsController(productDao);
        Product product = ProductDaoTest.exampleProduct();
        productDao.insert(product);

        assertThat(controller.getBody())
                .contains("<option value=" + product.getId() + ">" + product.getName() + "</option>");
    }

    @Test
    void shouldUpdateExistingProductWithNewCategory() throws IOException, SQLException {
        UpdateProductController controller = new UpdateProductController(productDao);

        Product product = exampleProduct();
        productDao.insert(product);

        ProductCategory category = CategoryDaoTest.exampleCategory();
        categoryDao.insert(category);

        String body = "productId=" + product.getId() + "&categoryId=" + category.getId();

        HttpMessage response = controller.handle(new HttpMessage(body));
        assertThat(productDao.retrieve(product.getId()).getCategoryId())
                .isEqualTo(category.getId());
        assertThat(response.getStartLine())
                .isEqualTo("HTTP/1.1 302 Redirect");
        assertThat(response.getHeaders().get("Location"))
                .isEqualTo("http://localhost:8080/index.html");
    }

    public static Product exampleProduct() {
        Product product = new Product();
        product.setName(exampleProductName());
        product.setPrice(10.50 + random.nextInt(20));
        return product;
    }

    private static String exampleProductName() {
        String[] options = {"Apples", "Bananas", "Coconuts", "Dates"};
        return options[random.nextInt(options.length)];
    }
}