package no.kristiania.pgr203;

class Product {
    private final int id;
    private final String name;
    private int categoryId;

    Product(int id, String name, int categoryId) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
    }

    int getId() {
        return id;
    }

    String getName() {
        return name;
    }

    int getCategoryId() {
        return categoryId;
    }
}
