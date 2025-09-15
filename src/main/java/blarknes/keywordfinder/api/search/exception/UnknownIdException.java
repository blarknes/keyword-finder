package blarknes.keywordfinder.api.search.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * {@link UnknownIdException} is thrown when the user tries to retrieve
 * information from an ID that does not exist.
 */
@ResponseStatus(BAD_REQUEST)
public class UnknownIdException extends RuntimeException {

    public UnknownIdException(final String id) {
        super("The ID " + id + " does not exist");
    }

}
