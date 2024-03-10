package com.example.logsfinder.controlleradvice;

import org.springframework.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.logsfinder.model.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class LogsFinderControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse response = new ErrorResponse();
        response.setMsg("Internal server error");
        log.error(ex.getLocalizedMessage(), ex);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR));
    }

    @Override
    public ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        ErrorResponse resp = new ErrorResponse();
        resp.setMsg(ex.getLocalizedMessage());
        return super.handleExceptionInternal(ex, resp, headers, statusCode, request);
    }
}
