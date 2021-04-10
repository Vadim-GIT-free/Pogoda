package com.example.weatherapp;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenWeatherDay {
    private static final String OPEN_WEATHER_MAP_API ="https://api.openweathermap.org/data/2.5/forecast?q=";
    private static final String API_METRIC ="&units=metric&lang=ru&appid=";

    public static JSONObject getJSON(Context context, String city) {
        try {
            String urlString = OPEN_WEATHER_MAP_API + city + API_METRIC + context.getString(R.string.open_weather_maps_app_id1);
            URL urlObject = new URL(urlString);
            HttpURLConnection connection =
                    (HttpURLConnection) urlObject.openConnection();

            connection.addRequestProperty("YOUR_API_KEY",
                    context.getString(R.string.open_weather_maps_app_id1));

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();
            JSONObject data = new JSONObject(json.toString());
            Log.e("Wather", "Day"+ data);

            // This value will be 404 if the request was not
            // successful
            if (data.getInt("cod") != 200) {
                return null;
            }
            return data;
        } catch (Exception e) {
            return null;
        }
    }
}
