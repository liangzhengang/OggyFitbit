package com.jr.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jr.jrfitbitsdk.JRFBActivitySumAPI;
import com.jr.jrfitbitsdk.JRFBActivitySummaryInterface;
import com.jr.jrfitbitsdk.JRFBBaseAPI;
import com.jr.jrfitbitsdk.JRFitbitSDK;
import com.jr.jrfitbitsdk.model.JRFBActivitySummary;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity implements JRFBActivitySummaryInterface {

    public String  CLIENT_ID = "229X56";

    @Override
    public void didFetchActivity(JRFBBaseAPI api,JRFBActivitySummary result) {
        Log.i("ActivityHome", "Activ = " + result.getFairlyActiveMinutes() + " " + result.getLightlyActiveMinutes() );
    }

    @Override
    public void didFailFetchingActivity(JRFBBaseAPI api) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> list = new ArrayList<String>();
                list.add("activity");
                JRFitbitSDK.getInstance().authorize(HomeActivity.this,list);
            }
        });

        JRFitbitSDK.getInstance().initialize(CLIENT_ID, "fbg://callback/", this.getApplicationContext());

        Log.i("Test", "HEREREEE");

        if (JRFitbitSDK.getInstance().isAuthorized()) {
            Log.i("Test", "Authed");
//            Toast.makeText(this,"authed",5).show();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i("Test", "HEREREEE2");
        JRFitbitSDK.getInstance().onRecieveIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.i("Test", "Settings");

            JRFBActivitySumAPI summary = new JRFBActivitySumAPI(this);
            summary.fetchYesterdaysActivitySummary();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
