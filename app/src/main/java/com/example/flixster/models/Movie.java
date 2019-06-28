package com.example.flixster.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Movie {

    // tracks movie data from API:
        // in results' value's list of now_playing
    private String title;
    private String overview;
    private String posterPath; // only partial path, not full url
    // add new field for backdrop path
    private String backdropPath;

    // add constructor
    public Movie(JSONObject object) throws JSONException {
        title = object.getString("title");
        overview = object.getString("overview");
        posterPath = object.getString("poster_path");
        backdropPath = object.getString("backdrop_path");
        // each movie object is an element of JSONArray in results of get now playing api
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }
}
