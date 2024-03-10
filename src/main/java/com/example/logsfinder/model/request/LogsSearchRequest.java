package com.example.logsfinder.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogsSearchRequest {
    private String searchKeyword;
    private long from;
    private long to;
    private boolean ignoreCase; 
}

