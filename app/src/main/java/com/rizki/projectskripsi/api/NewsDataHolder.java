package com.rizki.projectskripsi.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsDataHolder {

    @GET("news")
    Call<ResponseApi> getNews();
}
