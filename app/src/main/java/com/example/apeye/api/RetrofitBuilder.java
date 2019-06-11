package com.example.apeye.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * Created by Abdel-Rahman El-Shikh on 07-Jun-19.
 */
public class RetrofitBuilder {
    // El-Kashef Base URL
    //http://3.219.62.232:8000/apeye/API/?
    private static final String BASE_URL = "http://3.219.62.232:8000/apeye/API/";

    // this olHttpClinet is for adding our custom Http headers
    private final static OkHttpClient clinet = buildClient();

    private final static Retrofit retrofit = buildRetrofit();

    private static OkHttpClient buildClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Request.Builder builder = request.newBuilder()
                                .addHeader("Accept","application/json")
                                .addHeader("Connection","close");
                        request = builder.build();
                        return chain.proceed(request);
                    }
                });
        return builder.build();
    }

    private static Retrofit buildRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(clinet)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
    }

    public static <T> T createService(Class<T> service) {
        return retrofit.create(service);
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }


}
