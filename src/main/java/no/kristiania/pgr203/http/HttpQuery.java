package no.kristiania.pgr203.http;

import java.util.HashMap;
import java.util.Map;

public class HttpQuery {
    private Map<String, String> parameters = new HashMap<>();

    private HttpQuery(String query) {
        for (String parameter : query.split("&")) {
            int equalPos = parameter.indexOf('=');
            String parameterName = parameter.substring(0, equalPos);
            String parameterValue = parameter.substring(equalPos+1);
            parameters.put(parameterName, parameterValue);
        }
    }

    public static HttpQuery parse(String query) {
        if (query == null) {
            return null;
        }
        return new HttpQuery(query);
    }

    public boolean containsKey(String name) {
        return parameters.containsKey(name);
    }

    public String get(String name) {
        return parameters.get(name);
    }
}
