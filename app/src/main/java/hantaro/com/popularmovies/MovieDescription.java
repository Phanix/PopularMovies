package hantaro.com.popularmovies;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDescription extends AppCompatActivity implements View.OnClickListener {

    private final String YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    TextView mMovieTitle;
    TextView mMovieDescription;
    ImageView mPoster;
    TextView mVoteAverage;
    TextView mReleaseData;
    Button mFavoriteButton;
    String mMovieId;
    Movie mMovie;
    String mMovieTitleString;
    boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_description);

        mMovieDescription = findViewById(R.id.tv_description);
        mMovieTitle = findViewById(R.id.tv_title);
        mPoster = findViewById(R.id.iv_poster);

        mVoteAverage = findViewById(R.id.tv_vote_average);
        mReleaseData = findViewById(R.id.tv_year);
        mFavoriteButton = findViewById(R.id.bt_favorite);
        mFavoriteButton.setOnClickListener(this);
        mMovie = null;
        Intent intent = getIntent();
        mMovieId = "";
        mMovieTitleString = "";
        if (intent.hasExtra("movie")) {

            mMovie = (Movie) intent.getSerializableExtra("movie");
            mMovieId = mMovie.getMovieId();
            MovieExecutors.getInstance().getDiskIO().execute(new Runnable() {
                @Override
                public void run() {
                    if (MainActivity.sMovieDatabase.mMovieDao().loadEntry(mMovieId) != null) {
                        isFavorite = true;
                    } else {
                        isFavorite = false;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setFavoriteButton();
                        }
                    });
                }
            });
            setTitle(mMovie.getTitle());
            mMovieTitle.setText(mMovie.getTitle());
            mMovieDescription.setText(mMovie.getOverview());
            mVoteAverage.setText(mMovie.getVoteAverage());
            mReleaseData.setText(mMovie.getReleaseDate());
            //Set Movie Poster
            Picasso.with(this).load(mMovie.getImageId()).into(mPoster);
            setFavoriteButton();
            LinearLayout listView = findViewById(R.id.movie_description_layout);

            //Inflate a list of trailers
            for (int i = 0; i < mMovie.getTrailers().size(); i++) {

                ImageView imageView = new ImageView(this);
                imageView.setImageResource(android.R.drawable.ic_media_play);
                imageView.setColorFilter(Color.BLACK);

                TextView textView = new TextView(this);
                textView.setText(getString(R.string.trailer) + " " + (i + 1));
                textView.setTextSize(25);

                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setPadding(16, 16, 16, 16);
                linearLayout.setBackgroundResource(R.drawable.list_trailer_selector);
                linearLayout.setClickable(true);
                linearLayout.setOnClickListener(this);
                linearLayout.setTag(mMovie.getTrailers().get(i));
                linearLayout.addView(imageView);
                linearLayout.addView(textView);
                listView.addView(linearLayout);
            }

            //Check if has any review
            if (mMovie.getReviewers() != null) {
                TextView reviewTextView = new TextView(this);
                reviewTextView.setTextColor(Color.GRAY);
                reviewTextView.setTypeface(null, Typeface.BOLD);
                reviewTextView.setText(getString(R.string.reviews) + " : ");
                reviewTextView.setTextSize(30);
                listView.addView(reviewTextView);
            }
            //Inflate a list of Reviewers
            for (Reviewer reviewer : mMovie.getReviewers()) {
                TextView author = new TextView(this);
                TextView content = new TextView(this);
                author.setText(reviewer.getName());
                author.setTextSize(27);
                author.setTextColor(Color.BLUE);
                author.setTypeface(null, Typeface.BOLD);
                author.setAllCaps(true);
                content.setTextSize(25);
                content.setText(reviewer.getContent());

                listView.addView(author);
                listView.addView(content);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view instanceof LinearLayout) {
            String tag = (String) view.getTag();
            Log.i("Tag", tag);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_URL + tag));
            startActivity(intent);
        } else if (view instanceof Button) {
            MovieExecutors.getInstance().getDiskIO().execute(new Runnable() {
                @Override
                public void run() {
                    if (isFavorite) {
                        MovieEntry movieEntry = MainActivity.sMovieDatabase.mMovieDao().loadEntry(mMovieId);
                        MainActivity.sMovieDatabase.mMovieDao().delete(movieEntry);
                        isFavorite = false;
                    } else {
                        MainActivity.sMovieDatabase.mMovieDao().insert(new MovieEntry(mMovieId, mMovie.getTitle(), mMovie.getImageId()));
                        isFavorite = true;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setFavoriteButton();
                        }
                    });
                }
            });
        }
    }

    private void setFavoriteButton() {
        if (isFavorite) {
            mFavoriteButton.setBackgroundColor(Color.YELLOW);
        } else {
            mFavoriteButton.setBackgroundColor(Color.BLUE);
        }
    }
}
