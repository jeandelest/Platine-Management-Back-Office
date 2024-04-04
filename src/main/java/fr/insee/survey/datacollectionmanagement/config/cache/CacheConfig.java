package fr.insee.survey.datacollectionmanagement.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {
    @Bean
    protected CaffeineCache habilitationCache() {
        return new CaffeineCache(CacheName.SOURCE_OPENED,
                Caffeine.newBuilder()
                        .initialCapacity(2000)
                        .maximumSize(20000)
                        .expireAfterAccess(10, TimeUnit.MINUTES)
                        .build());
    }
}
