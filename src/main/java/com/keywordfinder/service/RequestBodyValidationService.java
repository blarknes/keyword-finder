package com.keywordfinder.service;

import java.util.Map;

import javax.naming.directory.InvalidAttributeValueException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.keywordfinder.model.SearchInformation;
import com.keywordfinder.model.SearchRequestBody;

import spark.Request;

public class RequestBodyValidationService {

    Gson gson = new Gson();

    private final static String REGEX_PATTERN_URL = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    /**
     * Validate if the body of the new keyword search is valid or not.
     * 
     * @param req The Spark Request.
     * @param res The Spark Response.
     * @throws InvalidAttributeValueException
     */
    public void validateNewSearchBodyRequest(final Request req) throws InvalidAttributeValueException {
        SearchRequestBody searchBody;

        try {
            searchBody = gson.fromJson(req.body(), SearchRequestBody.class);
        } catch (JsonSyntaxException e) {
            throw new JsonSyntaxException(
                    "{ \"reason\": \"Invalid body provided. Must have only `baseurl` and `keyword`.\" }");
        }

        var baseurl = searchBody.getBaseurl() == null ? null : searchBody.getBaseurl().trim();
        if (baseurl == null || baseurl.isEmpty() || !baseurl.matches(REGEX_PATTERN_URL)) {
            throw new InvalidAttributeValueException(
                    "{ \"reason\": \"Invalid value for field `baseurl`. Not a valid URL.\" }");
        }

        var keyword = searchBody.getKeyword() == null ? null : searchBody.getKeyword().trim();
        if (keyword == null || keyword.length() < 4 || keyword.length() > 32) {
            throw new InvalidAttributeValueException(
                    "{ \"reason\": \"Invalid size for field `keyword`. Must be a anywhere between 4 through 32.\" }");
        }
    }

    /**
     * Validate if the request to see the current status of the search has a valid
     * id or not.
     * 
     * @param req         The Spark Request.
     * @param res         The Spark Response.
     * @param allSearches The information of every search.
     * @throws InvalidAttributeValueException
     */
    public void validateShowMadeSearch(final Request req, final Map<String, SearchInformation> allSearches)
            throws InvalidAttributeValueException {
        var id = req.params().get(":id");

        if (!allSearches.containsKey(id)) {
            throw new InvalidAttributeValueException(
                    "{ \"reason\": \"Invalid value for field `id`. This id does not exist.\" }");
        }
    }

}
