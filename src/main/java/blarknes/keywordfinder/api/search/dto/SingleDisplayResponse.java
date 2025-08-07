package blarknes.keywordfinder.api.search.dto;

import java.util.List;

import blarknes.keywordfinder.api.search.model.SearchInformation;

/**
 * The information of a search request to be returned to the user.
 */
public record SingleDisplayResponse(
    String id,
    String keyword,
    String baseurl,
    String status,
    Integer looked,
    Integer found,
    List<String> urls
) {

    /**
     * Constructor that builds a SingleDisplayResponse to return to the user in a
     * readable way.
     *
     * @param information the information of the search
     * @return The SingleDisplayResponse based on the search information.
     */
    public static SingleDisplayResponse fromSearchInformation(final SearchInformation information) {
        return new SingleDisplayResponse(
            information.getId(),
            information.getKeyword(),
            information.getBaseurl(),
            information.getDone(),
            information.getUrls().size(),
            information.getUrlsKeywordFoundList().size(),
            information.getUrlsKeywordFoundList()
        );
    }

}
