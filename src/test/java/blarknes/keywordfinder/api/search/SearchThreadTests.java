package blarknes.keywordfinder.api.search;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import blarknes.keywordfinder.api.search.model.SearchInformation;
import blarknes.keywordfinder.http.HttpRequestSender;
import lombok.val;

public class SearchThreadTests {

    private final ThreadManager mockThreadManager = mock(ThreadManager.class);
    private final SearchInformation mockInformation = mock(SearchInformation.class);
    private final HttpRequestSender mockHttpRequestSender = mock(HttpRequestSender.class);

    private static final String KEYWORD = "magic";
    private static final String BASEURL = "https://example.com";

    @ParameterizedTest
    @MethodSource("provideLinesForRunSearchForKeywords")
    void run_updateUrlsMapWhenKeywordIsFoundInPageSource(final List<String> lines, final int expected) {
        // given
        val searchThread = new SearchThread(this.mockThreadManager, this.mockInformation, BASEURL, this.mockHttpRequestSender);

        when(this.mockInformation.getKeyword()).thenReturn(KEYWORD);
        when(this.mockHttpRequestSender.doGetRequest(BASEURL)).thenReturn(lines);

        // when
        searchThread.run();

        // then
        verify(this.mockThreadManager, times(expected)).updateUrlKeywordWasFound(BASEURL);
    }

    private static List<Arguments> provideLinesForRunSearchForKeywords() {
        return List.of(
            Arguments.of(List.of("this line is magic"), 1),
            Arguments.of(List.of("this is not"), 0),
            Arguments.of(List.of(" "), 0),
            Arguments.of(List.of("this is a magical line with magic"), 1),
            Arguments.of(List.of("THIS IS VERY MAGIC"), 1),
            Arguments.of(List.of("this m a g i c was broken, doesn't work anymore"), 0),
            Arguments.of(List.of("this line has the keyword magic", "this line does not"), 1),
            Arguments.of(List.of("the next line has the word you want", "and it works like magic"), 1)
        );
    }

    @ParameterizedTest
    @MethodSource("provideLinesForRunSearchForUrls")
    void run_startNewThreadsWhenValidUrlIsFoundInPageSource(final List<String> lines, final List<String> expected) {
        // given
        val searchThread = new SearchThread(this.mockThreadManager, this.mockInformation, BASEURL, this.mockHttpRequestSender);

        when(this.mockInformation.getBaseurl()).thenReturn(BASEURL);
        when(this.mockInformation.getKeyword()).thenReturn(KEYWORD);
        when(this.mockHttpRequestSender.doGetRequest(any())).thenReturn(lines);

        // when
        searchThread.run();

        // then
        expected.forEach(expectedUrl -> verify(this.mockThreadManager).run(expectedUrl));
    }

    private static List<Arguments> provideLinesForRunSearchForUrls() {
        return List.of(
            Arguments.of(
                List.of("<a href=\"foo.html\">one valid url</a>"),
                List.of("https://example.com/foo.html")
            ),
            Arguments.of(
                List.of("<a href=\"FOO.HTML\">one valid url</a>"),
                List.of("https://example.com/FOO.HTML")
            ),
            Arguments.of(
                List.of("this is not a url"),
                emptyList()
            ),
            Arguments.of(
                List.of(" "),
                emptyList()
            ),
            Arguments.of(
                List.of("<a href=\"bar.html\">one valid url</a> <a href=\"baz.html\">two valid urls</a>"),
                List.of("https://example.com/bar.html", "https://example.com/bar.html")
            ),
            Arguments.of(
                List.of("<a href=\"bar.html\">one valid url</a> <a invalid>one invalid url</a>"),
                List.of("https://example.com/bar.html")
            ),
            Arguments.of(
                List.of("<a invalid>one invalid url</a> <a href=\"baz.html\">one valid url</a>"),
                List.of("https://example.com/baz.html")
            ),
            Arguments.of(
                List.of("<a href=\"https://foo.com\">one external https url</a>"),
                emptyList()
            ),
            Arguments.of(
                List.of("<a href=\"http://foo.com\">one external http url</a>"),
                emptyList()
            ),
            Arguments.of(
                List.of("<a href=\"../bar.html\"> relative url outside of scope</a>"),
                emptyList()
            )
        );
    }

}
