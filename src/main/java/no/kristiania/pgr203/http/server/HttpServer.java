package no.kristiania.pgr203.http.server;

import no.kristiania.pgr203.http.HttpHeaders;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static no.kristiania.pgr203.http.HttpMessage.readLine;

public class HttpServer {

    private ServerSocket serverSocket;

    private final List<HttpRequestHandler> handlers = new ArrayList<>();

    public HttpServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void addHandler(HttpRequestHandler handler) {
        handlers.add(handler);
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void start() {
        new Thread(this::serverThread).start();
    }

    private void serverThread() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                try {
                    handleRequest(clientSocket);
                } catch (Exception e) {
                    e.printStackTrace();
                    clientSocket.getOutputStream().write("HTTP/1.1 500 Server Error\r\n\r\n".getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRequest(Socket clientSocket) throws IOException {
        String requestLine = readLine(clientSocket.getInputStream());
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.parse(clientSocket.getInputStream());

        int firstSpace = requestLine.indexOf(' ');
        int secondSpace = requestLine.indexOf(' ', firstSpace+1);

        String requestMethod = requestLine.substring(0, firstSpace);
        String requestTarget = requestLine.substring(firstSpace+1, secondSpace);
        int questionPos = requestTarget.indexOf('?');
        String absolutePath = questionPos >= 0 ? requestTarget.substring(0, questionPos) : requestTarget;
        String query = questionPos >= 0 ? requestTarget.substring(questionPos+1) : null;

        HttpRequestHandler handler = findRequestHandler(requestMethod, absolutePath);
        handler
                .execute(absolutePath, query, requestHeaders, clientSocket.getInputStream())
                .write(clientSocket.getOutputStream(), "localhost" + ":" + getPort());
    }

    private HttpRequestHandler findRequestHandler(String requestMethod, String absolutePath) {
        for (HttpRequestHandler handler : handlers) {
            if (handler.canHandle(requestMethod, absolutePath)) {
                return handler;
            }
        }

        return new HttpNotFoundHandler();
    }
}
