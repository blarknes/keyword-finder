package blarknes.keywordfinder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

import blarknes.keywordfinder.api.healthcheck.HealthcheckController;
import blarknes.keywordfinder.api.search.SearchController;
import lombok.val;
import lombok.extern.apachecommons.CommonsLog;

@SpringBootTest
@AutoConfigureMockMvc
@CommonsLog
public class MainTests {

    @Autowired
    private HealthcheckController healthcheckController;

    @Autowired
    private SearchController searchController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sanityCheck() {
        assertThat(healthcheckController).isNotNull();
        assertThat(searchController).isNotNull();
    }

    @Test
    void integrationTest() throws Exception {
        val healthcheckCheckResponse = this.mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        log.info(
            healthcheckCheckResponse.getStatus() + "\n" +
            healthcheckCheckResponse.getContentAsString()
        );

        val searchNewSearchBody = "{ \"keyword\": \"magic\", \"baseurl\": \"https://example.com\" }";
        val searchNewSearchResponse = this.mockMvc.perform(post("/search")
            .contentType(APPLICATION_JSON)
            .content(searchNewSearchBody))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse();

        log.info(
            searchNewSearchResponse.getStatus() + "\n" +
            new JSONObject(searchNewSearchResponse.getContentAsString()).toString(4)
        );

        val searchListSearchesResponse = this.mockMvc.perform(get("/search"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        log.info(
            searchListSearchesResponse.getStatus() + "\n" +
            new JSONObject(searchListSearchesResponse.getContentAsString()).toString(4)
        );

        val searchId = JsonPath.read(searchNewSearchResponse.getContentAsString(), "$.id");
        val searchDisplaySearchResponse = this.mockMvc.perform(get("/search/" + searchId))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        log.info(
            searchDisplaySearchResponse.getStatus() + "\n" +
            new JSONObject(searchDisplaySearchResponse.getContentAsString()).toString(4)
        );
    }

}
