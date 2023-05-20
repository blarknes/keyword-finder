package com.keywordfinder.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.keywordfinder.model.SearchInformation;

import spark.Request;
import spark.Response;

public class ListSearchesService {

    private final Map<String, SearchInformation> allSearches;

    private final Gson gson = new Gson();

    private final String DONE = "done";
    private final String ACTIVE = "active";

    public ListSearchesService(Map<String, SearchInformation> allSearches) {
        this.allSearches = allSearches;
    }

    /**
     * Takes the information of every search digested into formatted strings and put
     * them into a map for Gson formatting purposes. So that is goes to the end user
     * in a highly readable way.
     * 
     * @param req The Spark Request.
     * @param res The Spark Response.
     * @return The formatted information of all searches.
     */
    public String listAllSearches(Request req, Response res) {
        var searchesDone = new ArrayList<String>();
        var searchesActive = new ArrayList<String>();

        filterSearchesIntoLists(searchesDone, searchesActive);

        var response = new HashMap<String, List<String>>();
        response.put(DONE, searchesDone);
        response.put(ACTIVE, searchesActive);

        return gson.toJson(response);
    }

    /**
     * Gets the information of every search made and filters it into two arrays of
     * formatted strings.
     * 
     * @param searchesDone   The List of done searches.
     * @param searchesActive The List of active searches.
     */
    private void filterSearchesIntoLists(List<String> searchesDone, List<String> searchesActive) {
        for (var key : this.allSearches.keySet()) {
            var entry = this.allSearches.get(key);

            var message = String.format("%s: %s keyword found in %d urls", entry.getId(), entry.getKeyword(),
                    entry.getUrls().size());

            if (entry.isDone()) {
                searchesDone.add(message);
            } else {
                searchesActive.add(message);
            }
        }
    }

}
