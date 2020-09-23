package no.kristiania.http;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpServerTest {

    @Test
    void shouldReturnSuccessfulStatusCode() throws IOException {
        new HttpServer(10001);
        HttpClient client = new HttpClient("localhost", 10001, "/echo");
        assertEquals(200, client.getStatusCode());
    }

    @Test
    void shouldReturnUnsuccessfulStatusCode() throws IOException {
        new HttpServer(10002);
        HttpClient client = new HttpClient("localhost", 10002, "/echo?status=404");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldReturnContentLength() throws IOException {
        new HttpServer(10003);
        HttpClient client = new HttpClient("localhost", 10003, "/echo?body=HelloWorld");
        assertEquals("10", client.getResponseHeader("Content-Length"));
    }

    @Test
    void shouldReturnResponseBody() throws IOException {
        new HttpServer(10004);
        HttpClient client = new HttpClient("localhost", 10004, "/echo?body=HelloWorld");
        assertEquals("HelloWorld", client.getResponseBody());
    }

    @Test
    void shouldReturnFileFromDisk() throws IOException {
        HttpServer server = new HttpServer(10005);
        File contentRoot = new File("target/");
        server.setContentRoot(contentRoot);

        String fileContent = "Hello World " + new Date();
        Files.writeString(new File(contentRoot, "test.txt").toPath(), fileContent);

        HttpClient client = new HttpClient("localhost", 10005, "/test.txt");
        assertEquals(fileContent, client.getResponseBody());
        assertEquals("text/plain", client.getResponseHeader("Content-Type"));
    }

    @Test
    void shouldReturnCorrectContentType() throws IOException {
        HttpServer server = new HttpServer(10006);
        File contentRoot = new File("target/");
        server.setContentRoot(contentRoot);

        Files.writeString(new File(contentRoot, "index.html").toPath(), "<h2>Hello World</h2>");

        HttpClient client = new HttpClient("localhost", 10006, "/index.html");
        assertEquals("text/html", client.getResponseHeader("Content-Type"));
    }

    @Test
    void shouldReturn404IfFileNotFound() throws IOException {
        HttpServer server = new HttpServer(10007);
        File contentRoot = new File("target/");
        server.setContentRoot(contentRoot);

        HttpClient client = new HttpClient("localhost", 10007, "/notFound.txt");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldPostNewProduct() throws IOException {
        HttpServer server = new HttpServer(10008);
        HttpClient client = new HttpClient("localhost", 10008, "/api/newProduct", "POST", "productName=apples&price=10");
        assertEquals(200, client.getStatusCode());
        assertEquals(List.of("apples"), server.getProductNames());
    }

}