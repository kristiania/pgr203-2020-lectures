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

    @Test
    void shouldGetContentType() throws IOException {
        HttpClient req = new HttpClient("/echo");
        assertThat(req.execute().getContentType()).isEqualTo("text/html");
    }

    @Test
    void shouldGetCorrectContentType() throws IOException {
        HttpClient req = new HttpClient("/echo?Content-Type=text%2Fplain");
        assertThat(req.execute().getContentType()).isEqualTo("text/plain");
    }

    @Test
    void shouldGetContentLength() throws IOException {
        String requestText = "This+is+a+test", otherText = "Hello";
        assertThat(new HttpClient("/echo?body=" + requestText).execute().getContentLength())
                .isEqualTo(requestText.length());
        assertThat(new HttpClient("/echo?body=" + otherText).execute().getContentLength())
                .isEqualTo(otherText.length());
    }

    @Test
    void shouldGetResponseBody() throws IOException {
        HttpClient req = new HttpClient("/echo");
        assertThat(req.execute().getBody()).isEqualTo("None");
    }

}