package hantaro.com.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import java.util.List;

@Dao
public interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MovieEntry movieEntry);

    @Delete
    void delete(MovieEntry movieEntry);

    @Query("SELECT * FROM user_favorites")
    LiveData<List<MovieEntry>> getAll();

    @Query("SELECT * FROM user_favorites WHERE movieID = :id")
    MovieEntry loadEntry(String id);
}
