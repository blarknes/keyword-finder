package blarknes.keywordfinder.api.search.dto;

import java.util.List;

/**
 * The information of all search requests to be returned to the user.
 */
public record ListDisplayResponse(
    List<ListDisplayElement> searches
) {

}
