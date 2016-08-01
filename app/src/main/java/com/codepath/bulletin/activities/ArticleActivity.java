package com.codepath.bulletin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.codepath.bulletin.R;
import com.codepath.bulletin.models.Article;

import org.parceler.Parcels;

/**
 * ArticleActivity shows details when an mArticle is selected in SearchActivity. User sees a WebView of the mArticle,
 * and an option to share the link using ShareActionProvider.
 */
public class ArticleActivity extends AppCompatActivity {

    private ShareActionProvider mShareAction;
    private Intent mShareIntent;
    private Article mArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        mArticle = (Article) Parcels.unwrap(getIntent().getParcelableExtra("mArticle"));

        WebView webView = (WebView) findViewById(R.id.wvArticle);

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                // Setup share intent now that web view has loaded
                prepareShareIntent();
                attachShareIntentAction();

                return true;
            }
        });
        webView.loadUrl(mArticle.getWebURL());
    }

    /**
     * Gets the mArticle headline and web URL and setup the associated share intent to hook into the provider
     */
    public void prepareShareIntent() {
        // Construct share intent
        mShareIntent = new Intent();
        mShareIntent.setAction(Intent.ACTION_SEND);
        mShareIntent.putExtra(Intent.EXTRA_SUBJECT, mArticle.getHeadline());
        mShareIntent.putExtra(Intent.EXTRA_TEXT, mArticle.getWebURL());
        mShareIntent.setType("text/plain");
    }

    /**
     *  Attaches the share intent to the share menu item provider
     */
    public void attachShareIntentAction() {
        if (mShareAction != null && mShareIntent != null)
            mShareAction.setShareIntent(mShareIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_article_detail, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch reference to the share action provider
        mShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        attachShareIntentAction(); // call here in case this method fires second

        // Return true to display menu
        return true;
    }
}
