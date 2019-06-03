package com.example.whatstheweather;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatstheweather.Adapters.WeatherForcastAdapter;
import com.example.whatstheweather.Common.Common;
import com.example.whatstheweather.Models.WeatherForcastResult;
import com.example.whatstheweather.Retrofit.IOpenWeatherMap;
import com.example.whatstheweather.Retrofit.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForcastFragment extends Fragment {

    static ForcastFragment instance;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;
    private TextView cityName,geoCoord;
    private RecyclerView recyclerView;

    public static ForcastFragment getInstance() {
        if(instance==null)
            instance=new ForcastFragment();
        return instance;
    }

    public ForcastFragment() {
        // Required empty public constructor
        compositeDisposable=new CompositeDisposable();
        Retrofit retrofit= RetrofitClient.getInstance();
        mService=retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_forcast, container, false);

        cityName=(TextView)view.findViewById(R.id.cityNameForcast);
        geoCoord=(TextView)view.findViewById(R.id.geoCoordForcast);
        recyclerView=(RecyclerView)view.findViewById(R.id.forcastRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        getWeatherForcastInfo();
        return view;
    }

    private void getWeatherForcastInfo() {
        compositeDisposable.add(mService.getForcastWeatherByLatLong(
                String.valueOf(Common.currentLocation.getLatitude()),
                String.valueOf(Common.currentLocation.getLongitude()),
                Common.API_ID,
                "metrics")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherForcastResult>() {
                    @Override
                    public void accept(WeatherForcastResult weatherForcastResult) throws Exception {
                        displayForcastResult(weatherForcastResult);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        System.out.println(throwable.getMessage());
                        Toast.makeText(getActivity(),throwable.getMessage(),Toast.LENGTH_LONG).show();
                    }
                })
        );
    }

    private void displayForcastResult(WeatherForcastResult weatherForcastResult) {
        cityName.setText(new StringBuilder(weatherForcastResult.getCity().getName()));
        geoCoord.setText(new StringBuilder(weatherForcastResult.getCity().getCoord().toString()));

        WeatherForcastAdapter weatherForcastAdapter=new WeatherForcastAdapter(getContext(),weatherForcastResult);
        recyclerView.setAdapter(weatherForcastAdapter);
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

}
