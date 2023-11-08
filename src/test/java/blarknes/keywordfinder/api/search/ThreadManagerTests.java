package blarknes.keywordfinder.api.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import blarknes.keywordfinder.api.search.model.SearchInformation;
import blarknes.keywordfinder.http.HttpRequestSender;
import lombok.val;

public class ThreadManagerTests {

    private final SearchInformation mockInformation = mock(SearchInformation.class);
    private final ExecutorService mockExecutor = mock(ExecutorService.class);
    private final HttpRequestSender mockHttpRequestSender = mock(HttpRequestSender.class);
    private final AtomicInteger mockRunningThreadsCount = mock(AtomicInteger.class);

    private static final String ID = "05GNJVHm";
    private static final String KEYWORD = "magic";
    private static final String BASEURL = "https://example.com/";

    @Test
    void run_threadCounterIncrementedWhenNewThreadStarted() {
        // given
        val runningThreadsCount = new AtomicInteger(0);
        val threadManager = new ThreadManager(this.mockInformation, this.mockExecutor, this.mockHttpRequestSender, runningThreadsCount);

        // when
        threadManager.run(BASEURL);

        // then
        assertEquals(1, runningThreadsCount.get());
    }

    @Test
    void run_shouldAddUniqueUrlToInformationUrlsMap() {
        // given
        val information = new SearchInformation(ID, KEYWORD, BASEURL);
        val threadManager = new ThreadManager(information, this.mockExecutor, this.mockHttpRequestSender, this.mockRunningThreadsCount);
        val expectedMap = Map.of(
            BASEURL, false
        );

        // when
        threadManager.run(BASEURL);

        // then
        assertEquals(information.getUrls(), expectedMap);
    }

    @Test
    void updateUrlKeywordWasFound_shouldUpdateUrlsMapValueToTrueWhenKeywordWasFound() {
        // given
        val information = new SearchInformation(ID, KEYWORD, BASEURL);
        val threadManager = new ThreadManager(information, this.mockExecutor, this.mockHttpRequestSender, this.mockRunningThreadsCount);
        val expectedMap = Map.of(
            BASEURL, true
        );

        // when
        threadManager.run(BASEURL);
        threadManager.updateUrlKeywordWasFound(BASEURL);

        // then
        assertEquals(information.getUrls(), expectedMap);
    }

    @Test
    void integration_searchInformationUrlsShouldReturnUniqueAndValidUrls() {
        // given
        val information = new SearchInformation(ID, KEYWORD, BASEURL);
        val threadManager = new ThreadManager(information, this.mockExecutor, this.mockHttpRequestSender, this.mockRunningThreadsCount);

        val url1 = BASEURL;
        val url2 = BASEURL + "/bar.html";
        val url3 = BASEURL + "/foo.html";

        val expectedUrlsMap = Map.of(
            url1, false,
            url2, true,
            url3, true
        );
        val expectedUrlsKeywordFoundList = List.of(url2, url3);

        // when
        threadManager.run(url1);
        threadManager.run(url2);
        threadManager.run(url2); // repeating this value to test uniquity
        threadManager.run(url3);

        threadManager.updateUrlKeywordWasFound(url2);
        threadManager.updateUrlKeywordWasFound(url3);

        // then
        assertEquals(information.getUrls(), expectedUrlsMap);
        assertEquals(information.getUrlsKeywordFoundList(), expectedUrlsKeywordFoundList);
    }

}
