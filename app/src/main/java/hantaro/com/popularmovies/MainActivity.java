package hantaro.com.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieClickListener, FavoriteAdapter.FavoriteClickListener {

    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String WEB = "http://api.themoviedb.org/3/movie/";
    private static final String POPULAR = "popular?api_key=" + API_KEY;
    private static final String TOP_RATED = "top_rated?api_key=" + API_KEY;
    private static final String QUERY_OPTION_VIDEOS = "/videos?api_key=";
    private static final String LANGUAGE = "&language=en-US";
    private static final String QUERY_OPTION_REVIEW = "/reviews?api_key=";
    public static final String STATE_POPULAR = "popular";
    public static final String STATE_TOP_RATED = "top_rated";
    public static final String STATE_FAVORITES = "favorites";
    private final String STATE_KEY = "key";
    private static final int NUMBER_OF_COLUMNS = 2;
    public static MovieDatabase sMovieDatabase;
    RecyclerView mRecyclerView;
    List<MovieEntry> mMovieEntries;
    MovieAdapter mMovieAdapter;
    FavoriteAdapter mFavoriteAdapter;
    Context mContext;
    MainViewModel mainViewModel = null;

    //Track current state
    private static String mCurrentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        sMovieDatabase = Room.databaseBuilder(mContext, MovieDatabase.class, MovieDatabase.DATABASE_NAME).allowMainThreadQueries().build();

        mRecyclerView = findViewById(R.id.rv_movie);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, NUMBER_OF_COLUMNS);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        favorite();

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(STATE_KEY)){
                //restore state
                String state = savedInstanceState.getString(STATE_KEY);
                mCurrentState = state;
                if(state.equals(STATE_FAVORITES)){
                    favorite();
                }else if(state.equals(STATE_POPULAR)){
                    fetchSelectedData(POPULAR);
                    setTitle(R.string.popular_title);
                }else if(state.equals(STATE_TOP_RATED)){
                    fetchSelectedData(TOP_RATED);
                    setTitle(R.string.top_rated_title);
                }
            }
        }else{
            //new state
            fetchSelectedData(POPULAR);
            setTitle(R.string.popular_title);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_KEY, mCurrentState);
        super.onSaveInstanceState(outState);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_popular:
                fetchSelectedData(POPULAR);
                setTitle(R.string.popular_title);
                mCurrentState = STATE_POPULAR;
                return true;
            case R.id.action_top_rated:
                fetchSelectedData(TOP_RATED);
                setTitle(R.string.top_rated_title);
                mCurrentState = STATE_TOP_RATED;
                return true;
            case R.id.action_favorites:
                populateViewFavorite(mMovieEntries);
                setTitle(R.string.favorite_title);
                mCurrentState  = STATE_FAVORITES;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void fetchSelectedData(String data) {
        FetchMovieTask fetchMovieTask = new FetchMovieTask();
        fetchMovieTask.execute(WEB + data);
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent intent = new Intent(this, MovieDescription.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    @Override
    public void onFavoriteClick(final MovieEntry movieEntry) {

        new AlertDialog.Builder(this).setTitle(movieEntry.getMovieTitle()).setMessage(R.string.alert_message)
                .setPositiveButton(R.string.alert_true, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MovieExecutors.getInstance().getDiskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                sMovieDatabase.mMovieDao().delete(movieEntry);
                            }
                        });
                    }
                }).setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).setNeutralButton(R.string.alert_share, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, movieEntry.getMovieTitle());
                intent.setType("text/plain");
                startActivity(intent);
            }
        }).show();
    }

    public void populateView(List<Movie> movies) {
        mMovieAdapter = new MovieAdapter(movies, this);
        mRecyclerView.setAdapter(mMovieAdapter);
    }

    public void populateViewFavorite(List<MovieEntry> movieEntries) {
        mFavoriteAdapter = new FavoriteAdapter(this, movieEntries, this);
        mRecyclerView.setAdapter(mFavoriteAdapter);
    }

    public void favorite() {
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getListLiveData().observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable final List<MovieEntry> movieEntries) {
                mMovieEntries = movieEntries;
                populateViewFavorite(movieEntries);
                setTitle(R.string.favorite_title);
            }
        });
    }

    public class FetchMovieTask extends AsyncTask<String, Void, List<Movie>> {
        URL url;
        List<Movie> movies = null;
        HttpURLConnection httpURLConnection;

        @Override
        protected List<Movie> doInBackground(String... strings) {
            try {
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                InputStream inputStream = httpURLConnection.getInputStream();
                String data = JsonUtils.convertInputStream(inputStream);
                movies = JsonUtils.ParseData(data);
                //Get Trailers
                for (Movie movie : movies) {
                    String id = movie.getMovieId();
                    url = new URL(WEB + id + QUERY_OPTION_VIDEOS + API_KEY + LANGUAGE);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    inputStream = httpURLConnection.getInputStream();
                    data = JsonUtils.convertInputStream(inputStream);
                    List<String> trailer = JsonUtils.ParseTrailers(data);
                    movie.setTrailers(trailer);
                }
                //Get Reviews
                for (Movie movie : movies) {
                    String id = movie.getMovieId();
                    url = new URL(WEB + id + QUERY_OPTION_REVIEW + API_KEY + LANGUAGE);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    inputStream = httpURLConnection.getInputStream();
                    data = JsonUtils.convertInputStream(inputStream);
                    List<Reviewer> reviewers = JsonUtils.ParseReviers(data);
                    movie.setReviewers(reviewers);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movies;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            populateView(movies);
        }
    }
}
