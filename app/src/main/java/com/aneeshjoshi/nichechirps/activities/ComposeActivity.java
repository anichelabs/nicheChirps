package com.aneeshjoshi.nichechirps.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.aneeshjoshi.nichechirps.R;
import com.aneeshjoshi.nichechirps.application.TwitterApplication;
import com.aneeshjoshi.nichechirps.models.Tweet;
import com.aneeshjoshi.nichechirps.restclients.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class ComposeActivity extends ActionBarActivity {

    private static final String TAG = "ComposeActivity";
    public static final String TWEET = "tweet";
    private static final int RESULT_ERROR = -1;
    private TwitterClient client;
    EditText etTweet;

    private static final int MAX_TWEET_LENGTH = 140;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setLogo(R.drawable.ic_white_logo);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDisplayShowTitleEnabled(false);
        client = TwitterApplication.getRestClient();
        etTweet = (EditText) findViewById(R.id.etTweet);

        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > MAX_TWEET_LENGTH){
                    etTweet.setTextColor(Color.RED);
                } else {
                    etTweet.setTextColor(Color.BLACK);
                }
                int lengthRemaining = MAX_TWEET_LENGTH - s.length();
                //Gotcha when setting string value that happens to be int. There is an overloaded function that takes an id.
                mMenu.getItem(0).setTitle("" + lengthRemaining);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_tweet:
                tweet();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void tweet() {
        String tweet = etTweet.getText().toString();
        if(tweet.length() > MAX_TWEET_LENGTH) {
            Toast.makeText(this, "Tweet too long", Toast.LENGTH_SHORT);
            return;
        }
        client.tweet(tweet, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet t = Tweet.fromJson(response);
                Intent i = new Intent();
                i.putExtra(TWEET, response.toString());
                setResult(RESULT_OK, i);
                finish();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getApplicationContext(), "Could not post tweet", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "failed to post tweet", throwable);
            }
        });
    }
}
