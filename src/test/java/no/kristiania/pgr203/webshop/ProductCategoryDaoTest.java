package no.kristiania.pgr203.webshop;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static no.kristiania.pgr203.webshop.ProductDaoTest.pickOne;
import static org.assertj.core.api.Assertions.assertThat;

class ProductCategoryDaoTest {

    @Test
    void shouldListInsertedCategories() throws SQLException {
        ProductCategoryDao dao = new ProductCategoryDao(ProductDaoTest.createDataSource());

        ProductCategory category = sampleCategory();
        dao.insert(category);
        assertThat(category).hasNoNullFieldsOrProperties();
        assertThat(dao.listAll())
                .contains(category);
    }

    private ProductCategory sampleCategory() {
        var category = new ProductCategory();
        category.setName(pickOne("A", "B", "C", "D", "E", "F", "G", "H"));
        return category;

    }
}