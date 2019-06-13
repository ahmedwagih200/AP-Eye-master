package com.example.apeye.model;

import com.squareup.moshi.Json;

/**
 * Created by Abdel-Rahman El-Shikh on 13-Jun-19.
 */
public class ApiSignUpResponse {

    @Json(name = "Result")
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
