package blarknes.keywordfinder.api.search;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import blarknes.keywordfinder.api.search.dto.ListDisplayElement;
import blarknes.keywordfinder.api.search.dto.ListDisplayResponse;
import blarknes.keywordfinder.api.search.dto.NewSearchResponse;
import blarknes.keywordfinder.api.search.dto.SearchRequestBody;
import blarknes.keywordfinder.api.search.dto.SingleDisplayResponse;
import blarknes.keywordfinder.api.search.exception.UnknownIdException;
import blarknes.keywordfinder.api.search.model.SearchInformation;
import blarknes.keywordfinder.api.search.utility.IdUtilities;
import blarknes.keywordfinder.http.HttpRequestSender;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * This service is responsible for managing and performing search operations.
 */
@Service
@RequiredArgsConstructor
public class SearchService {

    private final ExecutorService executor;
    private final HttpRequestSender httpRequestSender;
    private final AtomicInteger runningThreadsCount;
    private final Map<String, SearchInformation> searches = new ConcurrentHashMap<>();

    /**
     * Starts a new search process based on the request body.
     *
     * @param body the request body provide by the user
     * @return A new {@link NewSearchResponse} containing the id.
     */
    public NewSearchResponse newSearch(final SearchRequestBody body) {
        val id = IdUtilities.generateNewId(searches);

        val information = new SearchInformation(id, body.keyword(), body.baseurl());
        this.searches.put(id, information);

        spawnFirstSearchThread(information);

        return new NewSearchResponse(id);
    }

    /**
     * Retrieves all the current searches and formats them to a user readable
     * output.
     *
     * @return The list of every {@link ListDisplayElement}.
     */
    public ListDisplayResponse listSearches() {
        val elements = new ArrayList<ListDisplayElement>();

        for (val entry : searches.entrySet()) {
            elements.add(ListDisplayElement.fromSearchInformation(entry.getValue()));
        }

        return new ListDisplayResponse(elements);
    }

    /**
     * Retrieves a single search and format it to a user readable output.
     *
     * @param id the id of the desired search information
     * @return The user readable data of the search information.
     */
    public SingleDisplayResponse displaySearch(final String id) {
        if (!IdUtilities.idExists(this.searches, id)) {
            throw new UnknownIdException(id);
        }

        val information = this.searches.get(id);

        return SingleDisplayResponse.fromSearchInformation(information);
    }

    private void spawnFirstSearchThread(final SearchInformation information) {
        val threadManager = new ThreadManager(information, executor, httpRequestSender, runningThreadsCount);
        threadManager.run(information.getBaseurl());
    }

}
