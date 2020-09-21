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
}
