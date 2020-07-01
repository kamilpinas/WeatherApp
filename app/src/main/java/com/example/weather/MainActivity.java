package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    Button buttonCheck;
    EditText cityCheck;
    TextView textError,textInternetState;
    WeatherData data;
    Intent intent;
    private final String apiid = "749561a315b14523a8f5f1ef95e45864";
    private final String units= "metric";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textInternetState=findViewById(R.id.textInternetState);

        buttonCheck=findViewById(R.id.buttonCheck);
        cityCheck=findViewById(R.id.cityCheck);
        textError=findViewById(R.id.textError);
        loadData();
        if (isNetworkAvailable() == true) {
        buttonCheck.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                    textError.setText("");
                    intent = new Intent(v.getContext(), WeatherActivity.class);
                    intent.putExtra("city_name", cityCheck.getText());
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://api.openweathermap.org/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
                    Call<WeatherData> call = jsonPlaceHolder.getWeatherData(cityCheck.getText().toString().replaceAll(" ", "") + ",pl", apiid, units);
                    call.enqueue(new Callback<WeatherData>() {
                        @Override
                        public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                            if (!response.isSuccessful()) {
                                System.out.println("Code: " + response.code());
                                textError.setText("Code: " + response.code());
                                return;
                            }
                            data = response.body();
                            intent.putExtra("weather_class", data);
                            startActivity(intent);
                            saveData(cityCheck.getText().toString());

                        }

                        @Override
                        public void onFailure(Call<WeatherData> call, Throwable throwable) {
                            System.out.println(throwable.getMessage());
                            textError.setText(throwable.getMessage());
                        }
                    });
                }
            });
        }
        else{
        textInternetState.setVisibility(1);
        buttonCheck.setEnabled(false);
    }
    }
    private void saveData(String name){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("city_key",name);
        editor.apply();
    }

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences",MODE_PRIVATE);
        cityCheck.setText(sharedPreferences.getString("city_key","x"));
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
