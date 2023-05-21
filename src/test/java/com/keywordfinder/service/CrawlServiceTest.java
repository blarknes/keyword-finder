package com.keywordfinder.service;

import static java.util.Collections.emptyList;
import static org.eclipse.jetty.http.HttpMethod.GET;
import static org.eclipse.jetty.http.HttpStatus.OK_200;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.keywordfinder.model.HttpResponse;
import com.keywordfinder.model.SearchInformation;
import com.keywordfinder.utilities.HttpClient;

class CrawlServiceTest {

    private final ThreadService threadService = mock(ThreadService.class);
    private final SearchInformation searchInformation = mock(SearchInformation.class);
    private final HttpClient httpClient = mock(HttpClient.class);

    private static List<Arguments> searchingForKeywordTestData() {
        return List.of(
                Arguments.of(List.of("this line is magic"), 1),
                Arguments.of(List.of("this is not"), 0),
                Arguments.of(List.of(" "), 0),
                Arguments.of(List.of("this is a magical line with magic"), 1),
                Arguments.of(List.of("THIS IS VERY MAGIC"), 1),
                Arguments.of(List.of("this m a g i c was broken, doesn't work anymore"), 0),
                Arguments.of(List.of("this line has the keyword magic", "this line does not"), 1),
                Arguments.of(List.of("the next line has the word you want", "and it works like magic"), 1));
    }

    @ParameterizedTest
    @MethodSource("searchingForKeywordTestData")
    void testKeywordFoundOrNotInPage(final List<String> lines, final int expected) throws MalformedURLException {
        // GIVEN
        final var url = new URL("https://example.com");
        when(this.searchInformation.getKeyword()).thenReturn("magic");
        when(this.httpClient.makeHttpRequest(url, GET)).thenReturn(new HttpResponse(OK_200, lines));

        final var crawlerService = new CrawlService(this.threadService, this.searchInformation, url, this.httpClient);

        // WHEN
        crawlerService.run();

        // THEN
        verify(this.threadService, times(expected)).addURLFoundKeyword(url.toString());
    }

    private static List<Arguments> searchingForNewUrlTestData() throws MalformedURLException {
        return List.of(
                Arguments.of("<a href=\"foo.html\">one valid url</a>",
                        List.of(new URL("https://example.com/first-path/foo.html"))),
                Arguments.of("<a href=\"FOO.HTML\">one valid url</a>",
                        List.of(new URL("https://example.com/first-path/FOO.HTML"))),
                Arguments.of("this is not a url",
                        emptyList()),
                Arguments.of(" ",
                        emptyList()),
                Arguments.of("<a href=\"bar.html\">one valid url</a> <a href=\"baz.html\">two valid urls</a>",
                        List.of(new URL("https://example.com/first-path/bar.html"),
                                new URL("https://example.com/first-path/bar.html"))),
                Arguments.of("<a href=\"bar.html\">one valid url</a> <a invalid>one invalid url</a>",
                        List.of(new URL("https://example.com/first-path/bar.html"))),
                Arguments.of("<a invalid>one invalid url</a> <a href=\"baz.html\">one valid url</a>",
                        List.of(new URL("https://example.com/first-path/baz.html"))),
                Arguments.of("<a href=\"https://foo.com\">one external https url</a>",
                        emptyList()),
                Arguments.of("<a href=\"http://foo.com\">one external http url</a>",
                        emptyList()),
                Arguments.of("<a href=\"../bar.html\"> relative url outside of scope</a>",
                        emptyList()));
    }

    @ParameterizedTest
    @MethodSource("searchingForNewUrlTestData")
    void testFoundOrNotNewUrlsToVisit(final String line, final List<URL> expected) throws MalformedURLException {
        // GIVEN
        final var url = new URL("https://example.com/first-path/");
        when(this.searchInformation.getBaseurl()).thenReturn(url);
        when(this.searchInformation.getKeyword()).thenReturn("magic");
        when(this.httpClient.makeHttpRequest(url, GET)).thenReturn(new HttpResponse(OK_200, List.of(line)));

        final var crawlerService = new CrawlService(this.threadService, this.searchInformation, url, this.httpClient);

        // WHEN
        crawlerService.run();

        // THEN
        expected.forEach(expectedUrl -> verify(this.threadService).run(expectedUrl));
    }

}
