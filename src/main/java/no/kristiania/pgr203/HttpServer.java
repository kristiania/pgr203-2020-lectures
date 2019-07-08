package no.kristiania.pgr203;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.kristiania.pgr203.HttpMessage.readLine;

public class HttpServer {

    private ServerSocket serverSocket;

    private List<HttpRequestHandler> handlers = new ArrayList<>();

    HttpServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    void addHandler(HttpRequestHandler handler) {
        handlers.add(handler);
    }

    int getPort() {
        return serverSocket.getLocalPort();
    }

    void start() {
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
                .execute(requestMethod, absolutePath, query, requestHeaders, clientSocket.getInputStream())
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

    static List<Product> getProducts() {
        return Arrays.asList(
                new Product(1, "Apples", 1),
                new Product(2, "Bananas", 1),
                new Product(3, "Coconuts", 1),
                new Product(4, "Chocolate", 2),
                new Product(5, "Ice cream", 2)
        );
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer(10080);

        Map<Integer, Integer> shoppingCart = new HashMap<>();

        server.addHandler(new AddToShoppingCartHandler(shoppingCart));
        server.addHandler(new ShowShoppingCartHandler(shoppingCart, getProducts()));
        server.addHandler(new ShowProductsHandler(getProducts()));
        server.handlers.add(new DirectoryHttpHandler(Path.of("src/main/resources/webapp")));

        server.start();
    }
}
