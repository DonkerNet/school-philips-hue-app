package com.wdonker.huecontrolcenter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

// This activity allows the user to change the settings for a Philips Hue light
public class LightSettingsActivity extends AppCompatActivity implements HueTaskListener {

    private TextView titleTextView;
    private Switch onSwitch;
    private LinearLayout settingsLayout;
    private SeekBar brightnessSeekBar;
    private SeekBar hueSeekBar;
    private SeekBar saturationSeekBar;

    private int lightId;
    private Light light;

    // Sets up the controls in the activity and loads the light
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.titleTextView = (TextView)findViewById(R.id.titleTextView);
        this.onSwitch = (Switch)findViewById(R.id.onSwitch);
        this.settingsLayout = (LinearLayout)findViewById(R.id.settingsLayout);
        this.brightnessSeekBar = (SeekBar)findViewById(R.id.brightnessSeekBar);
        this.hueSeekBar = (SeekBar)findViewById(R.id.hueSeekBar);
        this.saturationSeekBar = (SeekBar)findViewById(R.id.saturationSeekBar);

        this.onSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLight();
                updateSettingsVisibility();
            }
        });

        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { saveLight();}
        };

        this.brightnessSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        this.hueSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        this.saturationSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        hideSettings(); // Hide by default

        this.lightId = getIntent().getIntExtra("lightId", -1);
        new GetLightHueTask(getString(R.string.HueApiBaseUrl), this).execute(this.lightId);
    }

    @Override
    public void onHueTaskComplete(Object sender, Object result) {
        if (sender instanceof GetLightHueTask){
            loadLight(result != null ? (Light)result : null);
        }
        else if (sender instanceof UpdateLightHueTask){

            // TODO: show message or whatever
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    // Loads a light into the activity
    private void loadLight(Light light){
        if (light == null) {
            this.titleTextView.setText(String.format("Light %d not found!", this.lightId));
            hideSettings();
        }
        else {
            this.light = light;

            this.titleTextView.setText(light.getName());
            this.onSwitch.setChecked(light.isEnabled());
            this.brightnessSeekBar.setProgress(light.getBrightness());

            Integer hue = light.getHue();
            if (hue != null){
                this.hueSeekBar.setEnabled(true);
                this.hueSeekBar.setProgress(hue);
            }
            else{
                this.hueSeekBar.setProgress(0);
                this.hueSeekBar.setEnabled(false);
            }

            Integer saturation = light.getSaturation();
            if (saturation != null){
                this.saturationSeekBar.setEnabled(true);
                this.saturationSeekBar.setProgress(saturation);
            }
            else{
                this.saturationSeekBar.setProgress(0);
                this.saturationSeekBar.setEnabled(false);
            }

            showSettings();
        }
    }

    // Updates the light and sends the update information to the Hue API
    private void saveLight(){
        this.light.setEnabled(this.onSwitch.isChecked());
        if(this.brightnessSeekBar.isEnabled())
            this.light.setBrightness(this.brightnessSeekBar.getProgress());
        if (this.hueSeekBar.isEnabled())
            this.light.setHue(this.hueSeekBar.getProgress());
        this.light.setSaturation(this.saturationSeekBar.getProgress());
        new UpdateLightHueTask(getString(R.string.HueApiBaseUrl), this).execute(this.light);
    }

    // Hides all settings in the activity
    private void hideSettings(){
        this.onSwitch.setVisibility(View.INVISIBLE);
        this.settingsLayout.setVisibility(View.INVISIBLE);
    }

    // Makes all settings visible in the activity
    private void showSettings(){
        this.onSwitch.setVisibility(View.VISIBLE);
        updateSettingsVisibility();
    }

    // Shows/hides the sliders based on whether the light is on or off (switch)
    private void updateSettingsVisibility(){
        if (this.onSwitch.isChecked())
            this.settingsLayout.setVisibility(View.VISIBLE);
        else
            this.settingsLayout.setVisibility(View.INVISIBLE);
    }
}
