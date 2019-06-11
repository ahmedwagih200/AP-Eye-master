package com.example.apeye.model;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by Abdel-Rahman El-Shikh on 07-Jun-19.
 */
public class ApiResponse {


    @Json(name = "flowers")
    private List<String> flowers = null;

    @Json(name = "pred")

    private List<String> pred = null;

    public List<String> getFlowers() {
        return flowers;
    }

    public void setFlowers(List<String> flowers) {
        this.flowers = flowers;
    }

    public List<String> getPred() {
        return pred;
    }

    public void setPred(List<String> pred) {
        this.pred = pred;
    }

}
