package com.wdonker.huecontrolcenter;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

// Task for retrieving a single light from the Philips Hue REST API
public class GetLightHueTask extends AbstractHueTask<Integer, Void, Light> {
    public GetLightHueTask(String apiBaseUrl, HueTaskListener listener) {
        super(apiBaseUrl, listener);
    }

    @Override
    protected Light doInBackground(Integer... params) {
        if (params == null || params.length == 0)
            return null;

        int id = params[0];

        String result = executeApiCall("lights/" + id, HttpMethod.GET, null);
        return parseJson(id, result);
    }

    // Parses the JSON result into a Light object
    private Light parseJson(int id, String json){
        Light light = null;

        try {
            JSONObject jsonLight = new JSONObject(json);

            if (jsonLight != null){
                JSONObject jsonState = jsonLight.getJSONObject("state");

                String name = jsonLight.getString("name");
                boolean isEnabled = jsonState.getBoolean("on");
                Integer hue = jsonState.isNull("hue") ? null : jsonState.getInt("hue");
                Integer saturation = jsonState.isNull("sat") ? null : jsonState.getInt("sat");
                int brightness = jsonState.getInt("bri");

                light = new Light(id, name, isEnabled, hue, saturation, brightness);
            }

        } catch (JSONException e) {
            Log.e(getLogTag(), e.getLocalizedMessage());
        }

        return light;
    }
}
