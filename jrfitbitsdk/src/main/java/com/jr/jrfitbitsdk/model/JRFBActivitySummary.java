package com.jr.jrfitbitsdk.model;

/**
 * Created by Jithin Roy on 3/10/16.
 */
public class JRFBActivitySummary {

    private String activityDate;
    private int lightlyActiveMinutes;
    private int sedentaryMinutes;
    private int veryActiveMinutes;
    private int fairlyActiveMinutes;

    public void setFairlyActiveMinutes(int fairlyActiveMinutes) {
        this.fairlyActiveMinutes = fairlyActiveMinutes;
    }

    public int getFairlyActiveMinutes() {

        return fairlyActiveMinutes;
    }

    public void setLightlyActiveMinutes(int lightlyActiveMinutes) {
        this.lightlyActiveMinutes = lightlyActiveMinutes;
    }

    public void setSedentaryMinutes(int sedentaryMinutes) {
        this.sedentaryMinutes = sedentaryMinutes;
    }

    public void setVeryActiveMinutes(int veryActiveMinutes) {
        this.veryActiveMinutes = veryActiveMinutes;
    }

    public int getLightlyActiveMinutes() {

        return lightlyActiveMinutes;
    }

    public int getSedentaryMinutes() {
        return sedentaryMinutes;
    }

    public int getVeryActiveMinutes() {
        return veryActiveMinutes;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }
}
