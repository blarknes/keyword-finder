package com.keywordfinder.utilities;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toUnmodifiableList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;

import com.keywordfinder.model.HttpResponse;

public class HttpClient {

    /**
     * Makes an HTTP request to the specified URL using the given HTTP method.
     * 
     * @param url    The URL to make the request to.
     * @param method The HTTP method to be used for the request.
     * @return An instance of HttpResponse containing the response code and body.
     */
    public HttpResponse makeHttpRequest(URL url, HttpMethod method) {
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method.asString());
            var body = new BufferedReader(new InputStreamReader(conn.getInputStream())).lines()
                    .collect(toUnmodifiableList());
            HttpResponse response = new HttpResponse(conn.getResponseCode(), body);
            conn.disconnect();
            return response;
        } catch (IOException e) {
            return new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR_500, emptyList());
        }
    }

}
