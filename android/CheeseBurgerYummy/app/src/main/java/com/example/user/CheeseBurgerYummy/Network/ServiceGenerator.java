package com.example.user.CheeseBurgerYummy.Network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by user on 12/13/16.
 */

public class ServiceGenerator {
    /*
    public static final String API_URL = "http://127.0.0.1:8000";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }*/

    public static final String BASE_URL = "http://192.168.0.9:8080";
    private static Retrofit retrofit = null;
    static OkHttpClient httpClient = new OkHttpClient.Builder()
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request.Builder ongoing = chain.request().newBuilder();
                    ongoing.addHeader("Accept", "application/json;versions=1");
                    /*
                    if (isUserLoggedIn()) {
                        ongoing.addHeader("Authorization", getToken());
                    }*/
                    return chain.proceed(ongoing.build());
                }
            })
            .build();

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())/*.client(httpClient)*/.build();
        }
        return retrofit;
    }
}
