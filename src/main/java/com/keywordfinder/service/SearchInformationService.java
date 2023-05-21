package com.keywordfinder.service;

import static org.eclipse.jetty.http.HttpStatus.BAD_REQUEST_400;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.naming.directory.InvalidAttributeValueException;

import com.google.gson.Gson;
import com.keywordfinder.model.SearchInformation;
import com.keywordfinder.model.SearchRequestBody;
import com.keywordfinder.utilities.Utilities;

import spark.Request;
import spark.Response;

public class SearchInformationService {

    private final Map<String, SearchInformation> allSearches;
    private final RequestBodyValidationService requestBodyValidationService;

    private final Gson gson = new Gson();

    public SearchInformationService(Map<String, SearchInformation> allSearches) {
        this.allSearches = allSearches;
        this.requestBodyValidationService = new RequestBodyValidationService();
    }

    /**
     * Function to validate the request and start a new async search of the desired
     * keyword in the desired website.
     * 
     * @param req The Spark Request.
     * @param res The Spark Response.
     * @return The response message of the wanted search.
     * @throws MalformedURLException This exception will never happen, the URL is
     *                               being validated beforehand.
     */
    public String startNewSearch(Request req, Response res) throws MalformedURLException {
        try {
            this.requestBodyValidationService.validateNewSearchBodyRequest(req);
        } catch (InvalidAttributeValueException e) {
            res.status(BAD_REQUEST_400);
            return e.getMessage();
        }

        var information = instantiateSearchInformation(req);
        this.allSearches.put(information.getId(), information);
        runFirstSearchThread(information);

        return String.format("{ \"id\": \"%s\" }", information.getId());
    }

    /**
     * Instantiates a SearchInformation object based on the parameters passed on the
     * request, so that they can be passed through the crawling process.
     * 
     * @param req The Spark Request.
     * @return The instantiated SearchInformation object.
     * @throws MalformedURLException This exception will never happen, the URL is
     *                               being validated beforehand.
     */
    private SearchInformation instantiateSearchInformation(Request req) throws MalformedURLException {
        var body = gson.fromJson(req.body(), SearchRequestBody.class);
        var id = Utilities.generateRandomSearchId();
        var baseurl = new URL(body.getBaseurl());
        return new SearchInformation(id, baseurl, body.getKeyword());
    }

    /**
     * Runs the first thread of the crawling process based on the starting values
     * provided on the function itself and on the values inside the
     * SearchInformation object.
     * 
     * @param information The SearchInformation object containing user provided
     *                    data.
     */
    private void runFirstSearchThread(SearchInformation information) {
        var threads = new AtomicInteger(0);
        var urlsAccessed = new ConcurrentHashMap<String, Boolean>();
        var executor = Executors.newFixedThreadPool(256);

        var service = new ThreadService(executor, urlsAccessed, threads, information, information.getBaseurl());
        CompletableFuture.runAsync(service, executor);
    }

}
