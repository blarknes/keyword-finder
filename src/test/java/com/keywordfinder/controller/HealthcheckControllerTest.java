package com.keywordfinder.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static spark.Spark.awaitInitialization;
import static spark.Spark.stop;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jetty.http.HttpMethod;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.keywordfinder.model.HttpResponse;
import com.keywordfinder.utilities.HttpClient;
import com.keywordfinder.utilities.Router;

public class HealthcheckControllerTest {

    @BeforeEach
    public void setUp() throws Exception {
        Router router = new Router();
        router.establishRoutes();
        awaitInitialization();
    }

    @AfterEach
    public void tearDown() throws Exception {
        stop();
    }

    @Test
    public void testModelObjectsPOST() throws MalformedURLException {
        // GIVEN
        URL url = new URL("http://localhost:4567/healthcheck");
        HttpMethod method = HttpMethod.GET;
        HttpClient client = new HttpClient();

        // WHEN
        HttpResponse response = client.makeHttpRequest(url, method);

        // THEN
        assertEquals(200, response.getCode());
        assertEquals("OK", response.getBodyAsString());
    }

}