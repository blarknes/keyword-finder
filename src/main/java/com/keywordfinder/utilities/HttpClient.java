package com.keywordfinder.utilities;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.eclipse.jetty.http.HttpMethod.POST;
import static org.eclipse.jetty.http.HttpStatus.INTERNAL_SERVER_ERROR_500;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.keywordfinder.model.HttpResponse;

public class HttpClient {

    private final Logger log = LoggerFactory.getLogger(HttpClient.class);

    /**
     * Makes an HTTP request with a body to the specified URL using the given HTTP
     * method. And returns a instance of the HttpResponse object containing the
     * information inside the response body.
     * 
     * @param url    The URL to make the request to.
     * @param method The HTTP method to be used for the request.
     * @param body   The body of the request.
     * @return
     */
    public HttpResponse makeHttpRequest(final URL url, final HttpMethod method, final String body) {
        try {
            var conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method.asString());

            if (method == POST && body != null) {
                setRequestBody(conn, body);
            }

            /**
             * The responseBody will be the inputStream if the response code is 2XX, if not,
             * will be the errorStream.
             */
            var responseBody = new BufferedReader(new InputStreamReader(
                    conn.getResponseCode() / 100 == 2 ? conn.getInputStream() : conn.getErrorStream()))
                    .lines().collect(toUnmodifiableList());

            var response = new HttpResponse(conn.getResponseCode(), responseBody);
            conn.disconnect();
            return response;
        } catch (IOException e) {
            log.error(String.format("There was a problem trying to connect to %s.",
                    url.toString()), e);
            return new HttpResponse(INTERNAL_SERVER_ERROR_500, emptyList());
        }
    }

    /**
     * Makes an HTTP request without a body to the specified URL using the given
     * HTTP method.
     * 
     * @param url    The URL to make the request to.
     * @param method The HTTP method to be used for the request.
     * @return An instance of HttpResponse containing the response code and body.
     */
    public HttpResponse makeHttpRequest(final URL url, final HttpMethod method) {
        return this.makeHttpRequest(url, method, null);
    }

    /**
     * Sets the body of the HTTP POST requests.
     * 
     * @param conn The HTTP connection.
     * @param body The body of the request.
     * @throws IOException If there is a problem setting the body.
     */
    private void setRequestBody(final HttpURLConnection conn, final String body) throws IOException {
        conn.setDoOutput(true);
        var os = conn.getOutputStream();
        var osw = new OutputStreamWriter(os, "UTF-8");
        osw.write(body);
        osw.flush();
        osw.close();
        os.close();
    }

}
