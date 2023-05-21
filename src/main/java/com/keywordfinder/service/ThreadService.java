package com.keywordfinder.service;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.keywordfinder.model.SearchInformation;

public class ThreadService {

    private final Logger log = LoggerFactory.getLogger(ThreadService.class);

    private final SearchInformation information;
    private final Map<String, Boolean> urlsAccessed;
    private final AtomicInteger threadCounter;
    private final ExecutorService executor;

    /**
     * Constructs a ThreadService object to initialize the parameters that will move
     * along on the created threads of the CrawlService to find a desired keyword.
     * 
     * @param information The user provided search information.
     */
    public ThreadService(final SearchInformation information) {
        this.information = information;
        this.urlsAccessed = new ConcurrentHashMap<>();
        this.threadCounter = new AtomicInteger(0);
        this.executor = Executors.newFixedThreadPool(256);
    }

    /**
     * Starts the crawling service if the current url was not accessed prior to this
     * instance.
     * 
     * @param url The new URL to be crawled.
     */
    public void run(final URL url) {
        urlsAccessed.computeIfAbsent(url.toString(), key -> {
            runNewThread(url);
            return true;
        });
    }

    /**
     * Starts a new Thread or each url found inside the HTML page text.
     * 
     * @param url The new URL to be crawled.
     */
    public void runNewThread(final URL url) {
        final var runnable = new CrawlService(this, this.information, url);

        System.out.println(this.threadCounter.get());
        this.threadCounter.incrementAndGet();

        CompletableFuture.runAsync(runnable, this.executor)
                .thenAccept(empty -> {
                    final var threads = threadCounter.decrementAndGet();
                    if (threads <= 1) {
                        shutdown();
                    }
                });
    }

    /**
     * After all threads are finished, shuts down executors for proper resource
     * cleanup, preventing thread leakage, ensuring graceful termination of tasks,
     * and future-proofing the code. Doing so to help release resources, avoid
     * memory leaks, and allow tasks to complete gracefully.
     */
    public void shutdown() {
        if (!this.executor.isShutdown()) {
            log.info(String.format("The search from id `%s` encountered the keyword `%s` in `%d` urls.",
                    this.information.getId(), this.information.getKeyword(), this.information.getUrls().size()));

            this.information.updateDone();
            this.executor.shutdown();
        }
    }

}
