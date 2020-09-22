package no.kristiania.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class HttpServer {

    private File documentRoot;
    private List<String> productNames = new ArrayList<>();

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
        String requestLine = HttpMessage.readLine(clientSocket);
        System.out.println(requestLine);

        String requestMethod = requestLine.split(" ")[0];
        String requestTarget = requestLine.split(" ")[1];

        if (requestMethod.equals("POST")) {
            HttpMessage requestMessage = new HttpMessage(requestLine);
            requestMessage.readHeaders(clientSocket);

            int contentLength = Integer.parseInt(requestMessage.getHeader("Content-Length"));
            StringBuilder body = new StringBuilder();
            for (int i = 0; i < contentLength; i++) {
                // Read content body based on content-length
                body.append((char)clientSocket.getInputStream().read());
            }
            QueryString requestForm = new QueryString(body.toString());
            productNames.add(requestForm.getParameter("productName"));

            HttpMessage responseMessage = new HttpMessage("HTTP/1.1 200 OK");
            responseMessage.write(clientSocket);
            return;
        }

        String statusCode = null;
        String body = null;

        int questionPos = requestTarget.indexOf('?');
        if (questionPos != -1) {
            QueryString queryString = new QueryString(requestTarget.substring(questionPos + 1));
            statusCode = queryString.getParameter("status");
            body = queryString.getParameter("body");
        } else if (!requestTarget.equals("/echo")) {
            File targetFile = new File(documentRoot, requestTarget);

            if (!targetFile.exists()) {
                writeResponse(clientSocket, "404", requestTarget + " not found");
                return;
            }

            HttpMessage responseMessage = new HttpMessage("HTTP/1.1 200 OK");
            responseMessage.setHeader("Content-Length", String.valueOf(targetFile.length()));
            responseMessage.setHeader("Content-Type", "text/html");

            if (targetFile.getName().endsWith(".txt")) {
                responseMessage.setHeader("Content-Type", "text/plain");
            }
            responseMessage.write(clientSocket);

            try (FileInputStream inputStream = new FileInputStream(targetFile)) {
                inputStream.transferTo(clientSocket.getOutputStream());
            }
        }

        if (statusCode == null) statusCode = "200";
        if (body == null) body = "Hello <strong>World</strong>!";

        writeResponse(clientSocket, statusCode, body);
    }

    private void writeResponse(Socket clientSocket, String statusCode, String body) throws IOException {
        HttpMessage responseMessage = new HttpMessage("HTTP/1.1 " + statusCode + " OK");
        responseMessage.setHeader("Content-Length", String.valueOf(body.length()));
        responseMessage.setHeader("Content-Type", "text/plain");
        responseMessage.write(clientSocket);
        clientSocket.getOutputStream().write(body.getBytes());
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer(8080);
        server.setDocumentRoot(new File("src/main/resources"));
    }

    public void setDocumentRoot(File documentRoot) {
        this.documentRoot = documentRoot;
    }

    public List<String> getProductNames() {
        return productNames;
    }
}
