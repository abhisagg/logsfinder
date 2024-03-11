package com.example.logsfinder.controller;

import java.util.List;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.logsfinder.model.response.LogsSearchResponse;
import com.example.logsfinder.service.LogsFinderService;

@RestController
@RequestMapping("/api/v1/logs")
public class LogsFinderController {

    @Autowired
    private LogsFinderService logsFinderService;

    @GetMapping("/search")
    public ResponseEntity<LogsSearchResponse> searchLogs(@RequestParam("q") String searchWord, @RequestParam long from, 
                                                         @RequestParam long to, @RequestParam(required = false) boolean ignoreCase) throws Exception {
        List<String> matchingLogs = logsFinderService.searchLogs(searchWord, from, to, ignoreCase);
        LogsSearchResponse response = new LogsSearchResponse();
        response.setLogs(matchingLogs);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(HttpStatus.SC_OK));
    }
}