package com.jr.jrfitbitsdk;

import com.jr.jrfitbitsdk.model.JRFBActivitySummary;

/**
 * Created by Jithin Roy on 2/24/16.
 */
public interface JRFBActivitySummaryInterface {

     void didFetchActivity(JRFBBaseAPI api, JRFBActivitySummary result);

    void didFailFetchingActivity(JRFBBaseAPI api);
}
