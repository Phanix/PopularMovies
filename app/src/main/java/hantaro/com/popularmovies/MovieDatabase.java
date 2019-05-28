package hantaro.com.popularmovies;

        import android.arch.persistence.room.Database;
        import android.arch.persistence.room.RoomDatabase;

@Database(entities = MovieEntry.class, version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {

    public static String DATABASE_NAME = "user_db";

    public abstract MovieDao mMovieDao();
}
