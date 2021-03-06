package com.codepath.bulletin.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by seetha on 7/26/16.
 */
@Parcel
public class Article {
    private static final String THUMBNAIL_URL_PREFIX = "http://www.nytimes.com/";
    private String webURL;
    private String headline;
    private String thumbNail;

    //empty constructor needed by Parceler Library
    public Article() {

    }

    public String getWebURL() {
        return webURL;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbNail() {
        return thumbNail;
    }


    /**
     * Article constructor. Takes JSONObject, parses it, and converts to Article object.
     * @param jsonObject
     */
    public Article(JSONObject jsonObject) {
        try {
            this.webURL = jsonObject.getString("web_url");
            this.headline = jsonObject.getJSONObject("headline").getString("main");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");
            //check that it has at least one entry in it, and get first one
            if (multimedia.length() > 0) {
                JSONObject multimediaJson = multimedia.getJSONObject(0);
                this.thumbNail = THUMBNAIL_URL_PREFIX + multimediaJson.getString("url");
            } else {
                this.thumbNail = "";
            }
        } catch (JSONException e) {

        }
    }

    /**
     * Factory method to parse entire list of articles.
     * Accepts JSONArray list of elements and convert them all into articles.
     *
     * @return
     */
    public static ArrayList<Article> fromJSONArray(JSONArray array) {
        ArrayList<Article> results = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                results.add(new Article(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

}
