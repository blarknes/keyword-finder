package com.keywordfinder.controller;

import static spark.Spark.get;

public class HealthcheckController {

    private final String RES_TYPE = "application/json";
    private final String RESPONSE = "OK";

    /**
     * GET /healthcheck route handler.
     * <p>
     * This route is used to perform a quick health check on the application.
     * 
     * @return A predefined "200 OK" response to the user.
     */
    public void establishRoutes() {
        get("/healthcheck", (req, res) -> {
            res.type(RES_TYPE);
            return RESPONSE;
        });
    }

}
