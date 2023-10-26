package blarknes.keywordfinder.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import blarknes.keywordfinder.api.search.exception.HttpErrorResponseException;
import blarknes.keywordfinder.api.search.exception.InvalidURLException;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * Utility class for HTTP actions.
 */
@Component
@RequiredArgsConstructor
public class HttpRequestSender {

    private final HttpClient httpClient;

    /**
     * Sends an HTTP GET Request to an specified url and returns the response body
     * as an list of strings.
     *
     * @param url the desired url to make the request
     * @return A list of strings.
     */
    public List<String> doGetRequest(final String url) {
        val uri = buildUri(url);

        val request = HttpRequest.newBuilder()
            .uri(uri)
            .GET()
            .build();
        val response = getResponse(httpClient, request);

        if (!HttpStatus.valueOf(response.statusCode()).is2xxSuccessful()) {
            throw new HttpErrorResponseException(response.statusCode());
        }

        return response.body().toList();
    }

    private URI buildUri(final String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            throw new InvalidURLException(url);
        }
    }

    private HttpResponse<Stream<String>> getResponse(final HttpClient httpClient, final HttpRequest request) {
        try {
            return httpClient.send(request, BodyHandlers.ofLines());
        } catch (IOException | InterruptedException e) {
            throw new HttpErrorResponseException(500);
        }
    }

}
