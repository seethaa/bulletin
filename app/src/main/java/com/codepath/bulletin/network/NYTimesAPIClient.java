package com.codepath.bulletin.network;

import com.codepath.bulletin.models.Filter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * Handles API calls to NYTimes API
 * Created by seetha on 7/26/16.
 */
public class NYTimesAPIClient {

    private AsyncHttpClient mClient;

    private final String API_KEY = "33e66981e40f41188b6457a705b61974";
    private final String API_BASE_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json?";

    //SAMPLE URL:
    //String testURL = "https://api.nytimes.com/svc/search/v2/articlesearch.json?q=%22medicine%22&begin_date=20160112&sort=oldest&fq=news_desk:(%22Education%22%20%22Health%22)&api-key=33e66981e40f41188b6457a705b61974";
    //String testURL2 =  "https://api.nytimes.com/svc/search/v2/articlesearch.json?begin_date=20160112&sort=oldest&fq=news_desk:(%22Education%22%20%22Health%22)&api-key=33e66981e40f41188b6457a705b61974";

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

        String URL = getAPIUrl(Filter.getInstance().getFilteredQuery(searchText, 0));
        mClient.get(URL, handler);
    }

    /**
     * Searh for articles based on search text and page number
     * @param searchText
     * @param page
     * @param handler
     */
    public void getArticlesOnFilteredSearchByPage(final String searchText, final int page, JsonHttpResponseHandler handler) {

        String URL = getAPIUrl(Filter.getInstance().getFilteredQuery(searchText, page));
        mClient.get(URL, handler);
    }

}
