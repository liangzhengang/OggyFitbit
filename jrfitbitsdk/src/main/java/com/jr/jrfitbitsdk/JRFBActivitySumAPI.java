package com.jr.jrfitbitsdk;

import com.jr.jrfitbitsdk.model.JRFBActivitySummary;
import com.jr.jrfitbitsdk.parser.JRFBActivitySumParser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jithin Roy on 2/15/16.
 */
public class JRFBActivitySumAPI extends JRFBBaseAPI {

    private JRFBActivitySummaryInterface delegate;
    private String activityDate;

    public JRFBActivitySumAPI(JRFBActivitySummaryInterface delegate) {
        super();
        this.delegate = delegate;
    }

    public void fetchTodaysActivitySummary() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        activityDate = formatter.format(new Date());
        String url = "user/-/activities/date/" + activityDate + ".json";
        fetchRequest(url,null);
    }

    public void fetchYesterdaysActivitySummary() {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        System.out.println("Yesterday's date = "+ cal.getTime());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        activityDate = formatter.format(cal.getTime());
        String url = "user/-/activities/date/" + activityDate + ".json";
        fetchRequest(url,null);
    }

    protected void didCompleteRequest(String result) {
        if (this.delegate != null) {
            JRFBActivitySummary summary =  JRFBActivitySumParser.parseActivity(result);
            if (summary != null) {
                summary.setActivityDate(activityDate);
                this.delegate.didFetchActivity(this,summary);
            } else {
                didFailRequest();
            }


        }

    }

    protected void didFailRequest() {
        if (this.delegate != null) {
            this.delegate.didFailFetchingActivity(this);
        }
    }
}


