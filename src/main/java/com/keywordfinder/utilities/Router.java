package com.keywordfinder.utilities;

import static spark.Spark.port;

import com.keywordfinder.controller.HealthcheckController;

public class Router {

    private final HealthcheckController healthcheckController;

    private final int PORT = 4567;

    /**
     * Constructs a new instance of the Router class.
     * <p>
     * Every Controller added to the application must be added here so that the
     * routes can be available to the user.
     */
    public Router() {
        this.healthcheckController = new HealthcheckController();
    }

    /**
     * Umbrella function to setup all possible routes of the API.
     */
    public void establishRoutes() {
        port(PORT);

        healthcheckController.establishRoutes();
    }

}
