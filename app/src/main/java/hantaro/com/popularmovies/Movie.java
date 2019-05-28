package hantaro.com.popularmovies;

import java.io.Serializable;
import java.util.List;

public class Movie implements Serializable {

    private String mTitle;
    private String mImageId;
    private String mOverview;
    private String mVoteAverage;
    private String mReleaseDate;
    private String mMovieId;
    private List<String> mTrailers;
    private List<Reviewer> mReviewers;

    public Movie(String title, String imageId, String overview, String releaseDate, String voteAverage, String movieId) {
        mTitle = title;
        mImageId = imageId;
        mOverview = overview;
        mReleaseDate = releaseDate;
        mVoteAverage = voteAverage;
        mMovieId = movieId;
    }

    public List<Reviewer> getReviewers() {
        return mReviewers;
    }

    public void setReviewers(List<Reviewer> reviewers) {
        mReviewers = reviewers;
    }

    public List<String> getTrailers() {
        return mTrailers;
    }

    public void setTrailers(List<String> trailers) {
        mTrailers = trailers;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getImageId() {
        return mImageId;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getMovieId() {
        return mMovieId;
    }
}
