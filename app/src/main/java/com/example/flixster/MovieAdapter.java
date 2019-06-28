package com.example.flixster;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.flixster.models.Config;
import com.example.flixster.models.Movie;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    // list of movies
    ArrayList<Movie> movies;
    // config needed for image urls
    Config config;
    // context for rendering image
    Context context;

    // initialize with list
    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    // create *viewholder* class as static *inner* class
        // viewholder is each individual list item
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // track object views
        ImageView ivPosterImage;
        TextView tvTitle;
        TextView tvOverview;
        // for landscape orientation
        ImageView ivBackdropImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // lookup view objects by id
            ivPosterImage = itemView.findViewById(R.id.ivPosterImage);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOverview = itemView.findViewById(R.id.tvOverview);
            ivBackdropImage = itemView.findViewById(R.id.ivBackdropImage);
        }
    }

    // creates and inflates a new view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        // need to *get context* and create *inflater*
        context = parent.getContext();
        // parent is whole xml file that it comes from?
        LayoutInflater inflater = LayoutInflater.from(context);
        // inflater finds the small row structure and makes it usable for later rows?

        // create view using item_movie layout
        View movieView = inflater.inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(movieView);
    }

    // associates a created/inflated view with a specific data element at a specific position in the list
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        // get movie data out of specified position from list
        Movie movie = movies.get(i);
        // populate view with data from movie
        viewHolder.tvOverview.setText(movie.getOverview());
        viewHolder.tvTitle.setText(movie.getTitle());

        // figure out which orientation the phone is in
        boolean isPortrait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        // Toast.makeText(context, String.format("portrait orientation: %s", isPortrait), Toast.LENGTH_LONG).show();
            // this getConfiguration is different from the one we defined

        String imageUrl;

        // if portrait, load poster image
        if (isPortrait) {
            // set image using Glide
            // build image url
            imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());
        }
        else {
            imageUrl = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());
        }

        // cool Java syntax to condense if/else statements/cases

        int placeholderId = isPortrait ? R.drawable.flicks_movie_placeholder : R.drawable.flicks_backdrop_placeholder;
        ImageView imageView = isPortrait ? viewHolder.ivPosterImage : viewHolder.ivBackdropImage;

        // load image using glide
        Glide.with(context)
                .load(imageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 35, 0))
                .placeholder(placeholderId)
                .error(placeholderId)
                .into(imageView);
    }

    // returns size of entire data set
    @Override
    public int getItemCount() {
        return movies.size();
    }
}
