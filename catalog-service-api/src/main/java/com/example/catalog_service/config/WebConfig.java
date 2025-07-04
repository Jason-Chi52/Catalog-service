// Path: catalog-service-api/src/main/java/com/example/catalog_service/config/WebConfig.java

package com.example.catalog_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global CORS configuration to allow calls from our React UI.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configure CORS mappings for the entire API.
     * Allows React dev server at localhost:5173 to call any endpoint,
     * with any HTTP method and any header (including Authorization).
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
          .addMapping("/**")
          .allowedOrigins("http://localhost:5173")
          .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
          .allowedHeaders("*")
          .allowCredentials(true);
    }
}
