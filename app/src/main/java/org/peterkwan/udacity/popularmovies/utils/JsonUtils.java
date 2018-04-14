package org.peterkwan.udacity.popularmovies.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.peterkwan.udacity.popularmovies.data.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class JsonUtils {

    private static final String RESULTS = "results";
    private static final String ID = "ID";
    private static final String TITLE = "title";
    private static final String ORIGINAL_TITLE = "original_title";
    private static final String PLOT_SYNOPSIS = "overview";
    private static final String BACKDROP_PATH = "backdrop_path";
    private static final String POSTER_PATH = "poster_path";
    private static final String RELEASE_DATE = "release_date";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String POPULARITY = "popularity";
    private static final String VIDEO = "video";

    private static final String IMAGE_URL = "http://image.tmdb.org/t/p/w%d%s";
    private static final int POSTER_IMAGE_WIDTH = 500;
    private static final int BACKDROP_IMAGE_WIDTH = 1280;

    public static List<Movie> constructMovieListFromJson(@Nullable String jsonString) throws JSONException {
        if (StringUtils.isEmpty(jsonString))
            return null;

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray jsonArray = jsonObject.optJSONArray(RESULTS);
        if (jsonArray == null || jsonArray.length() == 0)
            return null;

        List<Movie> movieList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Movie movie = constructMovieFromJson(jsonArray.optJSONObject(i));
            if (movie != null)
                movieList.add(movie);
        }
        return movieList;
    }

    private static Movie constructMovieFromJson(@Nullable JSONObject jsonObject) {
        if (jsonObject == null)
            return null;

        return Movie.builder()
                .id(jsonObject.optLong(ID))
                .title(jsonObject.optString(TITLE))
                .originalTitle(jsonObject.optString(ORIGINAL_TITLE))
                .plotSynopsis(jsonObject.optString(PLOT_SYNOPSIS))
                .backdropImagePath(constructImagePath(BACKDROP_IMAGE_WIDTH, jsonObject.optString(BACKDROP_PATH)))
                .posterImagePath(constructImagePath(POSTER_IMAGE_WIDTH, jsonObject.optString(POSTER_PATH)))
                .releaseDate(jsonObject.optString(RELEASE_DATE))
                .userRating(jsonObject.optDouble(VOTE_AVERAGE))
                .popularity(jsonObject.optDouble(POPULARITY))
                .hasVideo(jsonObject.optBoolean(VIDEO))
                .build();
    }

    private static String constructImagePath(int width, @NonNull String path) {
        if (StringUtils.isEmpty(path))
            return null;
        return String.format(Locale.getDefault(), IMAGE_URL, width, path);
    }
}
