package com.aneeshjoshi.nichechirps.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

@Table(name = "users")
public class User extends Model {

    @Column(name = "name")
    private String name;

    @Column(name = "userId")
    private long userId;

    @Column(name = "screenName")
    private String screenName;

    @Column(name = "profileImageUrl")
    private String profileImageUrl;

    public User() {
        super();
    }

    // Parse model from JSON
    public static User fromJson(JSONObject object){
        User user = new User();
        try {
            user.name = object.getString("name");
            user.userId = object.getLong("id");
            user.screenName = object.getString("screen_name");
            user.profileImageUrl = object.getString("profile_image_url");
            //tweet.user
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
