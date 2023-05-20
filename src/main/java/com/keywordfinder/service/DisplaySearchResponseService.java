package com.keywordfinder.service;

import java.util.Map;

import javax.naming.directory.InvalidAttributeValueException;

import org.eclipse.jetty.http.HttpStatus;

import com.google.gson.Gson;
import com.keywordfinder.model.FormattedDisplayResponse;
import com.keywordfinder.model.SearchInformation;

import spark.Request;
import spark.Response;

public class DisplaySearchResponseService {

    private final Map<String, SearchInformation> allSearches;
    private final RequestBodyValidationService requestBodyValidationService;

    private final Gson gson = new Gson();

    private final String PARAM_ID = ":id";

    public DisplaySearchResponseService(Map<String, SearchInformation> allSearches) {
        this.allSearches = allSearches;
        this.requestBodyValidationService = new RequestBodyValidationService();
    }

    /**
     * Function to check if the user request is valid, then format and display to
     * the end user the information of the desired search in a highly digestible
     * way.
     * 
     * @param req The Spark Request.
     * @param res The Spark Response.
     * @return The display information of the desired search.
     */
    public String displaySearchInformation(Request req, Response res) {
        try {
            this.requestBodyValidationService.validateShowMadeSearch(req, this.allSearches);
        } catch (InvalidAttributeValueException e) {
            res.status(HttpStatus.BAD_REQUEST_400);
            return e.getMessage();
        }

        var id = req.params().get(PARAM_ID);
        var response = new FormattedDisplayResponse(allSearches.get(id));

        return gson.toJson(response);
    }

}