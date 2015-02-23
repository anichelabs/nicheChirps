package com.aneeshjoshi.nichechirps.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aneeshjoshi.nichechirps.R;
import com.aneeshjoshi.nichechirps.models.Tweet;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TweetsArrayAdapter extends ArrayAdapter<Tweet>{

    Pattern MATCH_ELAPSED_TIME = Pattern.compile("\\s(minutes\\sago|minute\\sago|" +
            "hours\\sago|hour\\sago|" +
            "day\\sago|days\\sago|" +
            "week\\sago|weeks\\sago|" +
            "month\\sago|months\\sago|" +
            "year\\sago|years\\sago)");

    public class ViewHolder{
        ImageView ivProfile;
        TextView tvUsername;
        TextView tvTweet;
        TextView tvTimestamp;
    }

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, R.layout.item_tweet, tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_tweet, parent, false);
            viewHolder.ivProfile = (ImageView) convertView.findViewById(R.id.ivProfilePic);
            viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.tvTweet = (TextView) convertView.findViewById(R.id.tvTweet);
            viewHolder.tvTimestamp = (TextView) convertView.findViewById(R.id.tvTimestamp);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Tweet tweet = getItem(position);
        viewHolder.ivProfile.setImageResource(android.R.color.transparent);
        Picasso.with(getContext())
                .load(tweet.getUser().getProfileImageUrl())
                .fit().centerInside().into(viewHolder.ivProfile);
        viewHolder.tvUsername.setText("@" + tweet.getUser().getScreenName());
        viewHolder.tvTweet.setText(tweet.getBody());
        String createdAt = tweet.getCreatedAt();
        String relativeLongString = getRelativeTimeAgo(createdAt);
        viewHolder.tvTimestamp.setText(getElapsedString(relativeLongString));

        return convertView;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    private String getElapsedString(String elapsedTime) {
        Matcher m = MATCH_ELAPSED_TIME.matcher(elapsedTime);
        StringBuffer sb = new StringBuffer();
        while(m.find()){
            String text = m.group(1);
            if(text.contains("minute")){
                m.appendReplacement(sb, "m");
            } else if(text.contains("hour")){
                m.appendReplacement(sb, "h");
            } else if(text.contains("day")){
                m.appendReplacement(sb, "d");
            } else if(text.contains("week")){
                m.appendReplacement(sb, "w");
            } else if(text.contains("month")){
                m.appendReplacement(sb, "Mons");
            } else if(text.contains("year")){
                m.appendReplacement(sb, "Yrs");
            }
            m.appendTail(sb);
        }
        return sb.toString();
    }


}
