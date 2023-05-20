package com.keywordfinder.controller;

import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.post;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.keywordfinder.model.SearchInformation;
import com.keywordfinder.service.DisplaySearchResponseService;
import com.keywordfinder.service.ListSearchesService;
import com.keywordfinder.service.SearchInformationService;

public class CrawlController {

    private final DisplaySearchResponseService displaySearchResponseService;
    private final ListSearchesService listSearchesService;
    private final SearchInformationService searchInformationServices;

    private final Map<String, SearchInformation> allSearches;

    private final String REQ_TYPE = "application/json";
    private final String RES_TYPE = "application/json";

    /**
     * Constructor to initialize all the const classes present in this file, so that
     * all the requests will be handled by the same instances of the classes.
     */
    public CrawlController() {
        this.allSearches = new ConcurrentHashMap<>();
        this.displaySearchResponseService = new DisplaySearchResponseService(this.allSearches);
        this.listSearchesService = new ListSearchesService(this.allSearches);
        this.searchInformationServices = new SearchInformationService(this.allSearches);
    }

    /**
     * Establishes routes for handling HTTP requests related to crawling.
     * <p>
     * The method sets up routes for creating a new search, listing all searches,
     * and retrieving information about a specific search.
     */
    public void establishRoutes() {
        path("/crawl", () -> {
            post("", REQ_TYPE, (req, res) -> {
                res.type(RES_TYPE);
                return this.searchInformationServices.startNewSearch(req, res);
            });
            get("/list", REQ_TYPE, (req, res) -> {
                res.type(RES_TYPE);
                return this.listSearchesService.listAllSearches(req, res);
            });
            get("/:id", REQ_TYPE, (req, res) -> {
                res.type(RES_TYPE);
                return this.displaySearchResponseService.displaySearchInformation(req, res);
            });
        });
    }

}
