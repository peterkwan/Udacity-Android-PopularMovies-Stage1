package org.peterkwan.udacity.popularmovies;

import android.content.Intent;
import android.os.Bundle;

import org.peterkwan.udacity.popularmovies.data.Movie;

public class MovieDetailActivity extends BaseActivity {

    private static final String MOVIE = "movie";
    private Movie movie = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null && intent.getParcelableExtra(MOVIE) != null)
            movie = intent.getParcelableExtra(MOVIE);

        setContentView(R.layout.activity_movie_detail);

        setTitle(movie.getTitle());
        initMovieDetailFragment();
    }

    private void initMovieDetailFragment() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(MOVIE, movie);

        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(bundle);

        fragmentManager.beginTransaction()
                .replace(R.id.movieDetailFragmentContainer, fragment)
                .commit();
    }

}
