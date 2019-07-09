package no.kristiania.pgr203.webshop;

import java.util.ArrayList;
import java.util.List;

public class ProductDao {
    private List<Product> tmpProducts = new ArrayList<>();

    public void insert(Product product) {
        tmpProducts.add(product);
    }

    public Product retrieve(int id) {
        for (Product product : tmpProducts) {
            if (product.getId() == id) {
                return product;
            }
        }

        return null;
    }
}
