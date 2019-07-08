package no.kristiania.pgr203;

import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("WeakerAccess")
public class HttpResponse {
    private final String statusLine;
    private final HttpHeaders headers = new HttpHeaders();
    private String body;

    public HttpResponse(InputStream inputStream) throws IOException {
        statusLine = HttpMessage.readLine(inputStream);
        headers.parse(inputStream);
        if (getHeader("Content-Length") != null) {
            body = HttpMessage.readBytes(inputStream, getContentLength());
        }
    }

    public int getStatusCode() {
        int firstSpace = statusLine.indexOf(' ');
        int secondSpace = statusLine.indexOf(' ', firstSpace + 1);

        return Integer.parseInt(statusLine.substring(firstSpace + 1, secondSpace));
    }

    public String getContentType() {
        String header = getHeader("Content-type");
        int semiColonPos = header.indexOf(';');
        return header.substring(0, semiColonPos);
    }

    public int getContentLength() {
        return headers.getContentLength();
    }

    String getHeader(String fieldName) {
        return headers.getHeader(fieldName);
    }

    public String getBody() {
        return body;
    }

}
