package com.example.whatstheweather.Retrofit;

import com.example.whatstheweather.Models.WeatherForcastResult;
import com.example.whatstheweather.Models.WeatherResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IOpenWeatherMap {
    @GET("weather")
    Observable<WeatherResult> getWeatherByLatLong(@Query("lat") String lat,
                                                  @Query("lon") String lon,
                                                  @Query("appid") String appid,
                                                  @Query("units") String units);


    @GET("forecast")
    Observable<WeatherForcastResult> getForcastWeatherByLatLong(@Query("lat") String lat,
                                                                @Query("lon") String lon,
                                                                @Query("appid") String appid,
                                                                @Query("units") String units);
}
