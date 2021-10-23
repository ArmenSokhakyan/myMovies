package com.armensokhakyan.mymovies.utils;

import com.armensokhakyan.mymovies.data.Movie;
import com.armensokhakyan.mymovies.data.Review;
import com.armensokhakyan.mymovies.data.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONUtils {

    private static final String KEY_RESULT = "results";
    private static final String KEY_ID = "id";
    private static final String KEY_VOTE_COUNT = "vote_count";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_BACKDROP_PATH = "backdrop_path";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_RELEASE_DATE = "release_date";

    private static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    private static final String SMALL_POSTER_SIZE = "w185";
    private static final String BIG_POSTER_SIZE = "w780";

    //for videos
    private static final String KEY_KEY_OF_VIDEO = "key";
    private static final String KEY_NAME_OF_VIDEO = "name";
    private static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    //for reviews
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_CONTENT = "content";

    public static ArrayList<Movie> getMoviesFromJSON(JSONObject jsonObject) {
        ArrayList<Movie> result = new ArrayList<>();
        if (jsonObject == null) {
            return result;
        }

        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULT);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonRow = jsonArray.getJSONObject(i);
                int id = jsonRow.getInt(KEY_ID);
                int voteCount = jsonRow.getInt(KEY_VOTE_COUNT);
                String title = jsonRow.getString(KEY_TITLE);
                String originalTitle = jsonRow.getString(KEY_ORIGINAL_TITLE);
                String overview = jsonRow.getString(KEY_OVERVIEW);
                String posterPath = BASE_POSTER_URL + SMALL_POSTER_SIZE + jsonRow.getString(KEY_POSTER_PATH);
                String bigPosterPath = BASE_POSTER_URL + BIG_POSTER_SIZE + jsonRow.getString(KEY_POSTER_PATH);
                String backdropPath = jsonRow.getString(KEY_BACKDROP_PATH);
                double voteAverage = jsonRow.getDouble(KEY_VOTE_AVERAGE);
                String releaseDate = jsonRow.getString(KEY_RELEASE_DATE);
                Movie movie = new Movie(id, voteCount, title, originalTitle, overview, posterPath, bigPosterPath, backdropPath, voteAverage, releaseDate);
                result.add(movie);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static ArrayList<Trailer> getMoviesVideosFromJSON(JSONObject jsonObject) {
        ArrayList<Trailer> result = new ArrayList<>();
        if (jsonObject == null) {
            return result;
        }

        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULT);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonRow = jsonArray.getJSONObject(i);
                String key = BASE_YOUTUBE_URL + jsonRow.getString(KEY_KEY_OF_VIDEO);
                String name = jsonRow.getString(KEY_NAME_OF_VIDEO);
                Trailer trailer = new Trailer(key, name);
                result.add(trailer);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static ArrayList<Review> getMoviesReviewsFromJSON(JSONObject jsonObject) {
        ArrayList<Review> result = new ArrayList<>();
        if (jsonObject == null) {
            return result;
        }

        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULT);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonRow = jsonArray.getJSONObject(i);
                String author = jsonRow.getString(KEY_AUTHOR);
                String content = jsonRow.getString(KEY_CONTENT);
                Review trailer = new Review(author, content);
                result.add(trailer);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

}
