package com.keywordfinder.model;

import static java.util.stream.Collectors.joining;

import java.util.List;

public class HttpResponse {

    private Integer code;
    private List<String> body;

    /**
     * Constructs a new instance of the HttpResponse class with the given response
     * code and body.
     *
     * @param code The HTTP response code.
     * @param body The response body.
     */
    public HttpResponse(final Integer code, final List<String> body) {
        this.code = code;
        this.body = body;
    }

    /**
     * Retrieves the HTTP response code.
     *
     * @return The HTTP response code.
     */
    public Integer getCode() {
        return this.code;
    }

    /**
     * Retrieves the HTTP response body as List<String>.
     *
     * @return The HTTP response body.
     */
    public List<String> getBodyAsList() {
        return this.body;
    }

    /**
     * Retrieves the HTTP response body as String.
     *
     * @return The HTTP response body.
     */
    public String getBodyAsString() {
        return this.body.stream().map(n -> String.valueOf(n)).collect(joining());
    }

}
