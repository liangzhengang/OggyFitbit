package com.jr.jrfitbitsdk;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * JRFBBaseAPI class that handles the api call.
 * Created by Jithin Roy on 2/15/16.
 */
public abstract class JRFBBaseAPI {

    private String BASE_URL = "https://api.fitbit.com/1/";
    private APITask task = null;


    protected void fetchRequest(final String apiName, final Object params) {

        try {
            String urlString = BASE_URL + apiName;

            URL url = new URL(urlString);
            Log.i("JRFBBaseAPI", url.toString());
            new APITask().execute(url);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected abstract void didCompleteRequest(String result);
    protected abstract void didFailRequest();

    private class APITask extends AsyncTask<URL, Integer, Object> {

        protected Object doInBackground(URL... urls) {
            int count = urls.length;
            String result = initiateAPI(urls[0], null);

            return result;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute (Object result) {
            didCompleteRequest((String)result);
        }

        private String initiateAPI(URL url, Object params) {

            BufferedReader reader = null;
            StringBuilder stringBuilder;

            try {
                // create the HttpURLConnection

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                if (params == null) {
                    connection.setRequestMethod("GET");
                } else {
                    connection.setRequestMethod("POST");
                }

                String authHeader = "Bearer " + JRFitbitSDK.getInstance().authToken();
                connection.setRequestProperty("Authorization", authHeader);


                Log.i("JRFBBaseAPI", " " + authHeader);
                connection.setReadTimeout(15 * 1000);
                connection.connect();

                int status = connection.getResponseCode();

                Log.i("JRFBBaseAPI", " " +status);

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                stringBuilder = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                Log.i("JRFBBaseAPI", " " +stringBuilder.toString());
                return stringBuilder.toString();

            } catch (Exception e) {
                e.printStackTrace();
                //throw e;
            } finally {

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
            return null;
        }
    }

}
