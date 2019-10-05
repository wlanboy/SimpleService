package com.wlanboy.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.sleuth.metric.SpanMetricReporter;
//import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
//import org.springframework.cloud.sleuth.zipkin2.ZipkinProperties;
//import org.springframework.cloud.sleuth.zipkin2.ZipkinRestTemplateCustomizer;
//import org.springframework.cloud.sleuth.zipkin2.ZipkinSpanReporter;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@SpringBootApplication
public class AuditApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuditApplication.class, args);
    }

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludeHeaders(true);
        return loggingFilter;
    }
    
}
