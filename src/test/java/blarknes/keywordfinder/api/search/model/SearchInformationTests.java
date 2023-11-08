package blarknes.keywordfinder.api.search.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import lombok.val;

public class SearchInformationTests {

    private static final String ID = "05GNJVHm";
    private static final String KEYWORD = "magic";
    private static final String BASEURL = "https://example.com/";

    @Test
    void getDone_shouldReturnDoneStatusWhenDoneIsTrue() {
        // given
        val searchInformation = new SearchInformation(ID, KEYWORD, BASEURL);

        // when
        searchInformation.updateDone();

        // then
        assertEquals("done", searchInformation.getDone());
    }

    @Test
    void getDone_shouldReturnRunningStatusWhenDoneIsFalse() {
        // given
        val searchInformation = new SearchInformation(ID, KEYWORD, BASEURL);

        // when

        // then
        assertEquals("running", searchInformation.getDone());
    }

    @Test
    void updateDone_shouldSetDoneStatusToTrue() {
        // given
        val searchInformation = new SearchInformation(ID, KEYWORD, BASEURL);

        // when
        searchInformation.updateDone();

        // then
        assertEquals("done", searchInformation.getDone());
    }

    @Test
    void getUrlsKeywordFoundList_shouldReturnSortedListWithValuesWhenFoundUrls() {
        // given
        val searchInformation = new SearchInformation(ID, KEYWORD, BASEURL);

        val url1 = BASEURL + "/page1";
        val url2 = BASEURL + "/page2";
        val url3 = BASEURL + "/page3";

        searchInformation.getUrls().put(url3, true);
        searchInformation.getUrls().put(url2, false);
        searchInformation.getUrls().put(url1, true);

        val expectedUrls = List.of(url1, url3);

        // when
        val urls = searchInformation.getUrlsKeywordFoundList();

        // then
        assertEquals(expectedUrls, urls);
    }

    @Test
    void getUrlsKeywordFoundList_shouldReturnEmptyListWhenThereIsNoFoundUrls() {
        // given
        val searchInformation = new SearchInformation(ID, KEYWORD, BASEURL);

        val url1 = BASEURL + "/page1";
        val url2 = BASEURL + "/page2";
        val url3 = BASEURL + "/page3";

        searchInformation.getUrls().put(url3, false);
        searchInformation.getUrls().put(url2, false);
        searchInformation.getUrls().put(url1, false);

        val expectedUrls = List.of();

        // when
        val urls = searchInformation.getUrlsKeywordFoundList();

        // then
        assertEquals(expectedUrls, urls);
    }

    @Test
    void getUrlsKeywordFoundList_shouldReturnEmptyListWhenUrlsMapIsEmpty() {
        // given
        val searchInformation = new SearchInformation(ID, KEYWORD, BASEURL);

        val expectedUrls = List.of();

        // when
        val urls = searchInformation.getUrlsKeywordFoundList();

        // then
        assertEquals(expectedUrls, urls);
    }

}
