package no.kristiania.pgr203;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class HttpResponse {
    private final String statusLine;
    private final List<String> headerLines = new ArrayList<>();
    private String serverResponse;

    public HttpResponse(CharSequence serverResponse) {
        this.serverResponse = serverResponse.toString();
        String[] responseLines = this.serverResponse.split("\r\n");

        int i=0;
        this.statusLine = responseLines[i++];
        while (i < responseLines.length && !responseLines[i].trim().isEmpty()) {
            this.headerLines.add(responseLines[i++]);
        }
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
        String header = getHeader("Content-Type");
        int semiColonPos = header.indexOf(';');
        return header.substring(0, semiColonPos);
    }

    private String getHeader(String fieldName) {
        for (String headerLine : headerLines) {
            if (headerLine.startsWith(fieldName+ ":")) {
                int colonPos = headerLine.indexOf(':');
                return headerLine.substring(colonPos+1).trim();
            }
        }
        return null;
    }
}
