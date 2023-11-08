package blarknes.keywordfinder.api.search.utility;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import blarknes.keywordfinder.api.search.model.SearchInformation;
import lombok.val;

public class IdUtilitiesTests {

    private static final String ID_1 = "05GNJVHm";
    private static final String ID_2 = "CO5ajf5b";
    private static final String KEYWORD = "magic";
    private static final String BASEURL = "baseurl";

    private final SearchInformation information = new SearchInformation(ID_1, KEYWORD, BASEURL);

    @Test
    void generateNewId_shouldReturnNewValidUniqueId() {
        // given
        val searches = new HashMap<String, SearchInformation>();

        // when
        val generatedId = IdUtilities.generateNewId(searches);

        // then
        assertThat(generatedId).isNotNull();
        assertThat(generatedId).isNotBlank();
    }

    @Test
    void generateNewId_shouldReturnNewUniqueIdWhenMapIsNull() {
        // given
        Map<String, SearchInformation> searches = null;

        // when
        val generatedId = IdUtilities.generateNewId(searches);

        // then
        assertThat(generatedId).isNotNull();
        assertThat(generatedId).isNotBlank();
    }

    @Test
    void idExists_shouldReturnTrueWhenMapContainsId() {
        // given
        val searches = new HashMap<String, SearchInformation>();
        searches.put(ID_1, this.information);

        // when
        val result = IdUtilities.idExists(searches, ID_1);

        // then
        assertTrue(result);
    }

    @Test
    void idExists_shouldReturnFalseWhenMapDoesNotContainId() {
        // given
        val searches = new HashMap<String, SearchInformation>();
        searches.put(ID_1, information);

        // when
        val result = IdUtilities.idExists(searches, ID_2);

        // then
        assertFalse(result);
    }

    @Test
    void idExists_shouldReturnFalseWhenMapIsNull() {
        // given
        Map<String, SearchInformation> searches = null;

        // when
        val result = IdUtilities.idExists(searches, ID_1);

        // then
        assertFalse(result);
    }

}
