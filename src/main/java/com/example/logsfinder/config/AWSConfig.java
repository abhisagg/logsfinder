package com.example.logsfinder.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.regions.Regions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class AWSConfig {
    @Value("${aws.access.key}")
    String accessKey;
    @Value("${aws.secret.key}")
    String secretKey;
    @Value("${aws.region.name}")
    String regionName;


    public Regions getRegion() {
        return Regions.fromName(regionName);
    }
}
