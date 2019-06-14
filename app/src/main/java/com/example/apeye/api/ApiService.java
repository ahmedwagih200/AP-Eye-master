package com.example.apeye.api;

import com.example.apeye.model.ApiResponse;
import com.example.apeye.model.ApiResultResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Abdel-Rahman El-Shikh on 07-Jun-19.
 */
public interface ApiService {

    @POST("API/")
    @FormUrlEncoded
    Call<ApiResponse> classify(@Field("pathimage") String imgUrl,
                               @Field("plant") String plantKind,
                               @Field("username") String userName);

    @POST("signupapi/")
    @FormUrlEncoded
    Call<ApiResultResponse> signUp(@Field("email") String email,
                                   @Field("username") String userName,
                                   @Field("password") String password);

    @POST("analysisapi/")
    @FormUrlEncoded
    Call<ApiResultResponse> MakeAnalysis(@Field("plantname") String plant,
                                         @Field("username") String userName,
                                         @Field("date") String date);
}
