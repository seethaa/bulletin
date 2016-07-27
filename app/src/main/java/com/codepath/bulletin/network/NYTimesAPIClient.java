package com.codepath.bulletin.network;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by seetha on 7/26/16.
 */
public class NYTimesAPIClient {

    private AsyncHttpClient mClient;

    private final String API_KEY = "33e66981e40f41188b6457a705b61974";
    private final String API_BASE_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json?";

    public NYTimesAPIClient() {
        this.mClient = new AsyncHttpClient();
    }

    private String getAPIUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl + "api-key=" + API_KEY;
    }

    /**
     * @TODO: Method for accessing the search API
     */
    public void getArticlesOnSearch(final String query, JsonHttpResponseHandler handler) {
        //            String url = getAPIUrl("now_playing?");

//            mClient.get(url + URLEncoder.encode(query, "utf-8"), handler);
        RequestParams params = new RequestParams();
        params.put("api-key", API_KEY);
        params.put("page", 0);
        params.put("q", query);
        mClient.get(API_BASE_URL, params, handler);
    }

    /**
     * API Call to get all currently playing movies
     *
     * @param handler
     */
    public void getAllCurrentlyPlayingMovies(JsonHttpResponseHandler handler) {
        String url = getAPIUrl("now_playing?");
        mClient.get(url, handler);
    }

    /**
     * API Call to get extra details about a particular movie using the movie ID
     */
    public void getExtraMovieDetails(String movieID, JsonHttpResponseHandler handler) {
        String url = getAPIUrl(movieID + "?");
        url = url + "&append_to_response=similar_movies,alternative_titles,keywords,releases,trailers,credits";
        mClient.get(url, handler);
    }
}
