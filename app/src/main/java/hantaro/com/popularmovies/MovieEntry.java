package hantaro.com.popularmovies;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "user_favorites")
public class MovieEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String movieID;
    private String movieTitle;
    private String imageId;

    public MovieEntry(String movieID, String title, String imageId) {
        this.imageId = imageId;
        this.movieID = movieID;
        this.movieTitle = title;
    }

    public MovieEntry(){ }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }
}
