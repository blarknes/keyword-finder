package blarknes.keywordfinder.api.search;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.regex.Pattern;

import blarknes.keywordfinder.api.search.model.SearchInformation;
import blarknes.keywordfinder.http.HttpRequestSender;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.apachecommons.CommonsLog;

/**
 * The Thread that contains the search functionalities and run them
 * asynchronously for each url.
 */
@RequiredArgsConstructor
@CommonsLog
public class SearchThread implements Runnable {

    private final ThreadManager threadManager;
    private final SearchInformation information;
    private final String currentUrl;
    private final HttpRequestSender httpRequestSender;

    private static final Pattern URL_REGEX_PATTERN = Pattern.compile("<a\\s+(?:[^>]*?\\s+)?href=([\"'])((?!\").*?)\\1", CASE_INSENSITIVE);

    /**
     * The runnable method to search for the desired informations on the web page.
     */
    @Override
    public void run() {
        val lines = httpRequestSender.doGetRequest(this.currentUrl);
        var found = false;

        for (val line : lines) {
            found = found ? true : searchForKeyword(line);
            searchForNewUrls(line);
        }
    }

    private boolean searchForKeyword(final String line) {
        val keyword = this.information.getKeyword().toLowerCase();

        if (line.toLowerCase().contains(keyword)) {
            this.threadManager.updateUrlKeywordWasFound(this.currentUrl);
            return true;
        }

        return false;
    }

    private void searchForNewUrls(final String line) {
        val matcher = URL_REGEX_PATTERN.matcher(line);
        val baseUrl = this.information.getBaseurl();

        while (matcher.find()) {
            val foundUrl = matcher.group(2);
            val formedUrl = buildUrlPath(foundUrl);

            val startsWithHttp = formedUrl.startsWith("http");
            val startsWithBaseurl = formedUrl.startsWith(baseUrl);

            if (formedUrl != null && startsWithHttp && startsWithBaseurl) {
                this.threadManager.run(formedUrl.toString());
            }
        }
    }

    private String buildUrlPath(final String foundUrl) {
        val baseUrl = this.information.getBaseurl();

        try {
            return URI.create(baseUrl).resolve(foundUrl).toURL().toString();
        } catch (MalformedURLException e) {
            log.warn("There was an error building the url " + foundUrl);
            return null;
        }
    }

}
