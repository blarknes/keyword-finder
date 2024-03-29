package blarknes.keywordfinder.api.search.dto;

import blarknes.keywordfinder.api.search.model.SearchInformation;

/**
 * The dto used to display the information of a all searches.
 */
public record ListDisplayElement(
    String id,
    String keyword,
    String baseurl,
    String status,
    Integer looked,
    Integer found
) {

    /**
     * Constructor that builds a ListDisplayElement to return to the user in a
     * readable way.
     *
     * @param information the ListDisplayElement of the search
     * @return The ListDisplayElement based on the search information.
     */
    public static ListDisplayElement fromSearchInformation(final SearchInformation information) {
        return new ListDisplayElement(
            information.getId(),
            information.getKeyword(),
            information.getBaseurl(),
            information.getDone(),
            information.getUrls().size(),
            information.getUrlsKeywordFoundList().size()
        );
    }

}
