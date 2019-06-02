package com.example.whatstheweather;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatstheweather.Common.Common;
import com.example.whatstheweather.Models.Coord;
import com.example.whatstheweather.Models.Weather;
import com.example.whatstheweather.Models.WeatherResult;
import com.example.whatstheweather.Retrofit.IOpenWeatherMap;
import com.example.whatstheweather.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class TodayFragment extends Fragment {

    static  TodayFragment instance;
    private ImageView imageView;
    private TextView wind,cityName,cityDesc,humidity,sunset,sunrise,pressure,temp,dateTime,geoCoord;
    private LinearLayout weatherPanel;
    private ProgressBar loadingCurrentWeather;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    public static TodayFragment getInstance() {
        if(instance==null)
            instance=new TodayFragment();
        return instance;
    }

    public TodayFragment() {
        // Required empty public constructor
        compositeDisposable=new CompositeDisposable();
        Retrofit retrofit= RetrofitClient.getInstance();
        mService=retrofit.create(IOpenWeatherMap.class);
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_today, container, false);

        imageView=(ImageView)view.findViewById(R.id.weatherImage);
        cityDesc=(TextView)view.findViewById(R.id.weatherDesc);
        cityName=(TextView)view.findViewById(R.id.cityName);
        sunrise=(TextView)view.findViewById(R.id.sunrise);
        sunset=(TextView)view.findViewById(R.id.sunset);
        wind=(TextView)view.findViewById(R.id.windSpeed);
        pressure=(TextView)view.findViewById(R.id.pressure);
        humidity=(TextView)view.findViewById(R.id.humidity);
        geoCoord=(TextView)view.findViewById(R.id.geoCoord);
        temp=(TextView)view.findViewById(R.id.temperature);
        dateTime=(TextView)view.findViewById(R.id.weatherDateTime);

        weatherPanel=(LinearLayout)view.findViewById(R.id.weatherPanel);
        loadingCurrentWeather=(ProgressBar)view.findViewById(R.id.loadingCurrentWeather);

        getWeatherInfo();
        return view;
    }
    private void getWeatherInfo() {
       compositeDisposable.add(mService.getWeatherByLatLong(String.valueOf(Common.currentLocation.getLatitude()),
               String.valueOf(Common.currentLocation.getLongitude()),
               Common.API_ID,
               "metric")
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new Consumer<WeatherResult>() {
                   @Override
                   public void accept(@NonNull WeatherResult weatherResult) throws Exception {
                       System.out.println("we:"+weatherResult);

                       Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                               .append(weatherResult.getWeather().get(0).getIcon())
                               .append(".png").toString()).into(imageView);
                       //Weather Info
                       System.out.println("City:"+weatherResult.getName());
                       cityName.setText(weatherResult.getName());
                       cityDesc.setText(new StringBuilder("Weather in ").append(weatherResult.getName()).toString());
                       temp.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp())).append("°C").toString());
                       dateTime.setText(Common.convertUnixToDate(weatherResult.getDt()));
                       pressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure())).append(" hpa").toString());
                       humidity.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity())).append(" %").toString());
                       sunrise.setText(Common.convertUnixToHour(weatherResult.getSys().getSunrise()));
                       sunset.setText(Common.convertUnixToHour(weatherResult.getSys().getSunset()));
                       geoCoord.setText(new StringBuilder("[").append(weatherResult.getCoord().toString()).append("]").toString());
                       wind.setText(new StringBuilder(String.valueOf(weatherResult.getWind().getSpeed())).append(" ").append(String.valueOf(
                               weatherResult.getWind().getDeg())));
                       //Panel
                       weatherPanel.setVisibility(View.VISIBLE);
                       loadingCurrentWeather.setVisibility(View.GONE);

                   }
               }, new Consumer<Throwable>() {
                   @Override
                   public void accept(@NonNull Throwable throwable) throws Exception {
                       System.out.println(throwable.getLocalizedMessage());
                       Toast.makeText(getActivity(),throwable.getMessage(),Toast.LENGTH_LONG).show();
                   }
               })
       );
    }
    // TODO: Rename method, update argument and hook method into UI event
}
