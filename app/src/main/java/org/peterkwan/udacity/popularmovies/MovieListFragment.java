package org.peterkwan.udacity.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import org.json.JSONException;
import org.peterkwan.udacity.popularmovies.data.Movie;
import org.peterkwan.udacity.popularmovies.utils.JsonUtils;
import org.peterkwan.udacity.popularmovies.utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lombok.NoArgsConstructor;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnMovieListClickListener} interface
 * to handle interaction events.
 */
@NoArgsConstructor
public class MovieListFragment extends BaseFragment implements MovieListAdapter.MovieListItemClickListener,
        LoaderManager.LoaderCallbacks<List<Movie>>, Handler.Callback {

    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();
    private static final int LOADER_ID = 1;
    private static final String MOVIE_LIST = "movieList";
    private static final String SORT_ORDER = "sortOrder";
    private static final int LOAD_DATA_FINISHED = 101;
    private static final int LOAD_DATA_STARTING = 102;

    private OnMovieListClickListener mListener;
    private MovieListAdapter movieListAdapter;
    private Unbinder unbinder;
    private ArrayList<Movie> movieList;
    private Context activity;
    private SharedPreferences sharedPreferences;

    @BindView(R.id.sortOrderSpinner)
    Spinner sortOrderSpinner;

    @BindView(R.id.movieListView)
    RecyclerView movieListView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindString(R.string.tmdb_credit)
    String tmdbCredit;

    @BindString(R.string.sort_order_pref_key)
    String sortOrderPrefKey;

    @BindString(R.string.movie_list_error_message)
    String movieListLoadDataErrorMsg;

    @BindArray(R.array.sort_order_values)
    String[] sortOrderValues;

    @BindInt(R.integer.grid_view_column_count)
    int gridViewColumnCount;

    private static Handler handler;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_LIST))
            movieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST);

        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        unbinder = ButterKnife.bind(this, rootView);
        activity = getActivity();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        sortOrderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sortOrder = sortOrderValues[position];
                if (!sharedPreferences.getString(sortOrderPrefKey, sortOrderValues[0]).equals(sortOrder)) {
                    sharedPreferences.edit().putString(sortOrderPrefKey, sortOrder).apply();
                    movieListAdapter.setMovieList(null);
                    mListener.onMovieItemSelected(null);
                }
                reloadMovieList(sortOrder);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String sortOrder = sharedPreferences.getString(sortOrderPrefKey, sortOrderValues[0]);
        sortOrderSpinner.setSelection(Arrays.asList(sortOrderValues).indexOf(sortOrder));

        movieListView.setLayoutManager(new GridLayoutManager(activity, gridViewColumnCount));

        handler = new Handler(this);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String sortOrder = sharedPreferences.getString(sortOrderPrefKey, sortOrderValues[0]);

        final View rootView = getView();

        movieListAdapter = new MovieListAdapter(this);
        movieListView.setAdapter(movieListAdapter);
        movieListView.setHasFixedSize(true);

        if (rootView != null && rootView.getViewTreeObserver() != null) {
            rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    int width = rootView.getWidth();
                    if (width > 0) {
                        int imageWidth = (width - gridViewColumnCount + 1 - 8) / gridViewColumnCount;
                        int imageHeight = (int) (imageWidth * 1.5);

                        movieListAdapter.setImageWidth(imageWidth);
                        movieListAdapter.setImageHeight(imageHeight);

                        rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }

            });
        }

        if (savedInstanceState == null)
            getLoaderManager().initLoader(LOADER_ID, constructLoaderArgs(sortOrder), this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMovieListClickListener) {
            mListener = (OnMovieListClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMovieListClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.tmdbLogoImageView)
    public void onLogoClick() {
        showMessageDialog(getActivity(), R.string.tmdb, tmdbCredit, android.R.drawable.ic_dialog_info);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(MOVIE_LIST, movieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClicked(Movie movie) {
        if (mListener != null)
            mListener.onMovieItemSelected(movie);
    }

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncMovieTaskLoader(activity, args);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {
        sendMessageToUIThread(LOAD_DATA_FINISHED);
        movieListAdapter.setMovieList(data);

        if (data == null)
            showMessageDialog(activity, R.string.movie_list_error_title, movieListLoadDataErrorMsg, android.R.drawable.ic_dialog_alert);
        else
            movieListView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg != null) {
            int messageId = msg.what;

            if (messageId == LOAD_DATA_FINISHED)
                progressBar.setVisibility(View.INVISIBLE);
            else if (messageId == LOAD_DATA_STARTING)
                progressBar.setVisibility(View.VISIBLE);
        }

        return true;
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnMovieListClickListener {
        void onMovieItemSelected(Movie movie);
    }

    private void reloadMovieList(String sortOrder) {
        getLoaderManager().restartLoader(LOADER_ID, constructLoaderArgs(sortOrder), this);
    }

    private Bundle constructLoaderArgs(String sortOrder) {
        Bundle args = new Bundle();
        args.putString(SORT_ORDER, sortOrder);
        return args;
    }

    private static void sendMessageToUIThread(int messageId) {
        Message msg = new Message();
        msg.what = messageId;
        handler.sendMessage(msg);
    }

    static class AsyncMovieTaskLoader extends AsyncTaskLoader<List<Movie>> {
        List<Movie> movies = null;
        final Bundle args;

        AsyncMovieTaskLoader(Context context, Bundle args) {
            super(context);
            this.args = args;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();

            if (movies != null)
                deliverResult(movies);
            else {
                sendMessageToUIThread(LOAD_DATA_STARTING);
                forceLoad();
            }
        }

        @Nullable
        @Override
        public List<Movie> loadInBackground() {
            if (args == null || !args.containsKey(SORT_ORDER))
                return null;

            if (NetworkUtils.isNetworkDisconnected(getContext()))
                return null;

            try {
                String resultJson = NetworkUtils.retrieveMovieListFromTMdb(args.getString(SORT_ORDER), BuildConfig.API_KEY);
                return JsonUtils.constructMovieListFromJson(resultJson);
            } catch (IOException | JSONException e) {
                Log.e(LOG_TAG, "Error loading data", e);
                return null;
            }
        }

        @Override
        public void deliverResult(@Nullable List<Movie> data) {
            movies = data;
            super.deliverResult(data);
        }
    }
}
