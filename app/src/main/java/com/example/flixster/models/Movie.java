package com.example.flixster.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel // class is parcelable
public class Movie {

    // tracks movie data from API:
        // in results' value's list of now_playing
    // for parcel, all fields must be public
    String title;
    String overview;
    String posterPath; // only partial path, not full url
    // add new field for backdrop path
    String backdropPath;
    Double voteAverage;
    // for youtube player
    Integer id;

    // no-arg constructor required for parcel
    public Movie() {}

    // add constructor
    public Movie(JSONObject object) throws JSONException {
        title = object.getString("title");
        overview = object.getString("overview");
        posterPath = object.getString("poster_path");
        backdropPath = object.getString("backdrop_path");
        voteAverage = object.getDouble("vote_average");
        id = object.getInt("id"); // not same as youtube id in get videos endpoint
        // each movie object is an element of JSONArray in results of get now playing api
    }

    public Integer getId() {
        return id;
    }

    public Double getVoteAverage() {
        return voteAverage;
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
