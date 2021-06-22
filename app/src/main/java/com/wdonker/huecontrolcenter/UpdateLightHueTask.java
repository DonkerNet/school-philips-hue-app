package com.wdonker.huecontrolcenter;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Task for updating a single light in the Philips Hue REST API
public class UpdateLightHueTask extends AbstractHueTask<Light, Void, Boolean> {

    public UpdateLightHueTask(String apiBaseUrl, HueTaskListener listener) {
        super(apiBaseUrl, listener);
    }

    @Override
    protected Boolean doInBackground(Light... lights) {
        if (lights == null || lights.length == 0)
            return true; // Nothing to update

        Light light = lights[0];
        if (light == null)
            return false;

        String relativeUrl = String.format("lights/%d/state", light.getId());
        String httpMethod = HttpMethod.PUT;

        String json;

        if (light.isEnabled()){
            // Workaround: enable the light before updating in case it was disabled to prevent the API from returning errors
            String enableResult = executeApiCall(relativeUrl, httpMethod, "{\"on\":true}");
            if (validateResult(enableResult) == false){
                return false;
            }
            json = String.format("{\"bri\":%d,\"sat\":%d,\"hue\":%d}", light.getBrightness(), light.getSaturation(), light.getHue());
        }
        else{
            json = "{\"on\":false}";
        }

        String result = executeApiCall(relativeUrl, httpMethod, json); //
        return validateResult(result);
    }

    // Checks if any errors were returned by the API
    private boolean validateResult(String json){
        try {
            JSONArray successObjects = new JSONArray(json);

            if (successObjects != null){
                for (int i = 0; i < successObjects.length(); i++){
                    JSONObject successObject = successObjects.getJSONObject(i);
                    if (!successObject.has("success"))
                        return false;
                }
            }

        } catch (JSONException e) {
            Log.e(getLogTag(), e.getLocalizedMessage());
        }

        return true;
    }
}
