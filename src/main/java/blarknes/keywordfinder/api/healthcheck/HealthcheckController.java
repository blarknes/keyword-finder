package blarknes.keywordfinder.api.healthcheck;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The controller that handles every health check and informative action.
 */
@Controller
@RequestMapping("/")
public class HealthcheckController {

    @GetMapping
    @ResponseBody
    private String check() {
        return "OK";
    }

}
