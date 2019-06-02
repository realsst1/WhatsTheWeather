package com.example.whatstheweather;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.whatstheweather.Retrofit.IOpenWeatherMap;
import com.example.whatstheweather.Retrofit.RetrofitClient;

import io.reactivex.disposables.CompositeDisposable;
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
        return view;
    }

}
