package no.kristiania.pgr203;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.kristiania.pgr203.HttpMessage.readLine;

public class HttpServer {

    private ServerSocket serverSocket;
    private Path rootResource = Paths.get(".");
    private Map<Integer, Integer> shoppingCart = new HashMap<>();

    HttpServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    void setRootResource(Path directory) {
        rootResource = directory;
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

        String requestTarget = requestLine.substring(firstSpace+1, secondSpace);

        HttpHeaders responseHeaders = new HttpHeaders();
        String content;
        Path targetFile = rootResource.resolve(requestTarget.substring(1));
        if (requestTarget.equals("/products")) {
            StringBuilder productListing = new StringBuilder("<div>");
            for (Product product : getProducts()) {
                productListing.append("<div><div>")
                        .append(product.getName())
                        .append("</div><button name='productId' value='")
                        .append(product.getId())
                        .append("'>Add to shopping to cart</button></div>");
            }
            productListing.append("</div>");
            content = productListing.toString();
        } else if (requestTarget.equals("/shoppingCart")) {
            String requestBody = HttpMessage.readBytes(clientSocket.getInputStream(), requestHeaders.getContentLength());

            Map<String, String> parameters = new HashMap<>();
            for (String parameter : requestBody.split("&")) {
                int equalPos = parameter.indexOf('=');
                String parameterName = parameter.substring(0, equalPos);
                String parameterValue = parameter.substring(equalPos+1);
                parameters.put(parameterName, parameterValue);
            }

            int quantity = parameters.containsKey("quantity") ? Integer.parseInt(parameters.get("quantity")) : 1;
            int productId = Integer.parseInt(parameters.get("productId"));
            if (!shoppingCart.containsKey(productId)) {
                shoppingCart.put(productId, 0);
            }
            shoppingCart.put(productId, shoppingCart.get(productId) + quantity);
            responseHeaders.add("Location", "http://" + "localhost" + ":" + getPort() + "/products.html");
            clientSocket.getOutputStream().write("HTTP/1.1 302 Redirect\r\n".getBytes());
            responseHeaders.write(clientSocket.getOutputStream());
            clientSocket.getOutputStream().write("\r\n".getBytes());
            clientSocket.getOutputStream().flush();
            return;
        } else if (Files.isRegularFile(targetFile)) {
            content = Files.readString(targetFile);
        } else {
            clientSocket.getOutputStream().write("HTTP/1.1 404 Not found\r\n".getBytes());

            responseHeaders.setContentLength(0);
            responseHeaders.add("Connection", "close");
            responseHeaders.write(clientSocket.getOutputStream());

            clientSocket.getOutputStream().write("\r\n".getBytes());
            clientSocket.getOutputStream().flush();
            return;
        }

        clientSocket.getOutputStream().write("HTTP/1.1 200 OK\r\n".getBytes());

        responseHeaders.setContentLength(content.length());
        responseHeaders.add("Connection", "close");
        responseHeaders.write(clientSocket.getOutputStream());

        clientSocket.getOutputStream().write("\r\n".getBytes());
        clientSocket.getOutputStream().write(content.getBytes());
        clientSocket.getOutputStream().flush();
    }

    private List<Product> getProducts() {
        return Arrays.asList(
                new Product(1, "Apples"),
                new Product(2, "Bananas"),
                new Product(3, "Coconuts")
        );
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer(10080);
        server.setRootResource(Path.of("src/main/resources/webapp"));
        server.start();
    }

    public Map<Integer, Integer> getShoppingCart() {
        return shoppingCart;
    }
}
