package com.rizki.projectskripsi.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Repository {

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://wendei.id/rs_rizki/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
