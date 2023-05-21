package com.keywordfinder.service;

import static org.eclipse.jetty.http.HttpMethod.GET;
import static org.eclipse.jetty.http.HttpStatus.OK_200;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.keywordfinder.model.HttpResponse;
import com.keywordfinder.model.SearchInformation;
import com.keywordfinder.utilities.HttpClient;

public class ThreadServiceTest {

    private final SearchInformation searchInformation = mock(SearchInformation.class);
    private final ThreadService threadService = mock(ThreadService.class);
    private final HttpClient httpClient = mock(HttpClient.class);
    private final ExecutorService executorService = mock(ExecutorService.class);
    private final AtomicInteger threadCounter = mock(AtomicInteger.class);
    private final Map<String, Boolean> urlsAccessed = new HashMap<>();
    private final ThreadService threadServiceWithParams = new ThreadService(this.searchInformation, this.urlsAccessed,
            this.threadCounter, this.executorService);

    @Test
    void testIfNewThreadWasCreatedForNewURL() throws MalformedURLException {
        // GIVEN
        final var url = new URL("https://example.com/first-path/");
        final var line = "<a href=\"foo.html\">one valid url</a>";

        when(this.searchInformation.getBaseurl()).thenReturn(url);
        when(this.searchInformation.getKeyword()).thenReturn("magic");
        when(this.httpClient.makeHttpRequest(url, GET)).thenReturn(new HttpResponse(OK_200, List.of(line)));

        final var crawlService = new CrawlService(this.threadService, this.searchInformation, url, this.httpClient);

        // WHEN
        crawlService.run();

        // THEN
        verify(this.threadService).run(any());
    }

    @Test
    void testAddOnlyOneWebsiteToFoundUrls() throws MalformedURLException {
        // GIVEN
        final var searchInformation = new SearchInformation("MagicId4", new URL("https://foo.com"), "magic");
        final var threadService = new ThreadService(searchInformation, this.urlsAccessed, this.threadCounter,
                this.executorService);

        // WHEN
        threadService.addURLFoundKeyword("https://foo.com");
        threadService.addURLFoundKeyword("https://foo.com");

        // THEN
        assertThat(searchInformation.getUrls().keySet(), containsInAnyOrder("https://foo.com"));
    }

    @Test
    void testThreadCounterIncremetingAndDecrementing() throws InterruptedException, MalformedURLException {
        // GIVEN
        final var threadService = new ThreadService(this.searchInformation, this.urlsAccessed, this.threadCounter,
                Executors.newFixedThreadPool(1));
        final var url = new URL("https://example.com/first-path/");

        // WHEN
        threadService.runNewThread(url);

        // THEN
        assertEquals(0, this.threadCounter.get());
    }

    @Test
    void testProperShutdown() {
        // WHEN
        threadServiceWithParams.shutdown();

        // THEN
        verify(executorService).shutdown();
    }

    @Test
    void testNotShutdownIfAlreadyShutdown() {
        // GIVEN
        when(this.executorService.isShutdown()).thenReturn(true);

        // WHEN
        threadServiceWithParams.shutdown();

        // THEN
        verify(this.executorService, never()).shutdown();
    }

}
