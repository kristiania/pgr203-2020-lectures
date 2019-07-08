package no.kristiania.pgr203;

import java.io.IOException;
import java.io.InputStream;

class HttpMessage {
    static String readLine(InputStream inputStream) throws IOException {
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
