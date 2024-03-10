package com.example.logsfinder.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    boolean success = false;
    String msg;
}
