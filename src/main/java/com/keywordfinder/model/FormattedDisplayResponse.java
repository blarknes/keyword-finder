package com.keywordfinder.model;

import static java.util.stream.Collectors.toList;

import java.util.List;

/**
 * This class is used to format the information inside SearchInformation class
 * to be a more concise and easily digestible JSON response to the end user.
 */
@SuppressWarnings("unused")
public class FormattedDisplayResponse {

    private String id;
    private String status;
    private List<String> urls;

    /**
     * Constructor that receives an SearchInformation object and converts the
     * information to match the desired output created by Gson.
     * 
     * @param information The SearchInformation object.
     */
    public FormattedDisplayResponse(SearchInformation information) {
        this.id = information.getId();
        this.status = information.isDone() ? "done" : "active";
        this.urls = information.getUrls().keySet().stream().sorted().collect(toList());
    }

}
