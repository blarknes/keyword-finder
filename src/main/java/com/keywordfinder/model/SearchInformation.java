package com.keywordfinder.model;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SearchInformation {

    private String id;
    private URL baseurl;
    private String keyword;
    private boolean done;
    private Map<String, Boolean> urls;

    /**
     * Constructs a new instance of the SearchInformation class with the given
     * search id, base url and keyword.
     *
     * @param id      The search id.
     * @param baseurl The base url for the search.
     * @param keyword The keyword to be searched.
     */
    public SearchInformation(final String id, final URL baseurl, final String keyword) {
        this.id = id;
        this.baseurl = baseurl;
        this.keyword = keyword;
        this.done = false;
        this.urls = new ConcurrentHashMap<>();
    }

    /**
     * Retrieves the search Id.
     *
     * @return The search Id.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Retrieves the base url provided in user request.
     *
     * @return The request base url.
     */
    public URL getBaseurl() {
        return this.baseurl;
    }

    /**
     * Retrieves the keyword provided in user request.
     *
     * @return The request keyword.
     */
    public String getKeyword() {
        return this.keyword;
    }

    /**
     * Retrieves if the search is done or not.
     *
     * @return The search status.
     */
    public boolean isDone() {
        return this.done;
    }

    /**
     * Updates the search status from `active` to `done`.
     */
    public void updateDone() {
        this.done = true;
    }

    /**
     * Retrieves the map of urls where the keyword was found.
     *
     * @return The map of found keyword urls.
     */
    public Map<String, Boolean> getUrls() {
        return this.urls;
    }

}
