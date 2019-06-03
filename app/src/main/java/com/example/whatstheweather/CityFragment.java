package com.example.whatstheweather;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatstheweather.Common.Common;
import com.example.whatstheweather.Models.WeatherResult;
import com.example.whatstheweather.Retrofit.IOpenWeatherMap;
import com.example.whatstheweather.Retrofit.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.label305.asynctask.SimpleAsyncTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class CityFragment extends Fragment {

    private List<String> cityList;
    private MaterialSearchBar searchBar;

    private ImageView imageView;
    private TextView wind,cityName,cityDesc,humidity,sunset,sunrise,pressure,temp,dateTime,geoCoord;
    private LinearLayout weatherPanel;
    private ProgressBar loadingCurrentWeather;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    static CityFragment instance;

    public static CityFragment getInstance() {
        if(instance==null)
            instance=new CityFragment();
        return instance;
    }

    public CityFragment() {
        // Required empty public constructor
        compositeDisposable=new CompositeDisposable();
        Retrofit retrofit= RetrofitClient.getInstance();
        mService=retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_city, container, false);

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

        searchBar=(MaterialSearchBar)view.findViewById(R.id.searchBar);
        searchBar.setEnabled(false);


        new LoadCities().execute();



        return view;
    }

    private class LoadCities extends SimpleAsyncTask<List<String>> {

        @Override
        protected List<String> doInBackgroundSimple() {
            cityList=new ArrayList<>();
            try{
                StringBuilder builder=new StringBuilder();
                InputStream inputStream=getResources().openRawResource(R.raw.city_list);
                GZIPInputStream gzipInputStream=new GZIPInputStream(inputStream);

                InputStreamReader streamReader=new InputStreamReader(gzipInputStream);
                BufferedReader bufferedReader=new BufferedReader(streamReader);
                String read;
                while ((read=bufferedReader.readLine())!=null){
                    builder.append(read);
                    cityList=new Gson().fromJson(builder.toString(), new TypeToken<List<String>>(){}.getType());

                }
            }
            catch (Exception e){

            }
            return cityList;
        }

        @Override
        protected void onSuccess(final List<String> cityList) {
            super.onSuccess(cityList);
            searchBar.setEnabled(true);
            searchBar.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    List<String> suggest=new ArrayList<>();
                    for(String search:cityList){
                        if(search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                            suggest.add(search);
                    }
                    searchBar.setLastSuggestions(suggest);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {

                }

                @Override
                public void onSearchConfirmed(CharSequence text) {
                    getWeatherInformation(text.toString());
                    searchBar.setLastSuggestions(cityList);
                }

                @Override
                public void onButtonClicked(int buttonCode) {

                }
            });
            searchBar.setLastSuggestions(cityList);
            loadingCurrentWeather.setVisibility(View.GONE);
            //weatherPanel.setVisibility(View.VISIBLE);
        }
    }

    private void getWeatherInformation(String lstCity) {

        compositeDisposable.add(mService.getWeatherByCity(lstCity,
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
