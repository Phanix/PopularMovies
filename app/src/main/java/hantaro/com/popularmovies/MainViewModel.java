package hantaro.com.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<MovieEntry>> mListLiveData;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mListLiveData = MainActivity.sMovieDatabase.mMovieDao().getAll();
    }

    public LiveData<List<MovieEntry>> getListLiveData() {
        return mListLiveData;
    }
}
