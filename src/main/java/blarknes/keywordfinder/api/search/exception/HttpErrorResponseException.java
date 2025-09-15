package blarknes.keywordfinder.api.search.exception;

/**
 * {@code HttpErrorResponseException} is thrown when an HTTP Request returned
 * with a code that was not 2xx.
 */
public class HttpErrorResponseException extends RuntimeException {

    public HttpErrorResponseException(final Integer code) {
        super("There was an error " + code + " with the HTTP Request");
    }

}
