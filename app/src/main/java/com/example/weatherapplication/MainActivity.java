package com.example.weatherapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private TextView cityNameText,temperatureText,humidityText,descriptionText,windtext;
    private Button refreshbutton;
    private ImageView weathericon;
    private EditText cityname;

    private static final String API_KEY = "6cb8d64f2e9ed03d95700d9e23e0ba2f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

       cityNameText = findViewById(R.id.cityNameText);
       temperatureText = findViewById(R.id.temperatureText);
       humidityText = findViewById(R.id.humiditytext);
       windtext = findViewById(R.id.windtext);
       descriptionText = findViewById(R.id.description);
       weathericon = findViewById(R.id.weathericon);
       refreshbutton = findViewById(R.id.weatherbutton);
       cityname = findViewById(R.id.cityname);

       refreshbutton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String cityName = cityname.getText().toString();
               if(!cityName.isEmpty()){
                   FetchWeatherData(cityName);
               } else {
                   cityname.setError("Please Enter a city name");
               }
           }
       });
       FetchWeatherData("Mumbai");
    }
    private  void FetchWeatherData(String cityName) {
        String url = "https:api.openweathermap.org/data/2.5/weather?q="+cityName + "&appid="+API_KEY + "&units=metric";
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() ->
        {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            try{
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                runOnUiThread(() -> updateUI(result));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        );


    }

    private void updateUI(String result) {
        if(result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject main = jsonObject.getJSONObject("main");
                double temperature = main.getDouble("temp");
                double humidity = main.getDouble("humidity");
                double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");

                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String iconCode = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                String resourceName = "ic_" + iconCode;
                int resId = getResources().getIdentifier(resourceName, "drawable",getPackageName());
                weathericon.setImageResource(resId);

                cityNameText.setText(jsonObject.getString("name"));
                temperatureText.setText(String.format("%.0f°C", temperature));
                humidityText.setText(String.format("%.0f%%", humidity));
                windtext.setText(String.format("%.0f km/h", windSpeed));
                descriptionText.setText(description);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}