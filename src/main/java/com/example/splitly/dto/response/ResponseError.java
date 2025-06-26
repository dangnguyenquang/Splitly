package com.example.splitly.dto.response;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseError extends ResponseData {

    private String error;
    private String path;
    private Date timestamp;

    public ResponseError(int status, String message) {
        super(status, message);
    }
}
