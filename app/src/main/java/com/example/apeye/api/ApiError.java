package com.example.apeye.api;

import java.util.List;
import java.util.Map;

/**
 * Created by Abdel-Rahman El-Shikh on 07-Jun-19.
 */
public class ApiError {
    private String message;
    private Map<String, List<String>> errors;

    public String getMessage() {
        return message;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }
}
