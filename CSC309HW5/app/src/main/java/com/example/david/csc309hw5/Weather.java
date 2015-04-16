package com.example.david.csc309hw5;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;


public class Weather extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        try {
            RetrieveWeather();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class WeatherServiceAsync extends AsyncTask<String, Void, String> {
        private final Weather Weather;

        public WeatherServiceAsync(Weather weather) {
            this.Weather = weather;
        }

        @Override
        protected String doInBackground(String... urls){
            String response = "";

            for(String url:urls){
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                try {
                    HttpResponse execute = client.execute(httpGet);

                    InputStream content = execute.getEntity().getContent();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s;

                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result){
            String test = result;
            try {
                JSONObject jsonResult = new JSONObject(test);

                double temperature = jsonResult.getJSONObject("main").getDouble("temp");
                temperature = ConvertTemperatureToFahrenheit(temperature);
                double humidity = jsonResult.getJSONObject("main").getDouble("humidity");
                double pressure = jsonResult.getJSONObject("main").getDouble("pressure");

                String description = jsonResult.getJSONArray("weather").getJSONObject(0).getString("description");

                this.Weather.SetDescription(description);
                this.Weather.SetTemperature(temperature);
                this.Weather.SetPressure(pressure);
                this.Weather.SetHumidity(humidity);

            }catch(JSONException e) {
                e.printStackTrace();
            }
        }

        private double ConvertTemperatureToFahrenheit(double temperature) {
            return (temperature - 273) * (9 / 5) + 32;
        }
    }

    public void RetrieveWeather() throws IOException {
        String url = "http://people.eku.edu/styere/fake_weather.php?id=4305974";

        WeatherServiceAsync task = new WeatherServiceAsync(this);
        task.execute(url);
    }

    public void SetTemperature(double temperature){
        TextView view = (TextView) this.findViewById(R.id.textTemperatureValue);

        DecimalFormat df = new DecimalFormat("###.##");
        String formattedTemperature = df.format(temperature);

        view.setText(formattedTemperature + " F");
    }

    public void SetPressure(double pressure){
        TextView view = (TextView) this.findViewById(R.id.textPressureValue);

        DecimalFormat df = new DecimalFormat("####.#");
        String formattedPressure = df.format(pressure);

        view.setText(formattedPressure + " mb");
    }

    public void SetHumidity(double humidity){
        TextView view = (TextView) this.findViewById(R.id.textHumidityValue);

        DecimalFormat df = new DecimalFormat("##.#");
        String formattedHumidity = df.format(humidity);

        view.setText(formattedHumidity + "%");
    }

    public void SetDescription(String description){
        TextView view = (TextView) this.findViewById(R.id.textDescriptionValue);

        view.setText(description);
    }
}

