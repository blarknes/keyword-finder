package blarknes.keywordfinder.api.healthcheck;

import static org.springframework.http.HttpStatus.OK;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Routes every health check and informative action.
 */
@Controller
@RequestMapping("/")
public class HealthcheckController {

    @GetMapping
    @ResponseBody
    private String check() {
        return OK.getReasonPhrase();
    }

}
