package com.codepath.bulletin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.codepath.bulletin.adapters.ArticleArrayAdapter;
import com.codepath.bulletin.models.Article;
import com.codepath.bulletin.network.NYTimesAPIClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    EditText etQuery;
    GridView gvResults;
    Button btnSearch;
    NYTimesAPIClient mNYTimesAPIClient;
    ArrayList<Article> mArticles;
    ArticleArrayAdapter mArticleArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setupViews();
    }

    private void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        btnSearch = (Button) findViewById(R.id.btnSearch);
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
                i.putExtra("article", article);

                //launch activity
                startActivity(i);

            }
        });

    }

    /**
     * On click handler for search button
     * @param view
     */
    public void onArticleSearch(View view) {
        String query = etQuery.getText().toString();
        Toast.makeText(this, "Searching for " + query, Toast.LENGTH_LONG).show();
        fetchAllMovies(query);
    }

        /**
         * Method to fetch updated data and refresh the listview. This method creates a new MovieAPIClient and
         * makes HTTPRequest to get a list of currently playing movies.
         *
         */
    private void fetchAllMovies(String query) {
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


}