package com.texas.smart.job.portal.config.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve static files from uploads directory
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + uploadDir + "/")
                .setCachePeriod(3600); // 1 hour cache

        // Serve company images
        registry.addResourceHandler("/files/company/**")
                .addResourceLocations("file:" + uploadDir + "/company/")
                .setCachePeriod(3600);

        // Serve job seeker images
        registry.addResourceHandler("/files/jobseeker/**")
                .addResourceLocations("file:" + uploadDir + "/jobseeker/")
                .setCachePeriod(3600);

        // Serve job attachments
        registry.addResourceHandler("/files/job/**")
                .addResourceLocations("file:" + uploadDir + "/job/")
                .setCachePeriod(3600);
    }
}