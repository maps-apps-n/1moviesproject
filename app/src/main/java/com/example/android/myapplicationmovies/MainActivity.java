package com.example.android.myapplicationmovies;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rv;
    private List<Movie> movList;
    private MoviesAdapter movieadapter;
    public static final String LOG_TAG = MoviesAdapter.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         rv = (RecyclerView) findViewById(R.id.recycler);
         rv.setLayoutManager(new GridLayoutManager(this, 2));
         movieadapter = new MoviesAdapter(this);
         rv.setAdapter(movieadapter);
         checkSortOrder();
    }

    private void getPopularMovies() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.themoviedb.org/3")
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addEncodedQueryParam("api_key", "KEY_GOES_HERE");
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        ServiceAPI service = restAdapter.create(ServiceAPI.class);
        service.getPopularMovies(new Callback<Movie.MovieResult>() {
            @Override
            public void success(Movie.MovieResult movieResult, Response response) {
                movieadapter.setMovieList(movieResult.getResults());
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void getTopRatedMovies() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.themoviedb.org/3")
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addEncodedQueryParam("api_key", "KEY_GOES_HERE");
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        ServiceAPI service = restAdapter.create(ServiceAPI.class);
        service.getTopRatedMovies(new Callback<Movie.MovieResult>() {
            @Override
            public void success(Movie.MovieResult movieResult, Response response) {
                movieadapter.setMovieList(movieResult.getResults());
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_menu:
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkSortOrder(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = preferences.getString(
                this.getString(R.string.sortby_key),
                this.getString(R.string.pop_movies_option)
        );
        if (sortOrder.equals(this.getString(R.string.pop_movies_option))) {
            Log.d(LOG_TAG, "Popular Movies");
            getPopularMovies();
        }
        else{
            Log.d(LOG_TAG, "Top Movies");
            getTopRatedMovies();
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        if (movList == null){
            checkSortOrder();
        }else{

            checkSortOrder();
        }
    }




    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        public ImageView images;
        public MovieViewHolder(View itemView) {
            super(itemView);
            images = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
    public static class MoviesAdapter extends RecyclerView.Adapter<MovieViewHolder> {
        private List<Movie> movList;
        private LayoutInflater mInflater;
        private Context mContext;

        public MoviesAdapter(Context context) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public MovieViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
            View view = mInflater.inflate(R.layout.imageview_main, parent, false);
            final MovieViewHolder vh = new MovieViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = vh.getAdapterPosition();
                    Intent intent = new Intent(mContext, MovieInfo.class);
                    intent.putExtra(MovieInfo.EXTRA_MOVIE, movList.get(position));
                    mContext.startActivity(intent);
                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(MovieViewHolder holder, int position) {
            Movie movie = movList.get(position);
            Picasso.with(mContext)
                    .load(movie.getPoster())
                    .placeholder(R.color.colorAccent)
                    .into(holder.images);
        }

        @Override
        public int getItemCount() {
            return (movList == null) ? 0 : movList.size();
        }

        public void setMovieList(List<Movie> movieList) {
            this.movList = new ArrayList<>();
            this.movList.addAll(movieList);
            notifyDataSetChanged();
        }
    }

    /* Check internet connection -
    https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
