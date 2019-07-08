package no.kristiania.pgr203;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    void shouldRespondToHttpRequest() throws IOException, InterruptedException {
        HttpRequest request = new HttpRequest("localhost", server.getPort(), "/hello");
        HttpResponse response = request.execute();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Hello world");
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
}
