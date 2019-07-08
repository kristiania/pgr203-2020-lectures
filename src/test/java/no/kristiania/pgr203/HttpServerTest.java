package no.kristiania.pgr203;

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
        server.setRootResource(testDirectory);

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
        HttpRequest request = new HttpRequest("localhost", server.getPort(), "/products");
        HttpResponse response = request.execute();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody())
                .contains(">Apples<")
                .contains(">Bananas<")
        ;
    }

    @Test
    void shouldAddToShoppingCart() throws IOException {
        HttpPostRequest request = new HttpPostRequest("localhost", server.getPort(), "/shoppingCart");
        request.setContent("productId=2");

        HttpResponse response = request.execute();
        assertThat(response.getStatusCode()).isEqualTo(302);
        assertThat(response.getHeader("Location"))
                .isEqualTo("http://localhost:" + server.getPort() + "/products.html");

        assertThat(server.getShoppingCart().get(2)).isEqualTo(1);
    }

    @Test
    void shouldAddProductMultipleTimesToCart() throws IOException {
        HttpPostRequest request = new HttpPostRequest("localhost", server.getPort(), "/shoppingCart");

        request.setContent("productId=1&quantity=3");
        assertThat(request.execute().getStatusCode()).isEqualTo(302);

        request.setContent("productId=1&quantity=2");
        assertThat(request.execute().getStatusCode()).isEqualTo(302);

        assertThat(server.getShoppingCart().get(1)).isEqualTo(5);
    }

    @Test
    void shouldShowShoppingCart() throws IOException {
        server.getShoppingCart().put(1, 10);
        server.getShoppingCart().put(3, 1);

        HttpRequest request = new HttpRequest("localhost", server.getPort(), "/shoppingCart");
        String body = request.execute().getBody();

        assertThat(body).isEqualTo(server.shoppingCartHtml(server.getShoppingCart(), server.getProducts()));
    }

    @Test
    void shouldFormatShoppingCart() {
        Product apples = new Product(10, "Apples");
        Product coconuts = new Product(100, "Coconuts");

        Map<Integer, Integer> shoppingCart = new HashMap<>();
        shoppingCart.put(apples.getId(), 10);
        shoppingCart.put(coconuts.getId(), 3);

        assertThat(server.shoppingCartHtml(shoppingCart, Arrays.asList(apples, coconuts)))
                .contains(">10 x Apples<")
                .contains(">3 x Coconuts<");
    }
}
