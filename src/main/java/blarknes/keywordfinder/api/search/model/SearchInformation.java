package blarknes.keywordfinder.api.search.model;

import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;

/**
 * The information of the user search.
 */
@Getter
public class SearchInformation {

    private String id;
    private String keyword;
    private String baseurl;
    private Boolean done;
    private Map<String, Boolean> urls;

    /**
     * Builds a new SearchInformation object based on the user input.
     *
     * @param id      the generated ID
     * @param keyword the word to be searched
     * @param baseurl the base url of the website
     */
    public SearchInformation(final String id, final String keyword, final String baseurl) {
        this.id = id;
        this.keyword = keyword;
        this.baseurl = baseurl;
        this.done = false;
        this.urls = new ConcurrentHashMap<>();
    }

    /**
     * Converts the search status to a more user readable string.
     *
     * @return the search status converted to a string
     */
    public String getDone() {
        return this.done ? "done" : "running";
    }

    /**
     * Updates the search status to done.
     */
    public void updateDone() {
        this.done = true;
    }

    /**
     * Filters every searched url to only those in which the word was found and
     * convert it to a sorted list.
     *
     * @return a sorted list containing urls
     */
    public List<String> getUrlsKeywordFoundList() {
        return this.urls.entrySet()
            .stream()
            .filter(Map.Entry::getValue)
            .map(Map.Entry::getKey)
            .sorted()
            .collect(toUnmodifiableList());
    }

}
