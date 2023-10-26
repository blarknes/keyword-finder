package blarknes.keywordfinder.api.search.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * The request body provided by the user to kickstart the search process.
 */
public record SearchRequestBody(
    @Size(
        min = 4,
        max = 32,
        message = "The keyword size must be between 4 and 32"
    )
    String keyword,

    @Pattern(
        regexp = "^[(http(s)?):\\/\\/(www\\.)?a-zA-Z0-9:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)$",
        message = "The baseurl must be a valid url"
    )
    String baseurl
) {

}
