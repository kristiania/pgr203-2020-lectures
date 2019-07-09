package no.kristiania.pgr203.webshop;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductDaoTest {

    @Test
    void shouldRetrieveInsertedProduct() {
        Product product = sampleProduct();

        ProductDao dao = new ProductDao();
        dao.insert(product);

        assertThat(dao.retrieve(product.getId()))
                .isEqualToComparingFieldByField(product);
    }

    private Product sampleProduct() {
        return new Product();
    }
}