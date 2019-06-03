package com.example.whatstheweather.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.whatstheweather.Common.Common;
import com.example.whatstheweather.Models.WeatherForcastResult;
import com.example.whatstheweather.R;
import com.squareup.picasso.Picasso;

public class WeatherForcastAdapter extends RecyclerView.Adapter<WeatherForcastAdapter.ViewHolder> {

    Context context;
    WeatherForcastResult forcastResult;

    public WeatherForcastAdapter(Context context, WeatherForcastResult forcastResult) {
        this.context = context;
        this.forcastResult = forcastResult;
    }

    @NonNull
    @Override
    public WeatherForcastAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_weather_forcast_single,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherForcastAdapter.ViewHolder viewHolder, int i) {

        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                .append(forcastResult.getList().get(i).getWeather().get(0).getIcon())
                .append(".png").toString()).into(viewHolder.imageView);

        viewHolder.dateTime.setText(new StringBuilder(Common.convertUnixToDate(forcastResult.getList().get(i).getDt())));
        viewHolder.desc.setText(new StringBuilder(forcastResult.getList().get(i).getWeather().get(0).description));
        viewHolder.temp.setText(new StringBuilder(String.valueOf(Math.round(forcastResult.getList().get(i).getMain().getTemp()-273.15))).append("°C"));


    }

    @Override
    public int getItemCount() {
        return forcastResult.getList().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;
         ImageView imageView;
         TextView dateTime,desc,temp;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            imageView=(ImageView) view.findViewById(R.id.weatherImage);
            dateTime=(TextView)view.findViewById(R.id.forcastDate);
            desc=(TextView)view.findViewById(R.id.forcastDesc);
            temp=(TextView)view.findViewById(R.id.temperature);
        }
    }
}
