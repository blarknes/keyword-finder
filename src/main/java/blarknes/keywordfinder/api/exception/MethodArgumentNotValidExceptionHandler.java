package blarknes.keywordfinder.api.exception;

import static java.time.Instant.now;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.http.HttpServletRequest;
import lombok.val;

/**
 * Handler for MethodArgumentNotValidException that formats the information in a
 * user readable way that matches the spring default to return it to the
 * request.
 */
@ControllerAdvice
public class MethodArgumentNotValidExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    private ErrorRequestResponse handler(MethodArgumentNotValidException exception, HttpServletRequest request) {
        val timestamp = now();
        val status = BAD_REQUEST.value();
        val error = BAD_REQUEST.getReasonPhrase();
        val message = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(toList());
        val path = request.getRequestURI();

        return ErrorRequestResponse.builder()
            .timestamp(timestamp)
            .status(status)
            .error(error)
            .message(message)
            .path(path)
            .build();
    }

}
