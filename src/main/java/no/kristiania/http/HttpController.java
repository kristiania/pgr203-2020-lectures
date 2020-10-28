package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public interface HttpController {
    void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException;
}
