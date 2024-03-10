package com.example.logsfinder.service;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.example.logsfinder.config.LogsFinderConfig;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LogsFinderServiceTest {
    @MockBean
    private AmazonS3 amazonS3;

    @MockBean
    private LogsFinderConfig logsFinderConfig;

    @Autowired
    private LogsFinderService logsFinderService;

    @Test
    public void testSearchLogs_CaseSensitive() throws Exception {
        String searchWord = "hello world";
        long from = Instant.parse("2022-01-01T00:00:00Z").getEpochSecond();
        long to = Instant.parse("2022-01-01T00:30:00Z").getEpochSecond();
        boolean ignoreCase = false;

        S3Object s3Object = new S3Object();
        S3ObjectInputStream objectInputStream = new S3ObjectInputStream(
                new ByteArrayInputStream("hello world\nHello World".getBytes()), null);
        s3Object.setObjectContent(objectInputStream);

        when(amazonS3.getObject(any(GetObjectRequest.class))).thenReturn(s3Object);
        when(logsFinderConfig.getBucket()).thenReturn("test-bucket");

        List<String> matchingLogs = null;
        matchingLogs = logsFinderService.searchLogs(searchWord, from, to, ignoreCase);
        
        assertEquals(Arrays.asList("hello world"), matchingLogs);
    }

    @Test
    public void testSearchLogs_IgnoreCase() throws Exception {
        String searchWord = "hello world";
        long from = Instant.parse("2022-01-01T00:00:00Z").getEpochSecond();
        long to = Instant.parse("2022-01-01T00:30:00Z").getEpochSecond();
        boolean ignoreCase = true;

        S3Object s3Object = new S3Object();
        S3ObjectInputStream objectInputStream = new S3ObjectInputStream(
                new ByteArrayInputStream("hello world\nHello World".getBytes()), null);
        s3Object.setObjectContent(objectInputStream);

        when(amazonS3.getObject(any(GetObjectRequest.class))).thenReturn(s3Object);
        when(logsFinderConfig.getBucket()).thenReturn("test-bucket");

        List<String> matchingLogs = null;
        matchingLogs = logsFinderService.searchLogs(searchWord, from, to, ignoreCase);
        
        assertEquals(Arrays.asList("hello world", "Hello World"), matchingLogs);
    }

    @Test
    public void testSearchLogs_NoMatch() throws Exception {
        String searchWord = "hello world";
        long from = Instant.parse("2022-01-01T00:00:00Z").getEpochSecond();
        long to = Instant.parse("2022-01-01T00:30:00Z").getEpochSecond();
        boolean ignoreCase = true;
    
        S3Object s3Object = new S3Object();
        S3ObjectInputStream objectInputStream = new S3ObjectInputStream(
                new ByteArrayInputStream("world\n World".getBytes()), null);
        s3Object.setObjectContent(objectInputStream);

        when(amazonS3.getObject(any(GetObjectRequest.class))).thenReturn(s3Object);
        when(logsFinderConfig.getBucket()).thenReturn("test-bucket");

        List<String> matchingLogs = null;
        matchingLogs = logsFinderService.searchLogs(searchWord, from, to, ignoreCase);
        assertEquals(Arrays.asList(), matchingLogs);
    }

    @Test
    public void testSearchLogs_MultipleFiles() throws Exception {
        String searchWord = "hello world";
        long from = Instant.parse("2022-01-01T11:00:00Z").getEpochSecond();
        long to = Instant.parse("2022-01-01T13:30:00Z").getEpochSecond();
        boolean ignoreCase = true;

        when(amazonS3.getObject(any(GetObjectRequest.class))).thenAnswer(invocation -> {
            GetObjectRequest getObjectRequest = invocation.getArgument(0);
            String filePath = getObjectRequest.getKey();

            S3Object s3Object = new S3Object();
            S3ObjectInputStream objectInputStream;
            if ("2022-01-01/11.txt".equals(filePath)) {
                objectInputStream = new S3ObjectInputStream(
                        new ByteArrayInputStream("hello world 1".getBytes()), null);
                s3Object.setObjectContent(objectInputStream);
                return s3Object;
            } else if ("2022-01-01/12.txt".equals(filePath)) {
                objectInputStream = new S3ObjectInputStream(
                        new ByteArrayInputStream("hello world 2".getBytes()), null);
            } else {
                objectInputStream = new S3ObjectInputStream(
                        new ByteArrayInputStream("world".getBytes()), null);
            }
            s3Object.setObjectContent(objectInputStream);
            return s3Object;
        });
        when(logsFinderConfig.getBucket()).thenReturn("test-bucket");

        List<String> matchingLogs = null;
        matchingLogs = logsFinderService.searchLogs(searchWord, from, to, ignoreCase);
        assertEquals(Arrays.asList("hello world 1", "hello world 2"), matchingLogs);
    }
}
