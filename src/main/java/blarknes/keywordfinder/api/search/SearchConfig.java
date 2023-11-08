package blarknes.keywordfinder.api.search;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SearchConfig {

    @Bean
    ExecutorService executorService(@Value("${thread.pool.size}") int size) {
        return Executors.newFixedThreadPool(size);
    }

    @Bean
    AtomicInteger atomicInteger() {
        return new AtomicInteger(0);
    }

}
