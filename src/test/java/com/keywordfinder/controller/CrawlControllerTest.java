package com.keywordfinder.controller;

import static org.eclipse.jetty.http.HttpMethod.GET;
import static org.eclipse.jetty.http.HttpMethod.POST;
import static org.eclipse.jetty.http.HttpStatus.BAD_REQUEST_400;
import static org.eclipse.jetty.http.HttpStatus.OK_200;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static spark.Spark.awaitInitialization;
import static spark.Spark.awaitStop;
import static spark.Spark.stop;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.keywordfinder.utilities.HttpClient;
import com.keywordfinder.utilities.Router;

@TestInstance(PER_CLASS)
class CrawlControllerTest {

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
        awaitStop();
    }

    @Test
    void testNewSearchWithInvalidBody() throws MalformedURLException {
        // GIVEN
        final var url = new URL("http://localhost:4567/crawl");
        final var method = POST;
        final var client = new HttpClient();

        // WHEN
        final var body = "{ \"invalid_property\": \"value for invalid_property\" }";
        final var response = client.makeHttpRequest(url, method, body);

        // THEN
        assertEquals(BAD_REQUEST_400, response.getCode().intValue());
        assertEquals("{ \"reason\": \"Invalid value for field `baseurl`. Not a valid URL.\" }",
                response.getBodyAsString());
    }

    @Test
    void testNewSearchWithInvalidBaseurl() throws MalformedURLException {
        // GIVEN
        final var url = new URL("http://localhost:4567/crawl");
        final var method = POST;
        final var client = new HttpClient();

        // WHEN
        final var body = "{ \"baseurl\": \"this is not a valid url\" }";
        final var response = client.makeHttpRequest(url, method, body);

        // THEN
        assertEquals(BAD_REQUEST_400, response.getCode().intValue());
        assertEquals("{ \"reason\": \"Invalid value for field `baseurl`. Not a valid URL.\" }",
                response.getBodyAsString());
    }

    @ParameterizedTest
    @ValueSource(strings = { "123", "this is a invalid value for field keyword" })
    void testNewSearchWithInvalidKeyword(final String keyword) throws MalformedURLException {
        // GIVEN
        final var url = new URL("http://localhost:4567/crawl");
        final var method = POST;
        final var client = new HttpClient();

        // WHEN
        final var body = "{ \"baseurl\": \"http://localhost:4567\", \"keyword\": \"" + keyword + "\" }";
        final var response = client.makeHttpRequest(url, method, body);

        // THEN
        assertEquals(BAD_REQUEST_400, response.getCode().intValue());
        assertEquals("{ \"reason\": \"Invalid size for field `keyword`. Must be a anywhere between 4 through 32.\" }",
                response.getBodyAsString());
    }

    @Test
    void testNewSearchWithAValidBody() throws MalformedURLException {
        // GIVEN
        final var url = new URL("http://localhost:4567/crawl");
        final var method = POST;
        final var client = new HttpClient();

        // WHEN
        final var body = "{ \"baseurl\": \"http://localhost:4567\", \"keyword\": \"arena\" }";
        final var response = client.makeHttpRequest(url, method, body);

        // THEN
        assertEquals(OK_200, response.getCode().intValue());
        assertTrue(response.getBodyAsString().matches("\\{ \"id\": \"[a-zA-Z0-9]{8}\" }"));
    }

    @Test
    void testListReturnsEmptyWhenNoSearchesWereMade() throws MalformedURLException {
        // GIVEN
        final var url = new URL("http://localhost:4567/crawl/list");
        final var method = GET;
        final var client = new HttpClient();

        // WHEN
        final var response = client.makeHttpRequest(url, method);

        // THEN
        assertEquals(OK_200, response.getCode());
        assertEquals("{\"active\":[],\"done\":[]}", response.getBodyAsString());
    }

    @Test
    void testDisplaySearchInformationWhenInvalidId() throws MalformedURLException {
        // GIVEN
        final var url = new URL("http://localhost:4567/crawl/this_is_an_invalid_id");
        final var method = GET;
        final var client = new HttpClient();

        // WHEN
        final var response = client.makeHttpRequest(url, method);

        // THEN
        assertEquals(BAD_REQUEST_400, response.getCode());
        assertEquals("{ \"reason\": \"Invalid value for field `id`. This id does not exist.\" }",
                response.getBodyAsString());
    }

}
