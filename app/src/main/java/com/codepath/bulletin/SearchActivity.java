package com.codepath.bulletin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
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

import com.codepath.bulletin.adapters.ArticleArrayAdapter;
import com.codepath.bulletin.models.Article;
import com.codepath.bulletin.models.Filter;
import com.codepath.bulletin.network.NYTimesAPIClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements FilterDialogFragment.SetFilterDialogListener {
    EditText etQuery;
    GridView gvResults;
//    Button btnSearch;
    NYTimesAPIClient mNYTimesAPIClient;
    ArrayList<Article> mArticles;
    ArticleArrayAdapter mArticleArrayAdapter;
//    static Filter mFilter;

     static String BEGIN_DATE_STR = "beginDate";
     static String SORT_BY_STR = "sortBy";
     static String NEWSDESK_ARTS_STR = "arts";
     static String NEWSDESK_FASHION_STYLE_STR = "fashionStyle";
     static String NEWSDESK_SPORTS_STR = "sports";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setupViews();

//        SharedPreferences mSettings = getApplication().getSharedPreferences("Filters", 0);
//        SharedPreferences.Editor editor = mSettings.edit();
//        editor.putString("test", "tester1");
//        editor.putString(SearchActivity.BEGIN_DATE_STR, "Sat, July 28");
//        editor.putString(SearchActivity.SORT_BY_STR, "Oldest");
//        editor.putBoolean(SearchActivity.NEWSDESK_ARTS_STR, false);
//        editor.putBoolean(SearchActivity.NEWSDESK_FASHION_STYLE_STR, false);
//        editor.putBoolean(SearchActivity.NEWSDESK_SPORTS_STR, false);
//
//        editor.apply();


    }

    private void setupViews() {
//        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
//        btnSearch = (Button) findViewById(R.id.btnSearch);
        mArticles = new ArrayList<>();
        mArticleArrayAdapter = new ArticleArrayAdapter(this, mArticles);
        gvResults.setAdapter(mArticleArrayAdapter);

        //hock up listener for grid click
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        searchView.requestFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                fetchAllArticles(query);

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void showEditDialog() {
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

    /**
     * Called when dialogfragment is finished
     *
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


    }

    /**
     * On click handler for search button
     * @param view
     */
    public void onArticleSearch(View view) {
        String query = etQuery.getText().toString();
        Toast.makeText(this, "Searching for " + query, Toast.LENGTH_LONG).show();
        fetchAllArticles(query);
    }

    /**
     * Method to fetch updated data and refresh the listview. This method creates a new NYTimesAPIClient and
     * makes HTTPRequest to get a list of currently playing movies.
     *
     */
    private void fetchFilteredArticles(String query, String beginDate, String sortBy, String newsDesk) {
        //newsDesk is comma separated list of news desk values
        mNYTimesAPIClient = new NYTimesAPIClient();
        mNYTimesAPIClient.getArticlesOnSearch(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults = null;

                try {
                    //get all results
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    Log.d("DEBUG", response.toString());
                    //mArticles.clear(); //clear existing items from list
                    mArticleArrayAdapter.addAll(Article.fromJSONArray(articleJsonResults)); //add all items to list
                    Log.d("DEBUG", mArticles.toString());
                    //mArticleArrayAdapter.notifyDataSetChanged(); //notify adapter
//                    printAllMovies(mMoviesArrayList); //debugging purposes
//                    mSwipeContainer.setRefreshing(false);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });


    }

        /**
         * Method to fetch updated data and refresh the listview. This method creates a new NYTimesAPIClient and
         * makes HTTPRequest to get a list of currently playing movies.
         *
         */
    private void fetchAllArticles(String query) {
        mNYTimesAPIClient = new NYTimesAPIClient();
        mNYTimesAPIClient.getArticlesOnSearch(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults = null;

                try {
                    //get all results
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    Log.d("DEBUG", response.toString());
                    //mArticles.clear(); //clear existing items from list
                    mArticleArrayAdapter.addAll(Article.fromJSONArray(articleJsonResults)); //add all items to list
                    Log.d("DEBUG", mArticles.toString());
                    //mArticleArrayAdapter.notifyDataSetChanged(); //notify adapter
//                    printAllMovies(mMoviesArrayList); //debugging purposes
//                    mSwipeContainer.setRefreshing(false);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_filter:
                showEditDialog();
                // Handle this selection
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
