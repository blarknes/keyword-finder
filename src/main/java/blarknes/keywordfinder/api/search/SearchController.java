package blarknes.keywordfinder.api.search;

import static org.springframework.http.HttpStatus.CREATED;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import blarknes.keywordfinder.api.search.dto.ListDisplayResponse;
import blarknes.keywordfinder.api.search.dto.NewSearchResponse;
import blarknes.keywordfinder.api.search.dto.SearchRequestBody;
import blarknes.keywordfinder.api.search.dto.SingleDisplayResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Routes all requests related to a word search.
 */
@Controller
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping({ "", "/" })
    @ResponseStatus(CREATED)
    @ResponseBody
    private NewSearchResponse newSearch(@Valid @RequestBody SearchRequestBody body) {
        return searchService.newSearch(body);
    }

    @GetMapping({ "", "/" })
    @ResponseBody
    private ListDisplayResponse listSearches() {
        return searchService.listSearches();
    }

    @GetMapping("/{id}")
    @ResponseBody
    private SingleDisplayResponse displaySearch(@PathVariable String id) {
        return searchService.displaySearch(id);
    }

}
