package ru.yjailbir.shopappyandexpracticumsprint5.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class WebConfig implements WebFluxConfigurer {
    @Value("${values.img_folder}")
    String imagesFolder;

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(Path.of(imagesFolder));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/images/**")
                .addResourceLocations("file:" + imagesFolder);
    }
}
