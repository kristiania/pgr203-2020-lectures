package no.kristiania.pgr203;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    void shouldExecuteHttpRequest() throws IOException {
        int statusCode = 200;
        HttpRequest request = new HttpRequest("/echo?status=" + statusCode);
        HttpResponse response = request.execute();
        assertThat(response.getStatusCode()).isEqualTo(statusCode);
    }

    @Test
    void shouldReturnStatusCode() throws IOException {
        int statusCode = 201;
        HttpRequest request = new HttpRequest("/echo?status=" + statusCode);
        HttpResponse response = request.execute();
        assertThat(response.getStatusCode()).isEqualTo(statusCode);
    }

    @Test
    void shouldGetContentType() throws IOException {
        HttpRequest req = new HttpRequest("/echo");
        assertThat(req.execute().getContentType()).isEqualTo("text/html");
    }

    @Test
    void shouldGetCorrectContentType() throws IOException {
        HttpRequest req = new HttpRequest("/echo?Content-Type=text%2Fplain");
        assertThat(req.execute().getContentType()).isEqualTo("text/plain");
    }

    @Test
    void shouldGetContentLength() throws IOException {
        String requestText = "This+is+a+test", otherText = "Hello";
        assertThat(new HttpRequest("/echo?body=" + requestText).execute().getContentLength())
                .isEqualTo(requestText.length());
        assertThat(new HttpRequest("/echo?body=" + otherText).execute().getContentLength())
                .isEqualTo(otherText.length());
    }

    @Test
    void shouldGetResponseBody() throws IOException {
        assertThat(new HttpRequest("/echo").execute().getBody()).isEqualTo("None");
        assertThat(new HttpRequest("/echo?body=This+is+a+test").execute().getBody())
                .isEqualTo("This is a test");
    }

}