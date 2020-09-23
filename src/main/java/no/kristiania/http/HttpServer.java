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
            String response = "HTTP/1.1 " + "200" + " OK\r\n" +
                    "Content-Length: " + file.length() + "\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "\r\n";

            clientSocket.getOutputStream().write(response.getBytes());
            new FileInputStream(file).transferTo(clientSocket.getOutputStream());
            return;
        }

        String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                body;

        clientSocket.getOutputStream().write(response.getBytes());
    }

    public static void main(String[] args) throws IOException {
        new HttpServer(8080);
    }

    public void setDocumentRoot(File documentRoot) {
        this.documentRoot = documentRoot;
    }
}
