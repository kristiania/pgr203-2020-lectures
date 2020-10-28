package no.kristiania.http;

import no.kristiania.database.ProductDao;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class UpdateProductController implements HttpController {
    public UpdateProductController(ProductDao productDao) {
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {

    }
}
