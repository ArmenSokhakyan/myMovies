package com.armensokhakyan.mymovies;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.armensokhakyan.mymovies.adapters.MovieAdapter;
import com.armensokhakyan.mymovies.data.MainViewModel;
import com.armensokhakyan.mymovies.data.Movie;
import com.armensokhakyan.mymovies.utils.JSONUtils;
import com.armensokhakyan.mymovies.utils.NetworkUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Switch switchSort;
    private TextView textViewPopularity;
    private TextView textViewByRate;
    private RecyclerView recyclerViewPosters;
    private ProgressBar progressBarMovies;

    private MovieAdapter adapter;
    private MainViewModel viewModel;

    private static String lang;

    //loader
    private int page = 1;
    private int sortMethod;
    private static final int LOADER_KEY = 133;
    private static boolean isLoading;
    private LoaderManager loaderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            case R.id.menuItemFavourite:
                Intent intent = new Intent(this, FavouriteActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickTextViewPopularity(View view) {
        setMethodOfSort(false);
        switchSort.setChecked(false);
    }

    public void onClickTextViewByRate(View view) {
        setMethodOfSort(true);
        switchSort.setChecked(true);
    }

    private void init() {

        lang = Locale.getDefault().getLanguage();

        loaderManager = LoaderManager.getInstance(this);
        viewModel = MainViewModel.getViewModel(this);
        LiveData<List<Movie>> moviesFromLiveData = viewModel.getMovies();
        moviesFromLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                if (page == 1) {
                    adapter.setMovies(movies);
                }
            }
        });

        switchSort = findViewById(R.id.switchSort);
        textViewPopularity = findViewById(R.id.textViewPopularity);
        textViewByRate = findViewById(R.id.textViewByRate);
        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        progressBarMovies = findViewById(R.id.progressBarMovies);

        adapter = new MovieAdapter();
        adapter.setOnPosterClickListener(position -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra(DetailActivity.MOVIE_ID_KEY, adapter.getMovies().get(position).getId());
            startActivity(intent);

        });

        adapter.setOnReachEnd(() -> {
            if (!isLoading) {
                downloadData(sortMethod, page);
            }
        });

        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, getPostersColumnCount()));
        recyclerViewPosters.setAdapter(adapter);

        switchSort.setChecked(true);
        switchSort.setOnCheckedChangeListener((buttonView, isChecked) -> setMethodOfSort(isChecked));
        switchSort.setChecked(false);
    }

    private int getPostersColumnCount() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density);
        return width / 185 > 0 ? width / 185 : 2;
    }

    private void setMethodOfSort(boolean byRate) {

        if (byRate) {
            textViewByRate.setTextColor(getResources().getColor(R.color.teal_200));
            textViewPopularity.setTextColor(getResources().getColor(R.color.white));
            sortMethod = NetworkUtils.TOP_RATE;
        } else {
            textViewByRate.setTextColor(getResources().getColor(R.color.white));
            textViewPopularity.setTextColor(getResources().getColor(R.color.teal_200));
            sortMethod = NetworkUtils.POPULARITY;
        }
        page = 1;

        downloadData(sortMethod, page);
    }

    private void downloadData(int sortMethod, int page) {
        URL url = NetworkUtils.buildURL(sortMethod, page, lang);
        Bundle bundle = new Bundle();
        bundle.putString(NetworkUtils.KEY_FOR_BUNDLE_URL, url.toString());
        loaderManager.restartLoader(LOADER_KEY, bundle, new MoviesLoader());
    }

    private class MoviesLoader implements LoaderManager.LoaderCallbacks<JSONObject> {
        @NonNull
        @Override
        public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle bundle) {
            NetworkUtils.JsonLoader jsonLoader = new NetworkUtils.JsonLoader(getApplicationContext(), bundle);
            jsonLoader.setOnStartLoadingListener(() -> isLoading = true);
            progressBarMovies.setVisibility(View.VISIBLE);
            return jsonLoader;
        }

        @Override
        public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject jsonObject) {
            ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);

            if (page == 1) {
                viewModel.deleteAllMovies();
            }

            if (!movies.isEmpty()) {
                for (Movie movie : movies) {
                    viewModel.insertMovie(movie);
                }
                adapter.addMovies(movies);
                page++;
            }
            isLoading = false;
            progressBarMovies.setVisibility(View.INVISIBLE);
            loaderManager.destroyLoader(LOADER_KEY);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

        }
    }

}