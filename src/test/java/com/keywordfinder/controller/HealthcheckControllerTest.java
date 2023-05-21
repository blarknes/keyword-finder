package com.keywordfinder.controller;

import static org.eclipse.jetty.http.HttpMethod.GET;
import static org.eclipse.jetty.http.HttpStatus.OK_200;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static spark.Spark.awaitInitialization;
import static spark.Spark.stop;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.keywordfinder.utilities.HttpClient;
import com.keywordfinder.utilities.Router;

@TestInstance(PER_CLASS)
class HealthcheckControllerTest {

    /**
     * Setting up the application to test the endpoints.
     */
    @BeforeEach
    private void setUp() throws Exception {
        final var router = new Router();
        router.establishRoutes();
        awaitInitialization();
    }

    /**
     * Shutting down application after tests have been made.
     */
    @AfterEach
    private void tearDown() throws Exception {
        stop();
    }

    @Test
    void testEndpointReturns200WhenApplicationIsAlive() throws MalformedURLException {
        // GIVEN
        final var url = new URL("http://localhost:4567/healthcheck");
        final var method = GET;
        final var client = new HttpClient();

        // WHEN
        final var response = client.makeHttpRequest(url, method);

        // THEN
        assertEquals(OK_200, response.getCode());
        assertEquals("OK", response.getBodyAsString());
    }

}
