package com.keywordfinder.service;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static org.eclipse.jetty.http.HttpMethod.GET;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import com.keywordfinder.model.SearchInformation;
import com.keywordfinder.utilities.HttpClient;

public class CrawlService implements Runnable {

    private final ThreadService threadService;
    private final URL currentUrl;
    private final URL baseurl;
    private final String keyword;

    private final HttpClient client;

    private final String URL_REGEX = "<a\\s+(?:[^>]*?\\s+)?href=([\"'])((?!\").*?)\\1";
    private final Pattern PATTERN = Pattern.compile(URL_REGEX, CASE_INSENSITIVE);

    /**
     * Construtcts a CrawlService object to be able to search in the HTML text the
     * desired keyword and new urls to repeat the process.
     * 
     * @param threadService The ThreadService of the user request.
     * @param information   The Information of the user request.
     * @param currentUrl    The current URL to be crawled.
     */
    public CrawlService(final ThreadService threadService, final SearchInformation information, final URL currentUrl,
            final HttpClient client) {
        this.threadService = threadService;
        this.currentUrl = currentUrl;
        this.client = client;
        this.baseurl = information.getBaseurl();
        this.keyword = information.getKeyword();
    }

    /**
     * Performs a HTTP GET request to get the contents of the current urls HTML.
     * <p>
     * Then iterates through each line of the content, looking for urls inside
     * anchor tags and the desired keyword if it wasn't already found in a previous
     * line.
     */
    private void searchInHTML() {
        final var lines = client.makeHttpRequest(this.currentUrl, GET).getBodyAsList();
        var found = false;

        for (String line : lines) {
            if (!found) {
                found = searchKeyword(line);
            }
            searchUrls(line);
        }
    }

    /**
     * Searches the desired keyword from the user inside the provided HTML text
     * line.
     * 
     * @param line The current line of the page HTML.
     * @return If the word was found or not.
     */
    private boolean searchKeyword(String line) {
        if (line.toLowerCase().contains(this.keyword.toLowerCase())) {
            this.threadService.addURLFoundKeyword(this.currentUrl.toString());
            return true;
        }
        return false;
    }

    /**
     * Searches the current HTML text line for anchor tags.
     * <p>
     * Then check if the anchor tag contains a valid url, and subsequently check if
     * the url is a subdomain of the baseurl and removes fragment identifiers if
     * there are any.
     * 
     * @param line The current line of the page HTML.
     */
    private void searchUrls(String line) {
        final var matcher = PATTERN.matcher(line);

        while (matcher.find()) {
            /**
             * The new urls will be in the second matched group of the regex, then will have
             * everything after `#` replaced to avoid repetition created by fragment
             * identifiers.
             */
            final URL newUrl;
            final var newUrlAsString = matcher.group(2);

            try {
                newUrl = new URL(this.baseurl, newUrlAsString);
            } catch (MalformedURLException e) {
                continue;
            }

            /**
             * Checking if the new urls starts with http or https and if they start with the
             * user provided base url.
             */
            if (!newUrl.getProtocol().startsWith("http") || !newUrl.toString().startsWith(this.baseurl.toString())) {
                continue;
            }

            this.threadService.run(newUrl);
        }
    }

    /**
     * Kickstarts the searching in the page when the CrawlService object is
     * instanciated.
     */
    @Override
    public void run() {
        searchInHTML();
    }

}
