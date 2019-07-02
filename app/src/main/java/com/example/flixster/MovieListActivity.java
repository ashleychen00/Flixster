package com.example.flixster;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.flixster.models.Config;
import com.example.flixster.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MovieListActivity extends AppCompatActivity {

    // CONSTANTS

    // base URL for API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    // parameter for API key
    public final static String API_KEY_PARAM = "api_key";
    // API KEY stored in res values file for security
    // tag for logging from this activity
    public final static String TAG = "MovieListActivity";

    // INSTANCE FIELDS

    AsyncHttpClient client;

    // list of currently playing movies
    ArrayList<Movie> movies;

    // recyclerview to help with showing list of movies on app
    RecyclerView rvMovies;
    // adapterview connected to recyclerview
    MovieAdapter adapter;

    // image config
    public static Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        // initialize client
        client = new AsyncHttpClient();
        // initialize movies list
        movies = new ArrayList<>();

        // initialize adapter -- must be after movies
            // after adapter connected to movies, can no longer reinitialize/declare, only modify
        adapter = new MovieAdapter(movies);

        // *resolve* ( = assign/declare) recycler view, connect a *layout manager* and adapter
        rvMovies = findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this)); // why this in linearlayoutmanager
        rvMovies.setAdapter(adapter);


        // get configuration information
        getConfiguration();
    }

    // get list of currently playing movies - very similar to getConfiguration
    private void getNowPlaying() {
        // create URL
        String url = API_BASE_URL + "/movie/now_playing";
        // assign request parameters of URL
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));

        // execute get request, JSON object returned
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // load results into movie list
                try {
                    JSONArray results = response.getJSONArray("results");
                    // iterate through results and create Movie objects
                    for (int i = 0; i < results.length(); i++) {
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);
                        // notify adapter that movies changed - row was added
                        adapter.notifyItemInserted(movies.size() - 1);
                    }
                    Log.i(TAG, String.format("Loaded %s movies", results.length()));
                } catch (JSONException e) {
                    logError("Failed to parse now_playing JSON object.", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed getting movies now playing.", throwable, true);
            }
        });
    }

    // get configuration from API - information on how to get other things, what sizes they are, etc.
        // from config, need: secure_base_url for images, poster_sizes (w342)
        // only need once
    private void getConfiguration() {
        // create URL
        String url = API_BASE_URL + "/configuration";
        // assign request parameters of URL
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));

        // execute the get request, expecting a JSON object returned
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // get imageBaseUrl and posterSize
                try {
                    config = new Config(response);
                        // if index 3 doesn't exist, opt with fallback
                        // (normal: getString returns string, get returns object)
                    Log.i(TAG, String.format("Loaded configuration with imageBaseUrl %s and posterSize %s", config.getImageBaseUrl(), config.getPosterSize()));
                    // pass to adapter
                    adapter.setConfig(config);
                    // get now playing movie list -- inside getConfiguration to enforce order
                    getNowPlaying();
                } catch (JSONException e) {
                    logError("Failed parsing configuration.", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed getting configuration.", throwable, true);
            }
        });

    }

    // handle errors, log and alert user
    private void logError(String message, Throwable error, boolean alertUser) {
        // always log error, but may or may not alert user with a Toast
        Log.e(TAG, message, error);
        if (alertUser) {
            Toast.makeText(getApplicationContext(), "The following error has occured: " + message, Toast.LENGTH_LONG).show();
        }
    }
}
