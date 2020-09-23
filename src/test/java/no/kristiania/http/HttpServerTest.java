package no.kristiania.http;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

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
        HttpClient client = new HttpClient("localhost", 10004,"/echo?body=HelloWorld");
        assertEquals("HelloWorld", client.getResponseBody());
    }

    @Test
    void shouldReturnFileOnDisk() throws IOException {
        HttpServer server = new HttpServer(10005);
        File documentRoot = new File("target");
        server.setDocumentRoot(documentRoot);
        String fileContent = "Test" + new Date();
        Files.writeString(new File(documentRoot, "Test.txt").toPath(), fileContent);
        HttpClient client = new HttpClient("localhost", 10005,"/Test.txt");
        assertEquals(fileContent, client.getResponseBody());
    }


}