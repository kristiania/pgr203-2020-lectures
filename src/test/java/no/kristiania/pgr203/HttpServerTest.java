package no.kristiania.pgr203;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class HttpServerTest {

    @Test
    void shouldExecuteHttpRequest() throws IOException {
        int statusCode = 200;
        HttpClient request = new HttpClient("/echo?status=" + statusCode);
        HttpResponse response = request.execute();
        assertThat(response.getStatusCode()).isEqualTo(statusCode);
    }

    @Test
    void shouldReturnStatusCode() throws IOException {
        int statusCode = 201;
        HttpClient request = new HttpClient("/echo?status=" + statusCode);
        HttpResponse response = request.execute();
        assertThat(response.getStatusCode()).isEqualTo(statusCode);
    }
}