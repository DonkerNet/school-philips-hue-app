package com.wdonker.huecontrolcenter;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

// Task for retrieving a list of IDs and names (key+value) for all lights from the Philips Hue REST API
public class GetLightListHueTask extends AbstractHueTask<Void, Void, LinkedHashMap<Integer, String>> {

    public GetLightListHueTask(String apiBaseUrl, HueTaskListener listener) {
        super(apiBaseUrl, listener);
    }

    @Override
    protected LinkedHashMap<Integer, String> doInBackground(Void... params) {
        String result = executeApiCall("lights", HttpMethod.GET, null);
        return parseJson(result);
    }

    // Converts the retrieved light data into a LinkedHashMap where the key is the light ID and the value is the light name
    private LinkedHashMap<Integer, String> parseJson(String json){
        LinkedHashMap<Integer, String> lights = new LinkedHashMap<>();

        if (json != null) {
            try {
                JSONObject jsonObject = new JSONObject(json);

                JSONArray jsonIds = jsonObject.names();

                if (jsonIds != null){
                    for (int i = 0; i < jsonIds.length(); i++){
                        String jsonId = jsonIds.getString(i);
                        JSONObject jsonLight = jsonObject.getJSONObject(jsonId);

                        int id = Integer.parseInt(jsonId);
                        String name = jsonLight.getString("name");

                        lights.put(id, name);
                    }
                }

            } catch (JSONException e) {
                Log.e(getLogTag(), e.getLocalizedMessage());
            }
        }

        return lights;
    }
}
