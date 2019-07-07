package no.kristiania.pgr203;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpServer {

    private ServerSocket serverSocket;
    private Path rootResource = Paths.get(".");

    public HttpServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void setRootResource(Path directory) {
        rootResource = directory;
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
        if (Files.isRegularFile(targetFile)) {
            content = Files.readString(targetFile);
        } else {
            content = "Hello world";
        }
        String responseBody = "HTTP/1.1 200 OK\r\nConnection: close\r\nContent-length: " + content.length() + "\r\n\r\n" + content;
        clientSocket.getOutputStream().write(responseBody.getBytes());
        clientSocket.getOutputStream().flush();
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer(10080);
        server.start();
    }


    private String readLine(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();
        int c;
        while ((c = inputStream.read()) != -1) {
            if (c == '\r') {
                inputStream.read(); // \n
                return result.toString();
            }
            result.append((char)c);
        }
        return result.toString();
    }
}
