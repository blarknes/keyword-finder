package blarknes.keywordfinder.api.search.dto;

import java.util.List;

/**
 * The response of the listing of searches request.
 */
public record ListDisplayResponse(
    List<ListDisplayElement> searches
) {

}
