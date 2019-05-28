package hantaro.com.popularmovies;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MovieExecutors {
    private static final Object LOCK = new Object();
    private static MovieExecutors sInstance;
    private final Executor diskIO;

    public MovieExecutors(Executor diskIO) {
        this.diskIO = diskIO;
    }

    public static MovieExecutors getInstance(){
        if(sInstance == null){
            synchronized (LOCK){
                sInstance = new MovieExecutors(Executors.newSingleThreadExecutor());
            }
        }
        return sInstance;
    }

    public Executor getDiskIO() {
        return diskIO;
    }
}
