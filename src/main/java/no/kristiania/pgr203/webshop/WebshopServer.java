package no.kristiania.pgr203.webshop;

import no.kristiania.pgr203.http.server.DirectoryHttpHandler;
import no.kristiania.pgr203.http.server.HttpServer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebshopServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer(10080);

        Map<Integer, Integer> shoppingCart = new HashMap<>();

        server.addHandler(new AddToShoppingCartHandler(shoppingCart));
        server.addHandler(new ShowShoppingCartHandler(shoppingCart, getProducts()));
        server.addHandler(new ShowProductsHandler(getProducts()));
        server.addHandler(new DirectoryHttpHandler(Path.of("src/main/resources/webapp")));

        server.start();
    }

    public static List<Product> getProducts() {
        return Arrays.asList(
                new Product(1, "Apples", 1),
                new Product(2, "Bananas", 1),
                new Product(3, "Coconuts", 1),
                new Product(4, "Chocolate", 2),
                new Product(5, "Ice cream", 2)
        );
    }

}
