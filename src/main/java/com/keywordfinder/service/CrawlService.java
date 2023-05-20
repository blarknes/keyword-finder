package com.keywordfinder.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.eclipse.jetty.http.HttpMethod;

import com.keywordfinder.model.SearchInformation;
import com.keywordfinder.utilities.HttpClient;

public class CrawlService {

    private final ExecutorService executor;
    private final Map<String, Boolean> urlsAccessed;
    private final AtomicInteger threadCounter;
    private final SearchInformation information;
    private final URL currentUrl;
    private final URL baseurl;
    private final String keyword;

    private final HttpClient client = new HttpClient();

    private final String URL_REGEX = "<a\\s+(?:[^>]*?\\s+)?href=([\"'])((?!\").*?)\\1";
    private final Pattern PATTERN = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE);

    public CrawlService(ExecutorService executor, Map<String, Boolean> urlsAccessed, AtomicInteger threadCounter,
            SearchInformation information, URL currentUrl, URL baseurl, String keyword) {
        this.executor = executor;
        this.urlsAccessed = urlsAccessed;
        this.threadCounter = threadCounter;
        this.information = information;
        this.currentUrl = currentUrl;
        this.baseurl = baseurl;
        this.keyword = keyword;
    }

    /**
     * Performs a HTTP GET request to get the contents of the current urls HTML.
     * <p>
     * Then iterates through each line of the content, looking for urls inside
     * anchor tags and the desired keyword if it wasn't already found in a previous
     * line.
     */
    public void searchInHTML() {
        final var lines = client.makeHttpRequest(this.currentUrl, HttpMethod.GET).getBodyAsList();
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
            var urls = this.information.getUrls();
            urls.put(this.currentUrl.toString(), true);
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
            final URL newUrl;

            /**
             * The new urls will be in the second matched group of the regex, then will have
             * everything after `#` replaced to avoid repetition created by fragment
             * identifiers.
             */
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

            startNewThread(newUrl);
        }
    }

    /**
     * Starts a new Thread or each url found inside the HTML page text.
     * 
     * @param newUrl The new URL to be crawled.
     */
    private void startNewThread(URL newUrl) {
        ThreadService crawlService = new ThreadService(this.executor, this.urlsAccessed, this.threadCounter,
                this.information, newUrl);

        CompletableFuture.runAsync(crawlService, this.executor)
                .thenRun(() -> {
                    synchronized (this.threadCounter) {
                        final var threads = this.threadCounter.decrementAndGet();
                        if (threads <= 1) {
                            this.information.updateDone();
                            crawlService.shutdown();
                        }
                    }
                });
    }

}
