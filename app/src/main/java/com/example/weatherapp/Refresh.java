package com.example.weatherapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;



public class Refresh extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
         mSwipeRefreshLayout.setOnRefreshListener(this);
         mSwipeRefreshLayout.setColorSchemeResources(R.color.colorBlue, R.color.colorYellow,
           R.color.colorGreen, R.color.colorRed);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_two, new WeatherFragment())
                    .commit();
        }

    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.weather, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.change_city) {
            showInputDialog();
        }
        if (item.getItemId() == R.id.variant_activity) {
            Intent intent = new Intent(this, Fragment2.class);
            startActivity(intent);
        }
        return false;
    }

        @Override
        public void onRefresh(){
            mSwipeRefreshLayout.setRefreshing(true);
            Toast.makeText(this, R.string.refresh_started, Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(Refresh.this, R.string.refresh_stop, Toast.LENGTH_SHORT).show();


                }
            }, 2000);

        }
        private void showInputDialog(){
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

    private void changeCity(String city) {
        WeatherFragment wf = (WeatherFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frame_two);
        wf.changeCity(city);
        new CityPreference(this).setCity(city);
    }
    }
