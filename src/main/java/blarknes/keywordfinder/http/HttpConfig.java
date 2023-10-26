package blarknes.keywordfinder.http;

import java.net.http.HttpClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpConfig {

    @Bean
    HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

}
