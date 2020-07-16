package com.example.weatherapp;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, WeatherFragment.OnRefreshListener {

    private WeatherFragment mWeatherFragment ;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wather_activity);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorBlue, R.color.colorYellow,
                R.color.colorGreen, R.color.colorRed);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.conteiner2, new WeatherFragment())
                    .commit();
        }
    }
    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        if (fragment instanceof WeatherFragment) {
            mWeatherFragment = (WeatherFragment) fragment;
            mWeatherFragment.setOnRefreshListenet(this);
        }
        super.onAttachFragment(fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.change_city) {
            showInputDialog();
        }
        if (item.getItemId() == R.id.next_window) {
            Intent intent = new Intent(this, RefreshActivity.class);
            startActivity(intent);
        }
        return false;
    }
    @Override
    public void onRefresh() {
        mWeatherFragment.updateWeatherData();
    }

    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Сменить город");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeCity(input.getText().toString());
            }
        });
        builder.show();
    }

    public void changeCity(String city) {
        WeatherFragment wf = (WeatherFragment) getSupportFragmentManager()
                .findFragmentById(R.id.conteiner2);
        wf.changeCity(city);
        new CityPreference(this).setCity(city);
    }
    @Override
    public void onRefreshStarted() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onRefreshComplete() {
        Toast.makeText(MainActivity.this, R.string.refresh_stop, Toast.LENGTH_SHORT).show();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}


