package com.codepath.bulletin.network;

import com.codepath.bulletin.models.Filter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * Created by seetha on 7/26/16.
 */
public class NYTimesAPIClient {

    private AsyncHttpClient mClient;

    private final String API_KEY = "33e66981e40f41188b6457a705b61974";
    private final String API_BASE_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json?";

    //SAMPLE URL:
    String testURL = "https://api.nytimes.com/svc/search/v2/articlesearch.json?q=%22medicine%22&begin_date=20160112&sort=oldest&fq=news_desk:(%22Education%22%20%22Health%22)&api-key=33e66981e40f41188b6457a705b61974";
   String testURL2 =  "https://api.nytimes.com/svc/search/v2/articlesearch.json?begin_date=20160112&sort=oldest&fq=news_desk:(%22Education%22%20%22Health%22)&api-key=33e66981e40f41188b6457a705b61974";

    public NYTimesAPIClient() {
        this.mClient = new AsyncHttpClient();
    }

    private String getAPIUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl + "&api-key=" + API_KEY;
    }

    /**
     * @TODO: Method for accessing the search API
     */
    public void getArticlesOnFilteredSearch(final String searchText, JsonHttpResponseHandler handler) {
//        getTestArticles(handler);
//        RequestParams params = new RequestParams();
//        params.put("api-key", API_KEY);
//        params.put("page", 0);

//        if (!TextUtils.isEmpty(query)){{//add search portion if it's not empty
//            params.put("q", query);
//        }}
//        params.put("begin_date", Filter.getInstance().getBeginDateFormatted());
//        params.put("sort", Filter.getInstance().getSortByFormatted());
//        params.put("fq", Filter.getInstance().getNewsDeskFormatted());

//        params.put("begin_date", "20160112");
//        params.put("sort", "oldest");
//        params.put("fq", "(%22Education%22%20%22Health%22)");
        String URL = getAPIUrl(Filter.getInstance().getFilteredQuery(searchText));
        System.out.println("DEBUGGY HTTP URL: " + URL);
        mClient.get(URL, handler);
//        mClient.get(API_BASE_URL, params, handler);
    }

    /**
     * @TODO: Method for accessing the search API
     */
//    public void getAllArticles(final String query, JsonHttpResponseHandler handler) {
//
////            mClient.get(url + URLEncoder.encode(query, "utf-8"), handler);
//        RequestParams params = new RequestParams();
//        params.put("api-key", API_KEY);
//        params.put("page", 0);
//        params.put("q", query);
//        mClient.get(API_BASE_URL, params, handler);
//    }


    /**
     * API Call to get all currently playing movies
     *
     * @param handler
     */
    public void getTestArticles(JsonHttpResponseHandler handler) {
        mClient.get(testURL2, handler);
    }
//
//    /**
//     * API Call to get extra details about a particular movie using the movie ID
//     */
//    public void getExtraMovieDetails(String movieID, JsonHttpResponseHandler handler) {
//        String url = getAPIUrl(movieID + "?");
//        url = url + "&append_to_response=similar_movies,alternative_titles,keywords,releases,trailers,credits";
//        mClient.get(url, handler);
//    }
}
