package com.example.weather;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity {
    private TextView textCity,textCurrentTemp,textTime,textPressure,textHumidity,textMinTemp,textMaxTemp,textInternetState;
    private WeatherData data;
    private ImageView icon;
    private String iconURL;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        textInternetState=findViewById(R.id.textInternetState);
        textCity=findViewById(R.id.textCity);
        textCurrentTemp=findViewById(R.id.textCurrentTemp);
        textTime=findViewById(R.id.textTime);
        textCurrentTemp=findViewById(R.id.textCurrentTemp);
        textPressure=findViewById(R.id.textPressure);
        textHumidity=findViewById(R.id.textHumidity);
        textMinTemp=findViewById(R.id.textMinTemp);
        textMaxTemp=findViewById(R.id.textMaxTemp);
        icon=findViewById(R.id.imageViewIcon);
        mSwipeRefreshLayout = findViewById(R.id.swiperefresh_items);

        weatherData();
        mHandler=new Handler();
        mHandler.post(mUpdate);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {//odswiezanie manualne
            @Override
            public void onRefresh() {
                if(isNetworkAvailable()==true)
                {
                    weatherData();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(mSwipeRefreshLayout.isRefreshing()){
                                mSwipeRefreshLayout.setRefreshing(false);
                                textInternetState.setVisibility(4);
                            }
                        }
                    },1000);
                }
                else
                {
                    textInternetState.setVisibility(0);
                    if(mSwipeRefreshLayout.isRefreshing()){
                        mSwipeRefreshLayout.setRefreshing(false);
                    }


                }
            }
        });
    }

    private Runnable mUpdate = new Runnable() { //odswiezanie co 5 minut
        @Override
        public void run() {
            if(isNetworkAvailable()==true)
            {
                weatherData();
                mHandler.postDelayed(this,300000);
            }
            else {
                textInternetState.setVisibility(1);
            }

        }
    };
    public final void weatherData(){
        Intent intent = getIntent();
        data = (WeatherData) intent.getSerializableExtra("weather_class");
        textCity.setText(data.getName());
        textCurrentTemp.setText(data.getMain().getTemp()+" °C");
        textMinTemp.setText(data.getMain().getTempMin()+" °C");
        textMaxTemp.setText(data.getMain().getTempMax()+" °C");
        textHumidity.setText(data.getMain().getHumidity()+" %");
        textPressure.setText(data.getMain().getPressure()+" hPa");
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        textTime.setText(currentTime);
        iconURL="https://openweathermap.org/img/wn/"+data.getWeather().get(0).getIcon()+"@2x.png";
        Picasso.with(this).load(iconURL).into(icon);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
