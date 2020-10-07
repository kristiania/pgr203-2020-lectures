package no.kristiania.database;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class ProductDaoTest {

    @Test
    void shouldListInsertedProducts() {
        ProductDao productDao = new ProductDao();
        String product = exampleProductName();
        productDao.insert(product);
        assertThat(productDao.list()).contains(product);
    }

    private String exampleProductName() {
        String[] options = {"Apples", "Bananas", "Coconuts", "Dates"};
        Random random = new Random();
        return options[random.nextInt(options.length)];
    }
}