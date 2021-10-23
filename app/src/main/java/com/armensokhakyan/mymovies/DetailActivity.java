package com.armensokhakyan.mymovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.armensokhakyan.mymovies.adapters.ReviewAdapter;
import com.armensokhakyan.mymovies.adapters.TrailerAdapter;
import com.armensokhakyan.mymovies.data.FavouriteMovie;
import com.armensokhakyan.mymovies.data.MainViewModel;
import com.armensokhakyan.mymovies.data.Movie;
import com.armensokhakyan.mymovies.data.Review;
import com.armensokhakyan.mymovies.data.Trailer;
import com.armensokhakyan.mymovies.utils.JSONUtils;
import com.armensokhakyan.mymovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    public static final String MOVIE_ID_KEY = "MOVIE_ID_KEY";

    private ImageView imageViewAddToFavorite;
    private ImageView imageViewBigPoster;
    private TextView textViewTitle;
    private TextView textViewOriginalTitle;
    private TextView textViewRate;
    private TextView textViewReleaseDate;
    private TextView textViewOverview;
    private RecyclerView recyclerViewTrailers;
    private RecyclerView recyclerViewReviews;

    private static String lang;
    private int movieId;
    private Movie movie;
    private FavouriteMovie favouriteMovie;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private LoaderManager loaderManager;
    private static final int LOADER_KEY_REVIEW = 123132;
    private static final int LOADER_KEY_VIDEOS = 123133;

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menuItemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.menuItemFavourite:
                Intent intentFavourite = new Intent(this, FavouriteActivity.class);
                startActivity(intentFavourite);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {

        viewModel = MainViewModel.getViewModel(this);
        lang = Locale.getDefault().getLanguage();
        loaderManager = LoaderManager.getInstance(this);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(MOVIE_ID_KEY)) {
            movieId = intent.getIntExtra(MOVIE_ID_KEY, -1);
        } else {
            finish();
        }

        movie = viewModel.getMovieById(movieId);

        if (movie == null) {
            finish();
            return;
        }

        imageViewAddToFavorite = findViewById(R.id.imageViewAddToFavourite);
        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRate = findViewById(R.id.textViewRate);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);
        recyclerViewTrailers = findViewById(R.id.recyclerViewTrailer);
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));

        Picasso.get().load(movie.getBigPosterPath()).into(imageViewBigPoster);
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewRate.setText(String.format("%s", movie.getVoteAverage()));
        textViewReleaseDate.setText(movie.getReleaseDate());
        textViewOverview.setText(movie.getOverview());

        trailerAdapter = new TrailerAdapter();
        trailerAdapter.setOnClickTrailerAdapterListener(url -> {
            Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent1);
        });
        recyclerViewTrailers.setAdapter(trailerAdapter);

        reviewAdapter = new ReviewAdapter();
        recyclerViewReviews.setAdapter(reviewAdapter);

        setFavouriteMovie();
        downloadData();
    }

    public void onClickAddToFavourite(View view) {
        if (favouriteMovie == null) {
            viewModel.insertFavouriteMovie(new FavouriteMovie(movie));
        } else {
            viewModel.deleteFavouriteMovie(new FavouriteMovie(movie));
        }
        setFavouriteMovie();
    }

    private void setFavouriteMovie() {
        favouriteMovie = viewModel.getFavouriteMovieById(movieId);

        if (favouriteMovie == null) {
            imageViewAddToFavorite.setImageResource(R.drawable.favourite_add_to);
        } else {
            imageViewAddToFavorite.setImageResource(R.drawable.favourite_remove);
        }
    }

    private void downloadData() {
        URL urlVideos = NetworkUtils.buildURLForVideos(movieId, lang);
        Bundle bundleVideos = new Bundle();
        bundleVideos.putString(NetworkUtils.KEY_FOR_BUNDLE_URL, urlVideos.toString());
        loaderManager.restartLoader(LOADER_KEY_VIDEOS, bundleVideos, new VideosLoader());

        URL urlReviews = NetworkUtils.buildURLForReviews(movieId, 1, lang);
        Bundle bundleReviews = new Bundle();
        bundleReviews.putString(NetworkUtils.KEY_FOR_BUNDLE_URL, urlReviews.toString());
        loaderManager.restartLoader(LOADER_KEY_REVIEW, bundleReviews, new ReviewsLoader());

    }

    private class VideosLoader implements LoaderManager.LoaderCallbacks<JSONObject> {

        @NonNull
        @Override
        public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle bundle) {
            NetworkUtils.JsonLoader jsonLoader = new NetworkUtils.JsonLoader(getApplicationContext(), bundle);
            return jsonLoader;
        }

        @Override
        public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject jsonObject) {
            ArrayList<Trailer> trailers = JSONUtils.getMoviesVideosFromJSON(jsonObject);
            trailerAdapter.setTrailers(trailers);
            loaderManager.destroyLoader(LOADER_KEY_VIDEOS);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

        }
    }

    private class ReviewsLoader implements LoaderManager.LoaderCallbacks<JSONObject> {

        @NonNull
        @Override
        public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle bundle) {
            return new NetworkUtils.JsonLoader(getApplicationContext(), bundle);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject jsonObject) {
            ArrayList<Review> reviews = JSONUtils.getMoviesReviewsFromJSON(jsonObject);
            reviewAdapter.setReviews(reviews);
            loaderManager.destroyLoader(LOADER_KEY_REVIEW);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

        }
    }

}