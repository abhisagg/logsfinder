package com.example.logsfinder.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.example.logsfinder.config.LogsFinderConfig;
import com.example.logsfinder.model.request.LogsSearchRequest;

@Service
public class LogsFinderService {
    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private LogsFinderConfig logsFinderConfig;

    private static final int THREAD_POOL_SIZE = 10;

    public List<String> searchLogs(LogsSearchRequest request) throws Exception {
        System.out.println("abcd");
        
        LocalDateTime fromDateTime = LocalDateTime.ofEpochSecond(request.getFrom(), 0, ZoneOffset.UTC);;
        LocalDateTime toDateTime = LocalDateTime.ofEpochSecond(request.getTo(), 0, ZoneOffset.UTC);;

        List<String> matchingLogs = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        List<Future<List<String>>> futures = new ArrayList<>();

        while (!fromDateTime.isAfter(toDateTime)) {
            System.out.println(fromDateTime + " " + toDateTime);
            String folderName = fromDateTime.toLocalDate().toString();
            int hour = fromDateTime.getHour();
            String filePath = folderName + "/" + String.format("%02d", hour) + ".txt";

            Callable<List<String>> task = () -> {
                List<String> logs = new ArrayList<>();
                BufferedReader reader = null;
                try {
                    S3Object s3Object = amazonS3.getObject(new GetObjectRequest(logsFinderConfig.getBucket(), filePath));
                    S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
                    reader = new BufferedReader(new InputStreamReader(objectInputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (request.isIgnoreCase() && line.toLowerCase().contains(request.getSearchKeyword().toLowerCase())) {
                            logs.add(line);
                        } else if (line.contains(request.getSearchKeyword())) {
                            logs.add(line);
                        }
                    }
                    reader.close();
                } catch (AmazonS3Exception e) {
                    if (reader != null) {
                        reader.close();
                    }
                    // ignore 404
                    if (e.getStatusCode() != 404) {
                        throw e;
                    }
                }
                return logs;
            };

            futures.add(executorService.submit(task));
            fromDateTime = fromDateTime.plusHours(1);
        }

        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        for (Future<List<String>> future : futures) {
            matchingLogs.addAll(future.get());
        }

        // TODO : Trim logs from start and end to exactly fit the input timestamps based on format of timestamp in logs
        return matchingLogs;
    }  
}
