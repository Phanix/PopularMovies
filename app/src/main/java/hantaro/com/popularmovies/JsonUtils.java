package hantaro.com.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    private static final String POSTER_URL = "https://image.tmdb.org/t/p/w500";

    public static List<Movie> ParseData(String data) {
        JSONObject root;
        List<Movie> movies = new ArrayList<>();
        try {
            root = new JSONObject(data);
            JSONArray resultJSONArray = root.getJSONArray("results");
            for (int i = 0; i < resultJSONArray.length(); i++) {
                JSONObject resultObject = resultJSONArray.getJSONObject(i);
                String posterPath = resultObject.getString("poster_path");
                String releaseDate = resultObject.getString("release_date");
                String voteAverage = resultObject.getString("vote_average");
                String title = resultObject.getString("title");
                String overview = resultObject.getString("overview");
                String movieId = resultObject.getString("id");

                Movie movie = new Movie(title, POSTER_URL + posterPath, overview, releaseDate, voteAverage, movieId);
                movies.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public static List<String> ParseTrailers(String data) {
        JSONObject root;
        List<String> trailers = new ArrayList<>();
        try {
            root = new JSONObject(data);
            JSONArray resultJSONArray = root.getJSONArray("results");
            for (int i = 0; i < resultJSONArray.length(); i++) {
                JSONObject resultObject = resultJSONArray.getJSONObject(i);
                String trailer = resultObject.getString("key");
                trailers.add(trailer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailers;
    }

    public static List<Reviewer> ParseReviers(String data) {
        JSONObject root;
        List<Reviewer> reviewers = new ArrayList<>();
        try {
            root = new JSONObject(data);
            JSONArray resultJSONArray = root.getJSONArray("results");
            for (int i = 0; i < resultJSONArray.length(); i++) {
                JSONObject resultObject = resultJSONArray.getJSONObject(i);
                String author = resultObject.getString("author");
                String content = resultObject.getString("content");
                reviewers.add(new Reviewer(author, content));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviewers;
    }



    public static String convertInputStream(InputStream inputStream) {
        StringBuilder data = new StringBuilder();
        BufferedReader bufferedReader;
        InputStreamReader inputStreamReader;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                data.append(line);
                line = bufferedReader.readLine();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data.toString();
    }
}
