package com.wlanboy.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.sleuth.metric.SpanMetricReporter;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.cloud.sleuth.zipkin2.ZipkinProperties;
import org.springframework.cloud.sleuth.zipkin2.ZipkinRestTemplateCustomizer;
import org.springframework.cloud.sleuth.zipkin2.ZipkinSpanReporter;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import zipkin2.Span;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludeHeaders(true);
        return loggingFilter;
    }
    
//    @Bean
//    public AlwaysSampler defaultSampler() {
//      return new AlwaysSampler();
//    }    
    
}
