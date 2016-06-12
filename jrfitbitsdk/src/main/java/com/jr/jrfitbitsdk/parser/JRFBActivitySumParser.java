package com.jr.jrfitbitsdk.parser;

import android.util.Log;

import com.jr.jrfitbitsdk.model.JRFBActivitySummary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jithin Roy on 3/10/16.
 */
public class JRFBActivitySumParser {

    public static JRFBActivitySummary parseActivity(String activityResult) {

        try {
            JSONObject reader = new JSONObject(activityResult);
            JSONObject summaryJson = reader.getJSONObject("summary");
            int fairlyActiveMinutes = summaryJson.getInt("fairlyActiveMinutes");
            int lightlyActiveMinutes = summaryJson.getInt("lightlyActiveMinutes");
            int sedentaryMinutes = summaryJson.getInt("sedentaryMinutes");
            int veryActiveMinutes = summaryJson.getInt("veryActiveMinutes");

            JRFBActivitySummary summary = new JRFBActivitySummary();
            summary.setLightlyActiveMinutes(lightlyActiveMinutes);
            summary.setFairlyActiveMinutes(fairlyActiveMinutes);
            summary.setSedentaryMinutes(sedentaryMinutes);
            summary.setVeryActiveMinutes(veryActiveMinutes);

            return  summary;
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;

    }
}
