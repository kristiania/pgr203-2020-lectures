package no.kristiania.pgr203.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class HttpHeaders {
    private final List<String> headerLines = new ArrayList<>();

    private void addLine(String headerLine) {
        headerLines.add(headerLine);
    }

    String getHeader(String fieldName) {
        for (String headerLine : headerLines) {
            int colonPos = headerLine.indexOf(':');
            String lineFieldName = headerLine.substring(0, colonPos).trim();
            if (lineFieldName.equalsIgnoreCase(fieldName)) {
                return headerLine.substring(colonPos+1).trim();
            }
        }
        return null;
    }

    public void parse(InputStream inputStream) throws IOException {
        String headerLine;
        while (!(headerLine = HttpMessage.readLine(inputStream)).trim().isEmpty()) {
            addLine(headerLine);
        }
    }

    public void add(String fieldName, String fieldValue) {
        addLine(fieldName + ": " + fieldValue);
    }

    public void setContentLength(long length) {
        add("Content-Length", String.valueOf(length));
    }

    public void write(OutputStream outputStream) throws IOException {
        for (String headerLine : headerLines) {
            outputStream.write((headerLine + "\r\n").getBytes());
        }
    }

    public int getContentLength() {
        return Integer.parseInt(getHeader("Content-Length"));
    }
}
