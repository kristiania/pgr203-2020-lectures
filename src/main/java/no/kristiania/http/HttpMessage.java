package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpMessage {
    private String startLine;
    private Map<String, String> headers = new HashMap<>();

    public HttpMessage(String startLine) {
        this.startLine = startLine;
    }

    public static String readLine(Socket socket) throws IOException {
        StringBuilder line = new StringBuilder();
        int c;
        while ((c = socket.getInputStream().read()) != -1) {
            // each line ends with \r\n (CRLF - carriage return, line feed)
            if (c == '\r') {
                socket.getInputStream().read(); // read and ignore the following \n
                break;
            }
            line.append((char)c);
        }
        return line.toString();
    }

    public static HttpMessage read(Socket socket) throws IOException {
        HttpMessage message = new HttpMessage(readLine(socket));
        message.readHeaders(socket);
        return message;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public void write(Socket socket) throws IOException {
        writeLine(socket, startLine);
        for (Map.Entry<String, String> header : headers.entrySet()) {
            writeLine(socket, header.getKey() + ": " + header.getValue());
        }
        writeLine(socket, "");
    }

    private void writeLine(Socket socket, String startLine) throws IOException {
        socket.getOutputStream().write((startLine + "\r\n").getBytes());
    }

    public String getStartLine() {
        return startLine;
    }

    public String getHeader(String headerName) {
        return headers.get(headerName);
    }

    public void readHeaders(Socket socket) throws IOException {
        // After status line the message contains 0 or more response header
        String headerLine;
        while (!(headerLine = HttpMessage.readLine(socket)).isEmpty()) {
            // http headers consist of "name: value"
            int colonPos = headerLine.indexOf(':');
            // parse header
            String headerName = headerLine.substring(0, colonPos);
            String headerValue = headerLine.substring(colonPos+1).trim();

            // store headers
            setHeader(headerName, headerValue);
        }
    }
}
