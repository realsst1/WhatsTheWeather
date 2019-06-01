package com.example.whatstheweather.Retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {


    private static Retrofit instance;

    public static Retrofit getInstance(){
        if(instance==null)
            instance=new Retrofit.Builder()
                    .baseUrl("")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        return instance;
    }
}
