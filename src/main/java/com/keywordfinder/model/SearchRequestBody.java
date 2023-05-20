package com.keywordfinder.model;

public class SearchRequestBody {

    private String baseurl;
    private String keyword;

    /**
     * Constructs a new empty instance of the SearchRequestBody class to be used
     * mainly by GSON.
     */
    public SearchRequestBody() {
    }

    /**
     * Retrieves the base url provided in the user request.
     *
     * @return The base url for the search.
     */
    public String getBaseurl() {
        return this.baseurl;
    }

    /**
     * Retrieves the keyword provided in the user request.
     *
     * @return The keyword to be searched.
     */
    public String getKeyword() {
        return this.keyword;
    }

}
