package no.kristiania.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

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
        int contentLength = 29;
        String statusCode = "200";

        int questionPos = requestTarget.indexOf('?');
        if (questionPos != -1) {
            String queryString = requestTarget.substring(questionPos + 1);
            QueryString qs = new QueryString(queryString);
            int equalPos = queryString.indexOf('=');
            String parameterName = queryString.substring(0, equalPos);
            String parameterValue = queryString.substring(equalPos + 1);
            statusCode = qs.getParameter("status");
            if (statusCode == null)
                statusCode = "200";
            if (qs.getParameter("body") != null)
                contentLength = qs.getParameter("body").length();
        }


        String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                "Content-Length: " + contentLength + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                "Hello <strong>World</strong>!";

        clientSocket.getOutputStream().write(response.getBytes());
    }

    public static void main(String[] args) throws IOException {
        new HttpServer(8080);
    }
}
