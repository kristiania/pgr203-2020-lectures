package no.kristiania.pgr203;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class HttpResponse {
    private final String statusLine;
    private final List<String> headerLines = new ArrayList<>();
    private String serverResponse;
    private String body;

    public HttpResponse(InputStream inputStream) throws IOException {
        statusLine = readLine(inputStream);

        String headerLine;
        while (!(headerLine = readLine(inputStream)).trim().isEmpty()) {
            this.headerLines.add(headerLine);
        }

        body = readBytes(inputStream, getContentLength());
    }

    private String readBytes(InputStream inputStream, int contentLength) throws IOException {
        StringBuilder result = new StringBuilder();
        int c;
        while ((c = inputStream.read()) != -1) {
            result.append((char)c);
            if  (result.length() == contentLength) {
                break;
            }
        }
        return result.toString();
    }

    private String readLine(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();
        int c;
        while ((c = inputStream.read()) != -1) {
            if (c == '\r') {
                inputStream.read(); // \n
                return result.toString();
            }
            result.append((char)c);
        }
        return result.toString();
    }

    public int getStatusCode() {
        int firstSpace = statusLine.indexOf(' ');
        int secondSpace = statusLine.indexOf(' ', firstSpace + 1);

        return Integer.parseInt(statusLine.substring(firstSpace + 1, secondSpace));
    }

    public String getServerResponse() {
        return serverResponse;
    }

    public String getContentType() {
        String header = getHeader("Content-type");
        int semiColonPos = header.indexOf(';');
        return header.substring(0, semiColonPos);
    }

    public int getContentLength() {
        return Integer.parseInt(getHeader("Content-length"));
    }

    private String getHeader(String fieldName) {
        for (String headerLine : headerLines) {
            int colonPos = headerLine.indexOf(':');
            String lineFieldName = headerLine.substring(0, colonPos).trim();
            if (lineFieldName.equalsIgnoreCase(fieldName)) {
                return headerLine.substring(colonPos+1).trim();
            }
        }
        return null;
    }

    public String getBody() {
        return body;
    }

}
