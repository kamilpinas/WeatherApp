package com.example.weather;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JsonPlaceHolder {
    @GET("/data/2.5/weather")
    Call<WeatherData> getWeatherData(@Query("q") String name,
                                     @Query("APPID") String APPID,
                                     @Query("units")String units);
}
