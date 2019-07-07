package no.kristiania.pgr203;

@SuppressWarnings("WeakerAccess")
public class HttpResponse {
    private String serverResponse;

    public HttpResponse(CharSequence serverResponse) {
        this.serverResponse = serverResponse.toString();
    }

    public int getStatusCode() {
        String statusLine = getStatusLine();
        int firstSpace = statusLine.indexOf(' ');
        int secondSpace = statusLine.indexOf(' ', firstSpace + 1);

        return Integer.parseInt(statusLine.substring(firstSpace + 1, secondSpace));
    }

    private String getStatusLine() {
        return serverResponse.split("\r\n")[0];
    }

    public String getServerResponse() {
        return serverResponse;
    }
}
