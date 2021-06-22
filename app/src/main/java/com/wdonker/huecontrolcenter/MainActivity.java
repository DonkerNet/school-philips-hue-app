package com.wdonker.huecontrolcenter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.LinkedHashMap;

// Main entry point for the app
public class MainActivity extends AppCompatActivity implements HueTaskListener {

    private ListView lightsListView;

    private Integer[] lightIds;

    // Sets up the controls in the activity and loads a list of available Hue lights
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lightsListView = (ListView)findViewById(R.id.lightsListView);
        lightsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent settingsIntent = new Intent(MainActivity.this, LightSettingsActivity.class);
                settingsIntent.putExtra("lightId", lightIds[position]);
                startActivity(settingsIntent);
            }
        });

        new GetLightListHueTask(getString(R.string.HueApiBaseUrl), this).execute();
    }

    @Override
    public void onHueTaskComplete(Object sender, Object result) {
        if (sender instanceof GetLightListHueTask){
            setLights(result != null ? (LinkedHashMap<Integer, String>)result : null);
        }
    }

    // Updates the list view with new light data
    private void setLights(LinkedHashMap<Integer, String> lights){
        if (lights == null){
            lights = new LinkedHashMap<>();
        }

        Integer[] lightIds = new Integer[lights.size()];
        lights.keySet().toArray(lightIds);
        this.lightIds = lightIds;

        ArrayAdapter listViewAdapter = new ArrayAdapter(this, R.layout.rowtextview, lights.values().toArray());
        lightsListView.setAdapter(listViewAdapter);

        TextView titleTextView = (TextView)findViewById(R.id.titleTextView);
        if (lights.size() == 0){
            titleTextView.setText("No lights available.");
        }
        else{
            titleTextView.setText("Available lights:");
        }
    }
}
