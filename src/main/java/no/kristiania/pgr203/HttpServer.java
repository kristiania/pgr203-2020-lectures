package no.kristiania.pgr203;

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

        String requestMethod = requestLine.substring(0, firstSpace);
        String requestTarget = requestLine.substring(firstSpace+1, secondSpace);
        int questionPos = requestTarget.indexOf('?');
        String absolutePath = questionPos >= 0 ? requestTarget.substring(0, questionPos) : requestTarget;
        String query = questionPos >= 0 ? requestTarget.substring(questionPos+1) : null;

        HttpHeaders responseHeaders = new HttpHeaders();
        String content;
        Path targetFile = rootResource.resolve(absolutePath.substring(1));
        if (absolutePath.equals("/products")) {
            content = productsHtml(getProducts(), parseParameters(query));
        } else if (requestMethod.equals("GET") && absolutePath.equals("/shoppingCart")) {
            content = shoppingCartHtml(getShoppingCart(), getProducts());
        } else if (requestMethod.equals("POST") && absolutePath.equals("/shoppingCart")) {
            String requestBody = HttpMessage.readBytes(clientSocket.getInputStream(), requestHeaders.getContentLength());

            Map<String, String> parameters = parseParameters(requestBody);

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

    private Map<String, String> parseParameters(String requestBody) {
        if (requestBody == null) {
            return null;
        }
        Map<String, String> parameters = new HashMap<>();
        for (String parameter : requestBody.split("&")) {
            int equalPos = parameter.indexOf('=');
            String parameterName = parameter.substring(0, equalPos);
            String parameterValue = parameter.substring(equalPos+1);
            parameters.put(parameterName, parameterValue);
        }
        return parameters;
    }

    private String productsHtml(List<Product> products, Map<String, String> query) {
        Integer categoryId = null;
        if (query != null && query.containsKey("productCategory")) {
            categoryId = Integer.parseInt(query.get("productCategory"));
        }
        StringBuilder productListing = new StringBuilder("<div>");
        for (Product product : products) {
            if (categoryId != null && product.getCategoryId() != categoryId) {
                continue;
            }
            productListing.append("<div><div>")
                    .append(product.getName())
                    .append("</div><button name='productId' value='")
                    .append(product.getId())
                    .append("'>Add to shopping to cart</button></div>");
        }
        productListing.append("</div>");
        return productListing.toString();
    }

    String shoppingCartHtml(Map<Integer, Integer> shoppingCart, List<Product> products) {
        StringBuilder shoppingCartContent = new StringBuilder("<div>");
        for (Map.Entry<Integer, Integer> entry : shoppingCart.entrySet()) {
            String productName = null;
            for (Product product : products) {
                if (product.getId() == entry.getKey()) {
                    productName = product.getName();
                }
            }
            shoppingCartContent.append("<div>")
                    .append(entry.getValue())
                    .append(" x ")
                    .append(productName)
                    .append("</div>");
        }
        shoppingCartContent.append("</div>");
        return shoppingCartContent.toString();
    }

    List<Product> getProducts() {
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
        server.setRootResource(Path.of("src/main/resources/webapp"));
        server.start();
    }

    Map<Integer, Integer> getShoppingCart() {
        return shoppingCart;
    }
}
