package com.keywordfinder.service;

import static org.eclipse.jetty.http.HttpStatus.BAD_REQUEST_400;

import java.util.Map;

import javax.naming.directory.InvalidAttributeValueException;

import com.google.gson.Gson;
import com.keywordfinder.model.FormattedDisplayResponse;
import com.keywordfinder.model.SearchInformation;

import spark.Request;
import spark.Response;

public class DisplaySearchResponseService {

    private final Map<String, SearchInformation> allSearches;
    private final RequestBodyValidationService requestBodyValidationService;

    private final Gson gson = new Gson();

    private final static String PARAM_ID = ":id";

    public DisplaySearchResponseService(final Map<String, SearchInformation> allSearches) {
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
    public String displaySearchInformation(final Request req, final Response res) {
        var id = req.params().get(PARAM_ID);

        try {
            this.requestBodyValidationService.validateShowMadeSearch(req, this.allSearches, id);
        } catch (InvalidAttributeValueException e) {
            res.status(BAD_REQUEST_400);
            return e.getMessage();
        }

        var response = new FormattedDisplayResponse(allSearches.get(id));

        return gson.toJson(response);
    }

}