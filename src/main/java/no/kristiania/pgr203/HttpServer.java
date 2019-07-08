package no.kristiania.pgr203;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static no.kristiania.pgr203.HttpMessage.readLine;

public class HttpServer {

    private ServerSocket serverSocket;
    private Path rootResource = Paths.get(".");

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
                handleRequest(clientSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRequest(Socket clientSocket) throws IOException {
        String requestLine = readLine(clientSocket.getInputStream());

        int firstSpace = requestLine.indexOf(' ');
        int secondSpace = requestLine.indexOf(' ', firstSpace+1);

        String requestTarget = requestLine.substring(firstSpace+1, secondSpace);

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
        } else if (Files.isRegularFile(targetFile)) {
            content = Files.readString(targetFile);
        } else {
            content = "Hello world";
        }

        clientSocket.getOutputStream().write("HTTP/1.1 200 OK\r\n".getBytes());

        HttpHeaders responseHeaders = new HttpHeaders();
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
}
