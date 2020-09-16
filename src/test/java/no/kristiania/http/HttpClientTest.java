package no.kristiania.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class HttpClientTest {

    @Test
    void shouldShowSuccessfulStatusCode() throws IOException {
        HttpClient client = new HttpClient("urlecho.appspot.com", 80, "/echo");
        assertEquals(200, client.getStatusCode());
    }

    @Test
    void shouldShowUnsuccessfulStatusCode() throws IOException {
        HttpClient client = new HttpClient("urlecho.appspot.com", 80, "/echo?status=404");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldReturnResponseHeaders() throws IOException {
        HttpClient client = new HttpClient("urlecho.appspot.com", 80, "/echo?body=Kristiania");
        assertEquals("10", client.getResponseHeader("Content-Length"));
    }

    @Test
    void shouldReturnResponseBody() throws IOException {
        HttpClient client = new HttpClient("urlecho.appspot.com", 80, "/echo?body=Kristiania");
        assertEquals("Kristiania", client.getResponseBody());
    }
}