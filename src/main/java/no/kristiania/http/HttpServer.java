package no.kristiania.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    private File documentRoot;

    public HttpServer(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);

        new Thread(() -> {
            while (true) {
                try {
                    handleRequest(serverSocket.accept());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void handleRequest(Socket clientSocket) throws IOException {
        String requestLine = HttpClient.readLine(clientSocket);
        System.out.println(requestLine);

        String requestTarget = requestLine.split(" ")[1];

        String body = "Hello <strong>World</strong>!";
        String statusCode = "200";

        int questionPos = requestTarget.indexOf('?');
        if (questionPos != -1) {
            QueryString qs = new QueryString(requestTarget.substring(questionPos + 1));

            if (qs.hasParameter("status")) {
                statusCode = qs.getParameter("status");
            }
            if (qs.hasParameter("body")) {
                body = qs.getParameter("body");
            }
        } else {
            File file = new File(documentRoot, requestTarget);
            HttpMessage responseMessage = new HttpMessage();
            responseMessage.setHeader("Content-Type", "text/plain");
            if (file.toString().endsWith(".html")) {
                responseMessage.setHeader("Content-Type", "text/html");
            }
            responseMessage.setHeader("Content-Length", String.valueOf(file.length()));

            responseMessage.setStartLine("HTTP/1.1 " + "200" + " OK");
            responseMessage.write(clientSocket);

            new FileInputStream(file).transferTo(clientSocket.getOutputStream());
            return;
        }

        HttpMessage responseMessage = new HttpMessage();
        responseMessage.setStartLine("HTTP/1.1 " + statusCode + " OK");
        responseMessage.setHeader("Content-Length", String.valueOf(body.length()));
        responseMessage.setHeader("Content-Type", "text/plain");
        responseMessage.write(clientSocket);

        clientSocket.getOutputStream().write(body.getBytes());
    }

    public static void main(String[] args) throws IOException {
        new HttpServer(8080);
    }

    public void setDocumentRoot(File documentRoot) {
        this.documentRoot = documentRoot;
    }
}
