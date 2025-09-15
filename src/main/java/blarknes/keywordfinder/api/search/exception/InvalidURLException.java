package blarknes.keywordfinder.api.search.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * {@link InvalidURLException} is thrown when the url provided by the user
 * passed the regex test but was not able to be converted to an URL object.
 */
@ResponseStatus(BAD_REQUEST)
public class InvalidURLException extends RuntimeException {

    public InvalidURLException(final String url) {
        super("The URL " + url + " is invalid");
    }

}
