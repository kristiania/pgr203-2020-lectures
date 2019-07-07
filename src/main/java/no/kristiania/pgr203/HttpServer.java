package no.kristiania.pgr203;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    private ServerSocket serverSocket;

    public HttpServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void setRootResource(File directory) {

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
                String content = "Hello world";
                String responseBody = "HTTP/1.1 200 OK\r\nConnection: close\r\nContent-length: " + content.length() + "\r\n\r\n" + content;
                clientSocket.getOutputStream().write(responseBody.getBytes());
                clientSocket.getOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer(10080);
        server.start();
    }
}
