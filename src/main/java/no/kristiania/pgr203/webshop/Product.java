package no.kristiania.pgr203.webshop;

public class Product {
    private final int id;
    private final String name;
    private int categoryId;

    public Product(int id, String name, int categoryId) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
    }

    public int getId() {
        return id;
    }

    String getName() {
        return name;
    }

    int getCategoryId() {
        return categoryId;
    }
}
