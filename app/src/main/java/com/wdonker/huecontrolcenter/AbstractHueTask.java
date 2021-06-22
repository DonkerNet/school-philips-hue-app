package com.wdonker.huecontrolcenter;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

// Base class for tasks that call the Philips Hue REST API
public abstract class AbstractHueTask<TParams,TProgress,TResult> extends AsyncTask<TParams,TProgress,TResult> {

    private String baseUrl;
    private String logTag;
    private HueTaskListener listener;

    protected AbstractHueTask(String apiBaseUrl, HueTaskListener listener){
        this.baseUrl = apiBaseUrl;
        this.listener = listener;
        this.logTag = this.getClass().getSimpleName();
    }

    protected String getLogTag(){
        return this.logTag;
    }

    // Can be used by tasks that implement this abstract class, so that they can make calls to the Hue API
    protected String executeApiCall(String relativeUrl, String httpMethod, String body){
        try {
            URL url = new URL(this.baseUrl + "/" + relativeUrl);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(5000);

            if (conn instanceof HttpURLConnection){
                HttpURLConnection httpConn = (HttpURLConnection)conn;
                httpConn.setAllowUserInteraction(false);
                httpConn.setInstanceFollowRedirects(true);
                httpConn.setRequestMethod(httpMethod);

                if (body != null){
                    httpConn.addRequestProperty("Content-Type", "application/json");
                    httpConn.setRequestProperty("Content-Length", Integer.toString(body.length()));
                    httpConn.getOutputStream().write(body.getBytes("UTF8"));
                }

                httpConn.connect();

                int responseCode = httpConn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK){
                    InputStream inputStream = httpConn.getInputStream();
                    String response = readStringFromStream(inputStream);
                    return response;
                }
            }

        } catch (IOException e) {
            Log.e(this.logTag, e.getLocalizedMessage());
        }

        return null;
    }

    // Makes a call to the listener that the task has completed
    protected void onPostExecute(TResult result){
        if (listener != null)
            listener.onHueTaskComplete(this, result);
    }

    // Reads a String from the InputStream from the HTTP response
    private String readStringFromStream(InputStream stream){
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder bob = new StringBuilder(); // Bob the StringBuilder

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                bob.append(line);
            }
        } catch (IOException e) {
            Log.e(this.logTag, e.getLocalizedMessage());
        }
        finally {
            try {
                reader.close();
            } catch (IOException e) {
                Log.e(this.logTag, e.getLocalizedMessage());
            }
        }

        return bob.toString();
    }
}
