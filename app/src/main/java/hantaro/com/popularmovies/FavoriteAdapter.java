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

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteAdapterViewHolder> {

    public FavoriteClickListener mFavoriteClickListener;

    Context mContext;
    List<MovieEntry> mMovieEntries;

    public FavoriteAdapter(Context context, List<MovieEntry> movieEntries, FavoriteClickListener favoriteClickListener) {
        mContext = context;
        mMovieEntries = movieEntries;
        mFavoriteClickListener = favoriteClickListener;
    }

    @NonNull
    @Override
    public FavoriteAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.movie, viewGroup, false);
        return new FavoriteAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapterViewHolder favoriteAdapterViewHolder, int i) {
        favoriteAdapterViewHolder.bind(mMovieEntries.get(i));
    }

    @Override
    public int getItemCount() {
        if (mMovieEntries == null) {
            return 0;
        }
        return mMovieEntries.size();
    }

    public interface FavoriteClickListener {
        void onFavoriteClick(MovieEntry movieEntry);
    }

    public class FavoriteAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImageView;

        public FavoriteAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv_movie_poster);
            itemView.setOnClickListener(this);
        }

        public void bind(MovieEntry movieEntry) {
            Picasso.with(mContext).load(movieEntry.getImageId()).into(mImageView);
        }

        @Override
        public void onClick(View view) {
            mFavoriteClickListener.onFavoriteClick(mMovieEntries.get(getAdapterPosition()));
        }
    }
}
