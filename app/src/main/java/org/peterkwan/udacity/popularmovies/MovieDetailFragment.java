package org.peterkwan.udacity.popularmovies;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.peterkwan.udacity.popularmovies.data.Movie;
import org.peterkwan.udacity.popularmovies.utils.StringUtils;

import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor
public class MovieDetailFragment extends Fragment {

    private static final String MOVIE = "movie";
    private static final String FROM_DATE_FORMAT = "yyyy-MM-dd";
    private static final String TO_DATE_FORMAT = "MMM dd, yyyy";

    private Unbinder unbinder;

    @Setter
    private Movie movie = null;

    @BindView(R.id.movieDetailContentLayout)
    View contentLayoutView;

    @BindView(R.id.backdropImageView)
    ImageView backdropImageView;

    @BindView(R.id.posterImageView)
    ImageView posterImageView;

    @BindView(R.id.titleTextView)
    TextView titleTextView;

    @BindView(R.id.originalTitleTextView)
    TextView originalTitleTextView;

    @BindView(R.id.releaseDateTextView)
    TextView releaseDateTextView;

    @BindView(R.id.plotSynopsisTextView)
    TextView plotSynopsisTextView;

    @BindView(R.id.userRatingTextView)
    TextView userRatingTextView;

    @BindView(R.id.ratingBar)
    RatingBar ratingBar;

    @BindString(R.string.original_title)
    String originalTitle;

    @BindString(R.string.release_date)
    String releasedDate;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (savedInstanceState != null)
            movie = savedInstanceState.getParcelable(MOVIE);
        else {
            Bundle bundle = getArguments();
            if (bundle != null && bundle.containsKey(MOVIE))
                movie = bundle.getParcelable(MOVIE);
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        if (movie != null)
            setItemSelectedView();
        else
            setNoItemSelectedView();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(MOVIE, movie);
        super.onSaveInstanceState(outState);
    }

    private void setNoItemSelectedView() {
        contentLayoutView.setVisibility(View.GONE);
    }

    private void setItemSelectedView() {
        Picasso.get()
                .load(movie.getBackdropImagePath())
                .into(backdropImageView);
        backdropImageView.setContentDescription(movie.getTitle());

        Picasso.get()
                .load(movie.getPosterImagePath())
                .into(posterImageView);
        posterImageView.setContentDescription(movie.getTitle());

        titleTextView.setText(movie.getTitle());
        originalTitleTextView.setText(String.format(originalTitle, movie.getOriginalTitle()));
        if (movie.getTitle().equals(movie.getOriginalTitle()))
            originalTitleTextView.setVisibility(View.GONE);

        releaseDateTextView.setText(String.format(releasedDate, StringUtils.convertDateFormat(movie.getReleaseDate(), FROM_DATE_FORMAT, TO_DATE_FORMAT)));
        plotSynopsisTextView.setText(movie.getPlotSynopsis());

        double userRating = movie.getUserRating();
        userRatingTextView.setText(String.format(Locale.getDefault(), "%.1f", userRating));
        ratingBar.setRating((float) userRating);

        contentLayoutView.setVisibility(View.VISIBLE);
    }
}
