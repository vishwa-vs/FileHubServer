package com.filehub.FileHubServer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private int status;    // HTTP status code
    private String message;
    private T data;
}

