package blarknes.keywordfinder.api.exception;

import java.time.Instant;
import java.util.List;

import lombok.Builder;

/**
 * Dto that formats the information of an exception in any request and format it
 * in a pattern that matches the one used by spring.
 */
@Builder
public record ErrorRequestResponse(
    Instant timestamp,
    Integer status,
    String error,
    List<String> message,
    String path
) {

}
