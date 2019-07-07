package no.kristiania.pgr203;

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

    void shouldServeFiles() throws IOException {
        HttpServer server = new HttpServer(0);
        Path testDirectory = Paths.get("target/test/test-" + System.currentTimeMillis());
        Files.createDirectories(testDirectory);
        server.setRootResource(testDirectory.toFile());

        String content = "Today's lucky numbers: " + random.nextInt() + " " + random.nextInt();
        Files.writeString(testDirectory.resolve("dummy.txt"), content);

        HttpRequest request = new HttpRequest("localhost", server.getPort(), "/dummy.txt");
        HttpResponse response = request.execute();
        assertThat(response.getBody()).isEqualTo(content);
    }


    @Test
    void shouldRespondToHttpRequest() throws IOException, InterruptedException {
        HttpServer server = new HttpServer(0);
        server.start();
        HttpRequest request = new HttpRequest("localhost", server.getPort(), "/hello");
        HttpResponse response = request.execute();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Hello world");
    }
}
