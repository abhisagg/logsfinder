package com.example.logsfinder.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class LogsFinderConfig {
    @Value("${logs.s3.bucket}")
    String bucket;
}
