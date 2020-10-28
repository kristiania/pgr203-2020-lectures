package no.kristiania.http;

import no.kristiania.database.Product;
import no.kristiania.database.ProductDao;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpServerTest {

    private JdbcDataSource dataSource;
    private HttpServer server;

    @BeforeEach
    void setUp() throws IOException {
        dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();

        // By using port 0, the operating system will give us an arbitrary port not in use
        server = new HttpServer(0, dataSource);
    }

    @Test
    void shouldReturnSuccessfulStatusCode() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo");
        assertEquals(200, client.getStatusCode());
    }

    @Test
    void shouldReturnUnsuccessfulStatusCode() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo?status=404");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldReturnContentLength() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo?body=HelloWorld");
        assertEquals("10", client.getResponseHeader("Content-Length"));
    }

    @Test
    void shouldReturnResponseBody() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo?body=HelloWorld");
        assertEquals("HelloWorld", client.getResponseBody());
    }

    @Test
    void shouldReturnFileFromDisk() throws IOException {
        File contentRoot = new File("target/test-classes");

        String fileContent = "Hello World " + new Date();
        Files.writeString(new File(contentRoot, "test.txt").toPath(), fileContent);

        HttpClient client = new HttpClient("localhost", server.getPort(), "/test.txt");
        assertEquals(fileContent, client.getResponseBody());
        assertEquals("text/plain", client.getResponseHeader("Content-Type"));
    }

    @Test
    void shouldReturnCorrectContentType() throws IOException {
        File contentRoot = new File("target/test-classes");

        Files.writeString(new File(contentRoot, "index.html").toPath(), "<h2>Hello World</h2>");

        HttpClient client = new HttpClient("localhost", server.getPort(), "/index.html");
        assertEquals("text/html", client.getResponseHeader("Content-Type"));
    }

    @Test
    void shouldReturn404IfFileNotFound() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/notFound.txt");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldPostNewProduct() throws IOException, SQLException {
        String requestBody = "productName=apples&price=10";
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/newProduct", "POST", requestBody);
        assertEquals(200, client.getStatusCode());
        assertThat(server.getProducts())
                .filteredOn(product -> product.getName().equals("apples"))
                .isNotEmpty()
                .satisfies(p -> assertThat(p.get(0).getPrice()).isEqualTo(10));
    }

    @Test
    void shouldReturnExistingProducts() throws IOException, SQLException {
        ProductDao productDao = new ProductDao(dataSource);
        Product product = new Product();
        product.setName("Coconuts");
        product.setPrice(20);
        productDao.insert(product);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/products");
        assertThat(client.getResponseBody()).contains("<li>Coconuts (kr 20.0)</li>");
    }

    @Test
    void shouldPostNewCategory() throws IOException, SQLException {
        String requestBody = "categoryName=candy&color=black";
        HttpClient postClient = new HttpClient("localhost", server.getPort(), "/api/newCategory", "POST", requestBody);
        assertEquals(200, postClient.getStatusCode());

        HttpClient getClient = new HttpClient("localhost", server.getPort(), "/api/categories");
        assertThat(getClient.getResponseBody()).contains("<li>candy</li>");
    }


}