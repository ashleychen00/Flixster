package com.example.flixster;

import android.os.Bundle;
import android.util.Log;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import static com.example.flixster.MovieDetailsActivity.YOUTUBE_KEY;

public class MovieTrailerActivity extends YouTubeBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_trailer);

        Log.i("MovieTrailerActivity", "in movie trailer activity");

        // added code so activity can play videos
        final String videoId = getIntent().getStringExtra(YOUTUBE_KEY);
        // find playerview from the layout
        YouTubePlayerView playerView = findViewById(R.id.player);
        // initialize with api key
        playerView.initialize(getString(R.string.youtube_api_key), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                // cues/playes video here, since success
                youTubePlayer.cueVideo(videoId); // apparently don't need full path, since know from youtube
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                // log error
                Log.e("MovieTrailerActivity", "Error initializing Youtube player");
            }
        });

    }
}
