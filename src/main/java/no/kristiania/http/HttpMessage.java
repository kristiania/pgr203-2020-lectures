package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpMessage {
    private Map<String, String> responseHeaders = new HashMap<>();
    private String startLine;

    public void setHeader(String name, String value) {
        responseHeaders.put(name, value);

    }

    public void setStartLine(String startLine) {
        this.startLine = startLine;
    }

    public void write(Socket socket) throws IOException {
        writeLine(socket, startLine);
        for (Map.Entry<String, String> header : responseHeaders.entrySet()) {
            writeLine(socket, header.getKey() + ": " + header.getValue());
        }
        writeLine(socket, "");
    }

    private void writeLine(Socket socket, String line) throws IOException {
        socket.getOutputStream().write((line + "\r\n").getBytes());
    }
}
