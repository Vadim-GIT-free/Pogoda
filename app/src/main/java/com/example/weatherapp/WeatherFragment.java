package com.example.weatherapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;


public class WeatherFragment extends Fragment {

    private final static String TAG = "WeatherFragment";

    Typeface weatherFont;
    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weatherIcon;
    TextView sensation_temperature;
    TextView mPressure;
    TextView mHumidity;
    TextView mSpeed;
    TextView mDirection;
    ImageView weather_icon_image;


    Handler handler;


    private OnRefreshListener mRefreshListener;
    private String mSelectedCity;


    public WeatherFragment() {
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        cityField = (TextView) rootView.findViewById(R.id.city_field);
        updatedField = (TextView) rootView.findViewById(R.id.updated_field);
        detailsField = (TextView) rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView) rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView) rootView.findViewById(R.id.weather_icon);
        mPressure = (TextView) rootView.findViewById(R.id.text_impression);
        mHumidity = (TextView) rootView.findViewById(R.id.text_humidity);
        mSpeed = (TextView) rootView.findViewById(R.id.text_speed);
        mDirection = (TextView) rootView.findViewById(R.id.text_direction);
        weather_icon_image = (ImageView) rootView.findViewById(R.id.imageIcon);
        sensation_temperature = (TextView) rootView.findViewById(R.id.sensation_temperature_field);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
       // weatherIcon.setTypeface(weatherFont);

        return rootView;
    }

    @Override
    public void onResume() {
        mSelectedCity = new CityPreference(getActivity()).getCity();
        updateWeatherData();
        super.onResume();
    }

    public void updateWeatherData() {
        if (mRefreshListener != null) {
            mRefreshListener.onRefreshStarted();
        }

        new Thread() {
            public void run() {
                final JSONObject json = RemoteFetch.getJSON(getActivity(), mSelectedCity);
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            renderWeather(json);

                            if (mRefreshListener != null) {
                                mRefreshListener.onRefreshComplete();
                            }
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json) {
        try {
            Log.i(TAG, "Rendering weather...");

            cityField.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Min:" + (String.format("%.1f", main.getDouble("temp_min")) +
                            "℃" + "-" + "Max:" +  main.getString("temp_max")) + "℃");

            mHumidity.setText(json.getJSONObject("main").getString("humidity") + "%");
            mPressure.setText(json.getJSONObject("main").getString("pressure") + "hPa");
            mSpeed.setText(json.getJSONObject("wind").getString("speed") + "м/с");

            currentTemperatureField.setText(String.format("%.1f", main.getDouble("temp")) + " ℃");

            sensation_temperature.setText(String.format("%.1f", main.getDouble("feels_like"))+ "℃");

            setDirectionWind(json.getJSONObject("wind").getInt("deg"));


            DateFormat df = DateFormat.getInstance();
            String updatedOn = df.format(new Date(json.getLong("dt") * 1000));
            updatedField.setText("Обновлено: " + updatedOn);

            setWeatherIcon(details.getInt("id") ,
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        } catch (Exception e) {
            Log.e(TAG, "One or more fields not found in the JSON data");
        }
    }

    private void setDirectionWind(int anInt) {
        String icon = "";
        if (anInt >= 0 && anInt <= 23) {
            icon = getActivity().getString(R.string.west);
        }else {
            if(anInt >= 24 && anInt <= 69){
                icon = getActivity().getString(R.string.north_west);
            }else {
            if (anInt >= 70 && anInt <= 113){
                icon = getActivity().getString(R.string.nord);
            }else {
                if(anInt >= 114 && anInt <= 159){
                    icon = getActivity().getString(R.string.north_ist);
                }else {
                    if (anInt >=160 && anInt <= 204){
                        icon = getActivity().getString(R.string.ist);
                    }else {
                        if(anInt >= 205 && anInt <= 249){
                            icon = getActivity().getString(R.string.south_ist);
                        }else {
                            if(anInt >= 250 && anInt <= 293){
                                icon = getActivity().getString(R.string.south);
                            }else {
                                if(anInt >= 294 && anInt <= 339){
                                    icon = getActivity().getString(R.string.south_west);
                                }else {
                                if(anInt >= 340 && anInt <=360){
                                    icon = getActivity().getString(R.string.west);
                                }}}
                    }}
            }}}}
        mDirection.setText(icon);

    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        if (actualId == 800) {
                weather_icon_image.setImageResource(R.drawable.sunny); // Солнечно
        } else {
            switch (id) {
                case 2:
                    weather_icon_image.setImageResource(R.drawable.thunder); // Гром
                    break;
                case 3:
                    weather_icon_image.setImageResource(R.drawable.drizzle); // Морось
                    break;
                case 7:
                    weather_icon_image.setImageResource(R.drawable.foggy);// Туман
                    break;
                case 8:
                    weather_icon_image.setImageResource(R.drawable.cloudy);// Пасмурно
                    break;
                case 6:
                    weather_icon_image.setImageResource(R.drawable.snowy_2); // Снег
                    break;
                case 5:
                    weather_icon_image.setImageResource(R.drawable.rainy);//Дождь
                    break;
            }
        }
        weather_icon_image.setImageResource(id);
    }

    public void changeCity(String city) {
        mSelectedCity = city;
        updateWeatherData();
    }

    public void setOnRefreshListenet(OnRefreshListener l) {
        mRefreshListener = l;
    }

    public interface OnRefreshListener {
        void onRefreshStarted();

        void onRefreshComplete();
    }
}
