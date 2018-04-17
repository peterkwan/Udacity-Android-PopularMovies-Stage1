package org.peterkwan.udacity.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONException;
import org.peterkwan.udacity.popularmovies.data.Movie;
import org.peterkwan.udacity.popularmovies.utils.JsonUtils;
import org.peterkwan.udacity.popularmovies.utils.NetworkUtils;

import java.io.IOException;
import java.util.List;

public class MovieListAsyncTaskLoader extends AsyncTaskLoader<List<Movie>> {

    private static final String LOG_TAG = MovieListAsyncTaskLoader.class.getSimpleName();
    private static final String SORT_ORDER = "sortOrder";

    private List<Movie> movieList;
    private Bundle loaderArgs;

    public MovieListAsyncTaskLoader(Context context, Bundle args) {
        super(context);
        this.loaderArgs = args;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (movieList != null)
            deliverResult(movieList);
        else
            forceLoad();
    }

    @Nullable
    @Override
    public List<Movie> loadInBackground() {
        if (loaderArgs == null || !loaderArgs.containsKey(SORT_ORDER))
            return null;

        if (NetworkUtils.isNetworkDisconnected(getContext()))
            return null;

        try {
            String resultJson = NetworkUtils.retrieveMovieListFromTMdb(loaderArgs.getString(SORT_ORDER));
            return JsonUtils.constructMovieListFromJson(resultJson);
        } catch (IOException | JSONException e) {
            Log.e(LOG_TAG, "Error loading data", e);
            return null;
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        movieList = null;
    }

    @Override
    public void deliverResult(@Nullable List<Movie> data) {
        movieList = data;
        super.deliverResult(data);
    }
}
