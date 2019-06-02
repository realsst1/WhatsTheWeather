package com.example.whatstheweather.Common;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {

    public static final String API_ID="fb39dd1d18ab443a96b2731cbdd222ac";
    public static  Location currentLocation=null;

    public static String convertUnixToDate(long dt) {
        Date date=new Date(dt*1000L);
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm EEE MM yyyy");
        String formattedDate=sdf.format(date);
        return formattedDate;
    }

    public static String convertUnixToHour(long sunrise) {
        Date date=new Date(sunrise*1000L);
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
        String formattedDate=sdf.format(date);
        return formattedDate;
    }
}
