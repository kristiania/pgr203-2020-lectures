package no.kristiania.pgr203;

import no.kristiania.pgr203.http.server.DirectoryHttpHandler;
import no.kristiania.pgr203.http.HttpPostRequest;
import no.kristiania.pgr203.http.HttpRequest;
import no.kristiania.pgr203.http.HttpResponse;
import no.kristiania.pgr203.http.server.HttpServer;
import no.kristiania.pgr203.webshop.AddToShoppingCartHandler;
import no.kristiania.pgr203.webshop.Product;
import no.kristiania.pgr203.webshop.ShowProductsHandler;
import no.kristiania.pgr203.webshop.ShowShoppingCartHandler;
import no.kristiania.pgr203.webshop.WebshopServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class HttpServerTest {

    private Random random = new Random();
    private HttpServer server;

    @BeforeEach
    void setUp() throws IOException {
        server = new HttpServer(0);
        server.start();
    }

    @Test
    void shouldServeFiles() throws IOException {
        Path testDirectory = Paths.get("target/test/test-" + System.currentTimeMillis());
        Files.createDirectories(testDirectory);
        server.addHandler(new DirectoryHttpHandler(testDirectory));

        String content = "Today's lucky numbers: " + random.nextInt(100) + " " + random.nextInt(100);
        Files.writeString(testDirectory.resolve("dummy.txt"), content);

        HttpRequest request = new HttpRequest("localhost", server.getPort(), "/dummy.txt");
        HttpResponse response = request.execute();
        assertThat(response.getBody()).isEqualTo(content);
    }


    @Test
    void shouldRespond404WhenNotFound() throws IOException {
        HttpRequest request = new HttpRequest("localhost", server.getPort(), "/hello");
        HttpResponse response = request.execute();
        assertThat(response.getStatusCode()).isEqualTo(404);
    }

    @Test
    void shouldReturnProducts() throws IOException {
        server.addHandler(new ShowProductsHandler(WebshopServer.getProducts()));
        HttpRequest request = new HttpRequest("localhost", server.getPort(), "/products");
        HttpResponse response = request.execute();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody())
                .contains(">Apples<")
                .contains(">Bananas<")
        ;
    }

    @Test
    void shouldFilterProducts() throws IOException {
        server.addHandler(new ShowProductsHandler(WebshopServer.getProducts()));
        HttpRequest request = new HttpRequest("localhost", server.getPort(), "/products?productCategory=2");
        HttpResponse response = request.execute();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody())
                .doesNotContain(">Apples<")
                .contains(">Chocolate<")
                .contains(">Ice cream<")
        ;
    }

    @Test
    void shouldAddToShoppingCart() throws IOException {
        HashMap<Integer, Integer> shoppingCart = new HashMap<>();
        server.addHandler(new AddToShoppingCartHandler(shoppingCart));

        HttpPostRequest request = new HttpPostRequest("localhost", server.getPort(), "/shoppingCart");
        request.setContent("productId=2");

        HttpResponse response = request.execute();
        assertThat(response.getStatusCode()).isEqualTo(302);
        assertThat(response.getHeader("Location"))
                .isEqualTo("http://localhost:" + server.getPort() + "/products.html");

        assertThat(shoppingCart.get(2)).isEqualTo(1);
    }

    @Test
    void shouldAddProductMultipleTimesToCart() throws IOException {
        HashMap<Integer, Integer> shoppingCart = new HashMap<>();
        server.addHandler(new AddToShoppingCartHandler(shoppingCart));

        HttpPostRequest request = new HttpPostRequest("localhost", server.getPort(), "/shoppingCart");

        request.setContent("productId=1&quantity=3");
        assertThat(request.execute().getStatusCode()).isEqualTo(302);

        request.setContent("productId=1&quantity=2");
        assertThat(request.execute().getStatusCode()).isEqualTo(302);

        assertThat(shoppingCart.get(1)).isEqualTo(5);
    }

    @Test
    void shouldShowShoppingCart() throws IOException {
        HashMap<Integer, Integer> shoppingCart = new HashMap<>();
        server.addHandler(new ShowShoppingCartHandler(shoppingCart, WebshopServer.getProducts()));
        shoppingCart.put(1, 10);
        shoppingCart.put(3, 1);

        HttpRequest request = new HttpRequest("localhost", server.getPort(), "/shoppingCart");
        String body = request.execute().getBody();

        assertThat(body).isEqualTo(new ShowShoppingCartHandler(shoppingCart, WebshopServer.getProducts()).shoppingCartHtml());
    }

    @Test
    void shouldFormatShoppingCart() {
        Product apples = new Product(10, "Apples", 1);
        Product coconuts = new Product(100, "Coconuts", 1);

        Map<Integer, Integer> shoppingCart = new HashMap<>();
        shoppingCart.put(apples.getId(), 10);
        shoppingCart.put(coconuts.getId(), 3);

        assertThat(new ShowShoppingCartHandler(shoppingCart, Arrays.asList(apples, coconuts)).shoppingCartHtml())
                .contains(">10 x Apples<")
                .contains(">3 x Coconuts<");
    }
}
