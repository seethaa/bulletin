package com.codepath.bulletin.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.bulletin.R;
import com.codepath.bulletin.models.Article;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ArticleArrayAdapter handles populating Gridview handling Article items
 * Created by seetha on 7/26/16.
 */
public class ArticleArrayAdapter extends ArrayAdapter<Article> {

    public ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context, android.R.layout.simple_list_item_1, articles);
    }

    static class ViewHolder {
        @BindView(R.id.ivImage) ImageView imageView;
        @BindView(R.id.tvTitle) TextView tvTitle;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override public View getView(int position, View view, ViewGroup parent) {
        Article article = this.getItem(position);

        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.item_article_result, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        //clear out recycled image from convertview from last time
        holder.imageView.setImageResource(0);

        holder.tvTitle.setText(article.getHeadline());

        //populate thumbnail image
        //remotely download image in the background
        String thumbnail = article.getThumbNail();
        //check if thumbnail is empty since NYTimesAPI sometimes returns ""
        if (!TextUtils.isEmpty(thumbnail)) {
            Glide.with(getContext()).load(thumbnail).centerCrop().placeholder(R.drawable.nytlogooriginal).into(holder.imageView);

        }
        else{
            Glide.with(getContext()).load(R.drawable.nytlogooriginal).centerCrop().placeholder(R.drawable.nytlogooriginal).into(holder.imageView);

        }
        return view;
    }

}
