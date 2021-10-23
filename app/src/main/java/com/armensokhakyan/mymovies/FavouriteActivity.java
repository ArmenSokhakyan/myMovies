package com.armensokhakyan.mymovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.armensokhakyan.mymovies.adapters.MovieAdapter;
import com.armensokhakyan.mymovies.data.FavouriteMovie;
import com.armensokhakyan.mymovies.data.MainViewModel;
import com.armensokhakyan.mymovies.data.Movie;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFavourite;
    private MovieAdapter adapter;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {

        viewModel = MainViewModel.getViewModel(this);

        recyclerViewFavourite = findViewById(R.id.recyclerViewFavourite);
        recyclerViewFavourite.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new MovieAdapter();
        adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Intent intent = new Intent(FavouriteActivity.this, DetailActivity.class);
                intent.putExtra(DetailActivity.MOVIE_ID_KEY, adapter.getMovies().get(position).getId());
                startActivity(intent);

            }
        });
        recyclerViewFavourite.setAdapter(adapter);

        LiveData<List<FavouriteMovie>> favouriteMovies = viewModel.getFavouriteMovies();
        favouriteMovies.observe(this, new Observer<List<FavouriteMovie>>() {
            @Override
            public void onChanged(List<FavouriteMovie> favouriteMovies) {
                ArrayList<Movie> movies = new ArrayList<Movie>();
                movies.addAll(favouriteMovies);
                adapter.setMovies(movies);
            }
        });
    }
}