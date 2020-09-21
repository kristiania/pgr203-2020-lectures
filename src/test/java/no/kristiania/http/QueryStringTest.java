package no.kristiania.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * "query string" is the part of an URL that is after a "?" (if any). For example, if you
 * go to https://www.google.com/search?q=test Google will search for the term "test".
 * The query string is "q=test" with one parameter named "q" with the value "test".
 * If you go to http://urlecho.appspot.com/echo?status=200&body=Hello%20world!, the
 * query string is "status=200&body=Hello%20world!" with two parameters.
 * The first parameter is named "status" and has value "200" while the second is named
 * "body" and has the value "Hello world!". Spaces are converted ("escaped")
 */
public class QueryStringTest {

    @Test
    void shouldReturnQueryParameter() {
        QueryString queryString = new QueryString("status=200");
        assertEquals("200", queryString.getParameter("status"));
    }

    @Test
    void shouldReturnNullForMissingParameter() {
        QueryString queryString = new QueryString("status=404");
        assertNull(queryString.getParameter("body"));
    }

    @Test
    void shouldParseSeveralParameters() {
        QueryString queryString = new QueryString("status=200&body=Hello");
        assertEquals("200", queryString.getParameter("status"));
        assertEquals("Hello", queryString.getParameter("body"));
    }

    @Test
    void shouldSerializeQueryString() {
        QueryString queryString = new QueryString("status=200");
        assertEquals("?status=200", queryString.getQueryString());
        queryString.addParameter("body", "Hello");
        assertEquals("?status=200&body=Hello", queryString.getQueryString());
    }

}
