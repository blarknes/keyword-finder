package com.keywordfinder.service;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import com.keywordfinder.model.SearchInformation;

public class ThreadService implements Runnable {

    private final ExecutorService executor;
    private final Map<String, Boolean> urlsAccessed;
    private final AtomicInteger threadCounter;
    private final SearchInformation information;
    private final URL currentUrl;
    private final URL baseurl;
    private final String keyword;

    private final CrawlService crawlService;

    /**
     * Constructs a ThreadService object to move along the parameters initialized on
     * the first thread created by the end user request to find a desired keyword.
     * 
     * @param executor      The ExecutorService of each request.
     * @param urlsAccessed  The urls accessed currently in the search.
     * @param threadCounter The thread counter.
     * @param information   The user provided search information.
     * @param currentUrl    The current url to be investigated.
     */
    public ThreadService(ExecutorService executor, Map<String, Boolean> urlsAccessed, AtomicInteger threadCounter,
            SearchInformation information, URL currentUrl) {
        this.executor = executor;
        this.urlsAccessed = urlsAccessed;
        this.threadCounter = threadCounter;
        this.information = information;
        this.currentUrl = currentUrl;
        this.baseurl = information.getBaseurl();
        this.keyword = information.getKeyword();

        this.crawlService = new CrawlService(this.executor, this.urlsAccessed, this.threadCounter, this.information,
                this.currentUrl, this.baseurl, this.keyword);
    }

    /**
     * Adds one thread to the counter and starts the crawling service if the current
     * url was not accessed prior to this instance.
     */
    private void start() {
        synchronized (this.threadCounter) {
            this.threadCounter.incrementAndGet();
        }

        this.urlsAccessed.computeIfAbsent(this.currentUrl.toString(), key -> {
            crawlService.searchInHTML();
            return true;
        });
    }

    /**
     * On each new Thread initialization of the ThreadService class, runs start
     * function to kickstart the crawling process accordingly.
     */
    @Override
    public void run() {
        start();
    }

    /**
     * After all threads are finished, shuts down executors for proper resource
     * cleanup, preventing thread leakage, ensuring graceful termination of tasks,
     * and future-proofing the code. Doing so to help release resources, avoid
     * memory leaks, and allow tasks to complete gracefully.
     */
    public void shutdown() {
        if (!this.executor.isShutdown()) {
            this.executor.shutdown();
        }
    }

}
