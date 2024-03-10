package com.example.logsfinder.model.response;
import java.util.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogsSearchResponse {
    boolean success = true;
    List<String> logs;
}
