package com.rizki.projectskripsi.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RsDataHolder {

    @GET("rs_covid_haversine")
    Call<ResponseApi> getRs(
            @Query("latitude") String latitude,
            @Query("longitude") String longitude,
            @Query("radius") String radius
    );
}
