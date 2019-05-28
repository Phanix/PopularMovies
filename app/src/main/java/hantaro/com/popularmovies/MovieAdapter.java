package hantaro.com.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    List<Movie> mMovies;
    Context mContext;
    private MovieClickListener mMovieClickListener;

    public MovieAdapter(List<Movie> movies, MovieClickListener movieClickListener) {
        mMovies = movies;
        mMovieClickListener = movieClickListener;
    }

    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.movie, viewGroup, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapterViewHolder movieAdapterViewHolder, int i) {
        movieAdapterViewHolder.bind(mMovies.get(i));
    }

    @Override
    public int getItemCount() {
        if (mMovies == null)
            return 0;
        return mMovies.size();
    }

    public interface MovieClickListener {
        void onMovieClick(Movie movie);
    }

    protected class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImageView;

        public MovieAdapterViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            mImageView = view.findViewById(R.id.iv_movie_poster);
        }

        public void bind(Movie movie) {
            Picasso.with(mContext).load(movie.getImageId()).into(mImageView);
        }

        @Override
        public void onClick(View view) {
            mMovieClickListener.onMovieClick(mMovies.get(getAdapterPosition()));
        }
    }
}
