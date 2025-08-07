package blarknes.keywordfinder.api.search;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import blarknes.keywordfinder.api.search.model.SearchInformation;
import blarknes.keywordfinder.http.HttpRequestSender;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.apachecommons.CommonsLog;

/**
 * Manages everything related to the search threads.
 */
@RequiredArgsConstructor
@CommonsLog
public class ThreadManager {

    private final SearchInformation information;
    private final ExecutorService executor;
    private final HttpRequestSender httpRequestSender;
    private final AtomicInteger runningThreadsCount;

    /**
     * Spawns a new thread if the url is not present on the searched ones.
     *
     * @param url the url to spawn a new thread
     */
    public void run(final String url) {
        this.information.getUrls().computeIfAbsent(url, key -> {
            spawnNewThread(url);
            return false;
        });
    }

    /**
     * Updates the map of searched urls to a positive value because the keyword was
     * found on the search.
     *
     * @param url the url to be updated
     */
    public void updateUrlKeywordWasFound(final String url) {
        this.information.getUrls().compute(url, (key, value) -> {
            return true;
        });
    }

    /**
     * Spawns a new search thread for the provided url and keeps track of the
     * current running thread count so that if it reaches 0 the status of the search
     * is updated to done and the pool is shutdown.
     *
     * @param url the url to be searched
     */
    private void spawnNewThread(final String url) {
        val thread = new SearchThread(this, this.information, url, this.httpRequestSender);
        this.runningThreadsCount.incrementAndGet();

        CompletableFuture.runAsync(thread, this.executor)
            .whenComplete((result, throwable) -> {
                if (throwable != null) {
                    log.warn(throwable);
                }

                val threads = this.runningThreadsCount.decrementAndGet();
                if (threads < 1) {
                    closeSearch();
                }
            });
    }

    private void closeSearch() {
        log.info(String.format(
            "Search %s found %s in %d url(s)",
            this.information.getId(),
            this.information.getKeyword(),
            this.information.getUrlsKeywordFoundList().size()
        ));

        this.information.markAsDone();
    }

}
