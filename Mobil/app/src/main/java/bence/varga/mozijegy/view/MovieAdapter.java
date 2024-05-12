package bence.varga.mozijegy.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import bence.varga.mozijegy.R;
import bence.varga.mozijegy.model.Movie;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> implements Filterable {
    private ArrayList<Movie> filmdata;
    private ArrayList<Movie> filmdataAll;
    private Context context;
    private int lastPos = -1;

    public MovieAdapter(Context context, ArrayList<Movie> movieData) {
        this.filmdata = movieData;
        this.filmdataAll = movieData;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_movies, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.ViewHolder holder, int position) {
        Movie currentMovie = filmdata.get(position);

        holder.bindTo(currentMovie);

        if(holder.getAdapterPosition()>lastPos){
            Animation animation = AnimationUtils.loadAnimation(context,R.anim.sliderow);
            holder.itemView.startAnimation(animation);
            lastPos = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {return filmdata.size();}

    @Override
    public Filter getFilter() {
        return shoppingFilter;
    }

    private Filter shoppingFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Movie> filteredList =new ArrayList<>();
            FilterResults res = new FilterResults();

            if(constraint == null || constraint.length() == 0){
                res.count = filmdataAll.size();
                res.values = filmdataAll;
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Movie item : filmdataAll){
                    if(item.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
                res.count = filteredList.size();
                res.values = filteredList;

            }

            return res;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filmdata = (ArrayList<Movie>) results.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView info;
        private TextView genre;
        private ImageView movieImg;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.movieTitleTv);
            info = itemView.findViewById(R.id.movieDescTv);
            movieImg = itemView.findViewById(R.id.movieImg);
            genre = itemView.findViewById(R.id.movieGenreTv);

            itemView.findViewById(R.id.movieDateListBtn).setOnClickListener(v -> {
                // Intent inicializálása az átmenethez
                Intent intent = new Intent(context, IdopontokActivity.class);
                String selectedMovieName = filmdata.get(getAdapterPosition()).getName();
                intent.putExtra("movie_name", selectedMovieName);

                // Az Intent indítása
                context.startActivity(intent);
            });
        }

        public void bindTo(Movie currentMovie) {
            title.setText(currentMovie.getName());
            info.setText(currentMovie.getInfo());
            genre.setText(currentMovie.getGenre());

            Glide.with(context).load(currentMovie.getImageRes()).into(movieImg);


        }
    }
}

