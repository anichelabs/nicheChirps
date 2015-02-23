package com.aneeshjoshi.nichechirps.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.aneeshjoshi.nichechirps.R;
import com.aneeshjoshi.nichechirps.adapters.TweetsArrayAdapter;
import com.aneeshjoshi.nichechirps.application.TwitterApplication;
import com.aneeshjoshi.nichechirps.listeners.EndlessScrollListener;
import com.aneeshjoshi.nichechirps.models.Tweet;
import com.aneeshjoshi.nichechirps.restclients.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME;
import static android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE;


public class HomeTimelineActivity extends ActionBarActivity {

    public static final String TAG = "HomeTimelineActivity";
    private static final int RC_COMPOSE = 0;
    private TwitterClient client;

    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;

    private SwipeRefreshLayout swipeContainer;
    private String screenName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_timeline);
        lvTweets = (ListView) findViewById(R.id.lvHomeTimeline);
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(this, tweets);
        lvTweets.setAdapter(aTweets);
        client = TwitterApplication.getRestClient(); //singleton client


        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setHomeButtonEnabled(true);
        supportActionBar.setDisplayOptions(DISPLAY_SHOW_HOME | DISPLAY_SHOW_TITLE);
        supportActionBar.setIcon(R.drawable.ic_white_logo);
        supportActionBar.setTitle(screenName);
        populateTimeline(1, false);

        client.getUsername(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    screenName = response.getString("screen_name");
                    getSupportActionBar().setTitle(screenName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        // Attach the listener to the AdapterView onCreate
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever cde is needed to append new items to your AdapterView
                customLoadMoreDataFromApi(page);
                // or customLoadMoreDataFromApi(totalItemsCount);
            }
        });

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline(1, true);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    // Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset) {

        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
       populateTimeline(offset, false);

    }

    //Send an API request to get the timeline json
    //Fill the list view by creating the tweet object.
    private void populateTimeline(int offset, final boolean isRefresh) {
        client.getHomeTimeline(25, offset, new JsonHttpResponseHandler(){
            //Success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d(TAG, json.toString());

                if(isRefresh){
                    aTweets.clear();
                }
                aTweets.addAll(Tweet.fromJsonArray(json));
                if(isRefresh){
                  swipeContainer.setRefreshing(false);
                }

            }

            //Failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, errorResponse.toString());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_timeline, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_compose){
            Intent i = new Intent(this, ComposeActivity.class);
            startActivityForResult(i, RC_COMPOSE);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_COMPOSE){
            Tweet t;
            try {
                JSONObject response = new JSONObject(data.getStringExtra(ComposeActivity.TWEET));
                t = Tweet.fromJson(response);
                tweets.add(0, t);
                aTweets.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                populateTimeline(1, true);
            }
        }
    }
}
