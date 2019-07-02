package com.example.flixster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.flixster.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MovieDetailsActivity extends AppCompatActivity {

    Movie movie;

    // xml view objects
    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;
    ImageView ivTrailer;

    // for api call
    AsyncHttpClient client;

    // key for youtube
    String youtubeKey;

    // for intents
    // numeric code to identify edit activity
    public static final int EDIT_REQUEST_CODE = 20;

    // keys used for passing data between activities
    public static final String YOUTUBE_KEY = "youtube_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        // unwrap movie from intent, using simple class name as key
        movie = Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.i("MovieDetailsActivity", String.format("Details for '%s'", movie.getTitle()));

        // establish wiring to xml file
        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        rbVoteAverage = findViewById(R.id.rbVoteAverage);
        ivTrailer = findViewById(R.id.ivTrailer);

        // add text to textviews
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        // set rating bar: given vote is out of 10, need to convert to out of 5
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage > 0 ? voteAverage / 2.0f : voteAverage);

        // set backdrop image
        Glide.with(this)
                .load(MovieListActivity.config.getImageUrl(MovieListActivity.config.getBackdropSize(), movie.getBackdropPath()))
                .bitmapTransform(new RoundedCornersTransformation(this, 35, 0))
                .placeholder(R.drawable.flicks_backdrop_placeholder)
                .error(R.drawable.flicks_backdrop_placeholder)
                .into(ivTrailer);

        getYoutubeKey();
    }

    private void getYoutubeKey() {
        // make api call to get youtube video
        client = new AsyncHttpClient();
        String url = MovieListActivity.API_BASE_URL + "/movie/" + movie.getId() + "/videos";
        RequestParams params = new RequestParams();
        params.put(MovieListActivity.API_KEY_PARAM, getString(R.string.api_key));

        // execute api call -- stores youtubeKey value
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    JSONObject info = (JSONObject) results.get(0);
                    youtubeKey = info.getString("key");
                    Log.i("MovieDetailsActivity", String.format("Retrieved youtube key: %s", youtubeKey));
                    setTrailerClick();
                } catch (JSONException e) {
                    Log.e("MovieDetailsActivity", "Could not parse JSONObject for movie video.");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("MovieDetailsActivity", "Failed to retrieve JSON Object for movie video.");
            }
        });
    }

    private void setTrailerClick() {
        ivTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create intent
                Intent intent = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class); // sketch with super?
                intent.putExtra(YOUTUBE_KEY, youtubeKey);
                MovieDetailsActivity.this.startActivity(intent);
            }
        });
    }
}
