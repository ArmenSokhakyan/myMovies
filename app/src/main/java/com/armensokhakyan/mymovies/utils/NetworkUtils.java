package com.armensokhakyan.mymovies.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {

    //for videos
    private static final String BASE_URL_FOR_VIDEOS = "https://api.themoviedb.org/3/movie/%s/videos";

    //for reviews
    private static final String BASE_URL_FOR_REVIEWS = "https://api.themoviedb.org/3/movie/%s/reviews";

    //for discover
    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String PARAM_API_KEY = "api_key";
    private static final String PARAM_LANGUAGE = "language";
    private static final String PARAM_SORT_BY = "sort_by";
    private static final String PARAM_PAGE = "page";
    private static final String PARAM_MIN_VOTE_COUNT = "vote_count.gte";

    private static final String API_KEY = "e5de6ec3175c0b96eb5c326e0db13299";
    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_TOP_RATE = "vote_average.desc";
    private static final String MIN_VOTE_COUNT_VALUE = "1000";

    public static final int POPULARITY = 0;
    public static final int TOP_RATE = 1;
    public static final String KEY_FOR_BUNDLE_URL = "KEY_FOR_BUNDLE_URL";

    public static URL buildURL(int sortBy, int page, String lang) {

        URL result = null;
        String methodOfSort;
        if (sortBy == POPULARITY) {
            methodOfSort = SORT_BY_POPULARITY;
        } else {
            methodOfSort = SORT_BY_TOP_RATE;
        }

        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .appendQueryParameter(PARAM_LANGUAGE, lang)
                .appendQueryParameter(PARAM_SORT_BY, methodOfSort)
                .appendQueryParameter(PARAM_MIN_VOTE_COUNT, MIN_VOTE_COUNT_VALUE)
                .appendQueryParameter(PARAM_PAGE, Integer.toString(page))
                .build();

        try {
            result = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static URL buildURLForVideos(int movieId, String lang) {

        URL result = null;

        Uri uri = Uri.parse(String.format(BASE_URL_FOR_VIDEOS, Integer.toString(movieId))).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .appendQueryParameter(PARAM_LANGUAGE, lang)
                .build();

        try {
            result = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static URL buildURLForReviews(int movieId, int page, String lang) {

        URL result = null;

        Uri uri = Uri.parse(String.format(BASE_URL_FOR_REVIEWS, movieId)).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .appendQueryParameter(PARAM_LANGUAGE, lang)
                .appendQueryParameter(PARAM_PAGE, Integer.toString(page))
                .build();

        try {
            result = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static class JsonLoader extends AsyncTaskLoader<JSONObject> {

        private Bundle bundle;
        private OnStartLoadingListener onStartLoadingListener;

        public interface OnStartLoadingListener {
            void onStartLoading();
        }

        /**
         * Stores away the application context associated with context.
         * Since Loaders can be used across multiple activities it's dangerous to
         * store the context directly; always use {@link #getContext()} to retrieve
         * the Loader's Context, don't use the constructor argument directly.
         * The Context returned by {@link #getContext} is safe to use across
         * Activity instances.
         *
         * @param context used to retrieve the application context.
         */
        public JsonLoader(@NonNull Context context, Bundle bundle) {
            super(context);
            this.bundle = bundle;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
            if (onStartLoadingListener != null) {
                onStartLoadingListener.onStartLoading();
            }
        }

        @Nullable
        @Override
        public JSONObject loadInBackground() {

            if (bundle == null) {
                return null;
            }
            String urlAsString = bundle.getString(KEY_FOR_BUNDLE_URL);
            URL url = null;
            try {
                url = new URL(urlAsString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            JSONObject result = null;

            if (url == null) {
                return null;
            }

            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(reader);

                StringBuilder stringBuilder = new StringBuilder();
                String line = bufferedReader.readLine();

                while (line != null) {
                    stringBuilder.append(line);
                    line = bufferedReader.readLine();
                }
                result = new JSONObject(stringBuilder.toString());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            return result;
        }

        public void setOnStartLoadingListener(OnStartLoadingListener onStartLoadingListener) {
            this.onStartLoadingListener = onStartLoadingListener;
        }
    }

}
