package org.peterkwan.udacity.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.peterkwan.udacity.popularmovies.data.Movie;

import butterknife.BindBool;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MovieListFragment.OnMovieListClickListener {

    private static final String MOVIE = "movie";
    private static final String MOVIE_DETAIL_FRAGMENT = "movieDetailFragment";

    @BindBool(R.bool.two_pane_layout)
    boolean isTwoPaneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieItemSelected(Movie movie) {
        if (isTwoPaneLayout) {
            initMovieDetailFragment(movie);
        }
        else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(MOVIE, movie);
            startActivity(intent);
        }
    }

    private void initMovieDetailFragment(Movie movie) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(MOVIE, movie);

        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(bundle);

        replaceFragment(R.id.movieDetailFragmentContainer, fragment, MOVIE_DETAIL_FRAGMENT);
    }
}
