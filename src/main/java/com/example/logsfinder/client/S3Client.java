package com.example.logsfinder.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.example.logsfinder.config.AWSConfig;

@Configuration
class S3Client {
    @Autowired
    AWSConfig awsConfig;

    @Bean
    public AmazonS3 S3() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(
            awsConfig.getAccessKey(), 
            awsConfig.getSecretKey()
        );

        return AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).withRegion(awsConfig.getRegion()).build();
    }
}
