package blarknes.keywordfinder.api.search.utilities;

import java.util.Map;

import blarknes.keywordfinder.api.search.model.SearchInformation;
import lombok.experimental.UtilityClass;

/**
 * Utility class for ID actions.
 */
@UtilityClass
public class IdUtilities {

    public static final Integer RANDOM_ID_SIZE = 8;
    private static final String ALPHANUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    /**
     * Generates a random alphanumeric ID that is unique based on the current
     * existent ids.
     *
     * @param searches the current array of searches
     * @return A new unique ID.
     */
    public static String generateNewId(final Map<String, SearchInformation> searches) {
        var result = "";

        while (result.isEmpty() || idExists(searches, result)) {
            for (int i = 0; i < RANDOM_ID_SIZE; i++) {
                result += ALPHANUMERIC.charAt((int) (Math.random() * ALPHANUMERIC.length()));
            }
        }

        return result;
    }

    /**
     * Checks if an ID is already in use on the pool of searches.
     *
     * @param searches the pool of searches
     * @param id       the ID to check
     * @return If the ID is in use or not.
     */
    public static boolean idExists(final Map<String, SearchInformation> searches, final String id) {
        if (searches == null) {
            return false;
        }

        return searches.keySet().contains(id);
    }

}
