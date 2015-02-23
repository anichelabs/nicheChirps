package com.aneeshjoshi.nichechirps.models;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * This is a temporary, sample model that demonstrates the basic structure
 * of a SQLite persisted Model object. Check out the ActiveAndroid wiki for more details:
 * https://github.com/pardom/ActiveAndroid/wiki/Creating-your-database-model
 * 
 */
@Table(name = "tweets")
public class Tweet extends Model implements Serializable{
    private static final String TAG = "Tweet";

    // Define table fields
	@Column(name = "body")
	private String body;

    @Column(name = "createdAt")
    private String createdAt;

    @Column(name = "tweetId")
    private long tweetId;

    @Column(name = "user")
    private User user;

	public Tweet() {
		super();
	}

	// Parse model from JSON
	public static Tweet fromJson(JSONObject object){

        Tweet tweet = new Tweet();
		try {
			tweet.body = object.getString("text");
            tweet.tweetId = object.getLong("id");
            tweet.createdAt = object.getString("created_at");
            //tweet.user
            tweet.user = User.fromJson(object.getJSONObject("user"));

		} catch (JSONException e) {
			e.printStackTrace();
		}
        return tweet;
	}


	// Record Finders
	public static Tweet byTweetId(long tweetId) {
		return new Select().from(Tweet.class).where("tweetId = ?", tweetId).executeSingle();
	}

	public static List<Tweet> recentItems() {
		return new Select().from(Tweet.class).orderBy("id DESC").limit("300").execute();
	}

    public static ArrayList<Tweet> fromJsonArray(JSONArray json) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < json.length(); i++){
            try {
                Tweet t = Tweet.fromJson(json.getJSONObject(i));
                tweets.add(t);
            } catch (JSONException e) {
                Log.d(TAG, "Error retrieveing tweet at " + i, e);
            }
        }
        return tweets;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "body='" + body + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", tweetId=" + tweetId +
                ", user=" + user +
                '}';
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public long getTweetId() {
        return tweetId;
    }

    public void setTweetId(long tweetId) {
        this.tweetId = tweetId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

