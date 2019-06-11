package com.example.apeye.api;

import com.example.apeye.model.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Abdel-Rahman El-Shikh on 07-Jun-19.
 */
public interface ApiService {

    @POST("?")
    @FormUrlEncoded
    Call<ApiResponse> classify(@Field("pathimage") String imgUrl,
                               @Field("Plant") String plantKind);
}
