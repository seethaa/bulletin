package com.codepath.bulletin.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.bulletin.utils.EndlessScrollListener;
import com.codepath.bulletin.R;
import com.codepath.bulletin.adapters.ArticleArrayAdapter;
import com.codepath.bulletin.models.Article;
import com.codepath.bulletin.models.Filter;
import com.codepath.bulletin.network.NYTimesAPIClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements FilterDialogFragment.SetFilterDialogListener {
    static final String BEGIN_DATE_STR = "beginDate";
    static final String SORT_BY_STR = "sortBy";
    static final String NEWSDESK_ARTS_STR = "arts";
    static final String NEWSDESK_FASHION_STYLE_STR = "fashionStyle";
    static final String NEWSDESK_SPORTS_STR = "sports";

    private GridView mGridViewResults;
    private NYTimesAPIClient mNYTimesAPIClient;
    private ArrayList<Article> mArticles;
    private ArticleArrayAdapter mArticleArrayAdapter;
    private String mSearchQuery;
    private SwipeRefreshLayout mSwipeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setupViews();

    }

    /**
     * Lays out all views, attaches listener to gridview, and sets arrayadapter.
     */
    private void setupViews() {
        mGridViewResults = (GridView) findViewById(R.id.gvResults);
        mArticles = new ArrayList<>();
        mArticleArrayAdapter = new ArticleArrayAdapter(this, mArticles);
        mGridViewResults.setAdapter(mArticleArrayAdapter);

        //hook up listener for grid click
        mGridViewResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //create an intent to display the article
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);

                //get the article that needs to be displayed
                Article article = mArticles.get(position);

                //pass in that article into intent
                i.putExtra("article", Parcels.wrap(article));

                //launch activity
                startActivity(i);

            }
        });

        //set on scroll listener for infinite scrolling
        mGridViewResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                customLoadMoreDataFromApi(page);
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

        //fetch initial articles with no search text
        fetchFilteredArticles("");

    }

    /**
     * Used for infinite scrolling
     *
     * @param page
     */
    private void customLoadMoreDataFromApi(int page) {
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter

        System.out.println("DEBUGGY PAGE " + page);
        fetchFilteredArticlesByPage(mSearchQuery, page);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // Use a custom search icon for the SearchView in AppBar
        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.search);
        // Customize searchview text and hint colors
        int searchEditId = android.support.v7.appcompat.R.id.search_src_text;
        EditText et = (EditText) searchView.findViewById(searchEditId);
        et.setTextColor(Color.BLACK);
        et.setHintTextColor(Color.BLACK);


        // Expand the search view and request focus
        searchItem.expandActionView();
        searchView.setQuery("", false);
        searchView.setIconifiedByDefault(true);
        searchView.isFocusableInTouchMode();
        searchView.requestFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                // clear existing view and perform query here
                mSearchQuery = query;
                refreshItems();

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                fetchFilteredArticles(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Checks if network is available and if device is online. If connected, it fetches articles based on search query.
     */
    private void refreshItems() {

        if (isNetworkAvailable() && isOnline()) {
            mArticles.clear();
            mArticleArrayAdapter.notifyDataSetChanged();
            fetchFilteredArticles(mSearchQuery);
        } else {
            callNetworkDialog();

        }
    }


    /**
     * Called when dialogfragment is finished
     */
    @Override
    public void onFinishSetFilterDialog() {
        Toast.makeText(this, "Got here  " + Filter.getInstance().getBeginDate(), Toast.LENGTH_LONG).show();


        //get filter values with intents
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(SearchActivity.BEGIN_DATE_STR, Filter.getInstance().getBeginDate());
        editor.putString(SearchActivity.SORT_BY_STR, Filter.getInstance().getSortBy());
        editor.putBoolean(SearchActivity.NEWSDESK_ARTS_STR, Filter.getInstance().isNewsdeskArts());
        editor.putBoolean(SearchActivity.NEWSDESK_FASHION_STYLE_STR, Filter.getInstance().isNewsdeskFashionStyle());
        editor.putBoolean(SearchActivity.NEWSDESK_SPORTS_STR, Filter.getInstance().isNewsdeskSports());

        editor.commit();

        //perform new search and refresh items in the list
        refreshItems();

    }

    /**
     * Checks if an active network is available
     *
     * @return true if network is available, false otherwise
     */
    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * Checks if device is connected to the internet
     *
     * @return true if device is connected, false otherwise
     */
    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Method to fetch updated data and refresh the gridview. This method creates a new NYTimesAPIClient and
     * makes HTTPRequest to get a list of articles based on the string query.
     */
    private void fetchFilteredArticles(String query) {

        if (isOnline() && isNetworkAvailable()) {
            //newsDesk is comma separated list of news desk values
            mNYTimesAPIClient = new NYTimesAPIClient();
//        String query = Filter.getInstance().getFilteredQuery();
            mNYTimesAPIClient.getArticlesOnFilteredSearch(query, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
                    JSONArray articleJsonResults = null;
                    try {
                        //get all results
                        articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                        Log.d("DEBUG", response.toString());
                        mArticleArrayAdapter.addAll(Article.fromJSONArray(articleJsonResults)); //add all items to list
                        Log.d("DEBUG", mArticles.toString());


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.d("DEBUG", "Failed to fetch articles on string query");
                }

            });
        } else {
            callNetworkDialog();
        }

    }

    /**
     * Alert user to connect to network
     */
    private void callNetworkDialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(SearchActivity.this);
        alert.setTitle("No Connection Available");
        alert.setMessage("Please check your network connection, and try again!");
        alert.setPositiveButton("OK", null);
        alert.show();
    }

    /**
     * Get filtered articles by page.
     * Uses NYTimesAPIClient and makes network call with search query and page number
     *
     * @param query
     * @param page
     */
    private void fetchFilteredArticlesByPage(String query, int page) {

        if (isOnline() && isNetworkAvailable()) {

            //newsDesk is comma separated list of news desk values
            mNYTimesAPIClient = new NYTimesAPIClient();
//        String query = Filter.getInstance().getFilteredQuery();
            mNYTimesAPIClient.getArticlesOnFilteredSearchByPage(query, page, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
                    JSONArray articleJsonResults = null;
                    try {
                        //get all results
                        articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                        Log.d("DEBUG", response.toString());
                        //mArticles.clear(); //clear existing items from list
                        ArrayList<Article> newArticles = Article.fromJSONArray(articleJsonResults);

                        for (int i = 0; i < newArticles.size(); i++) {
                            mArticles.add(newArticles.get(i));
                        }
                        mArticleArrayAdapter.notifyDataSetChanged(); //add all items to list
                        Log.d("DEBUG", mArticles.toString());
                        //mArticleArrayAdapter.notifyDataSetChanged(); //notify adapter
//                    printAllMovies(mMoviesArrayList); //debugging purposes
//                    mSwipeContainer.setRefreshing(false);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.d("DEBUG", "Failed to fetch articles on string query with page number");
                }

            });
        } else {//not connected
            callNetworkDialog();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_filter:
                showFilterDialog();
                // Handle this selection
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Calls Filter Dialog Fragment
     */
    private void showFilterDialog() {
        FragmentManager fm = getSupportFragmentManager();
        FilterDialogFragment editFilterDialogFragment = newInstance();
        editFilterDialogFragment.show(fm, "dialog_filter");
    }

    /**
     * Used for creating FilterDialogFragment and binding arguments
     *
     * @return
     */
    static FilterDialogFragment newInstance() {
        FilterDialogFragment f = new FilterDialogFragment();
        return f;
    }

}
