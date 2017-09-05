package com.example.android.myapplicationmovies;

import retrofit.http.GET;
import retrofit.Callback;



public interface ServiceAPI {
    @GET("/movie/popular")
    void getPopularMovies(Callback<Movie.MovieResult> cb);

    @GET("/movie/top_rated")
    void getTopRatedMovies(Callback<Movie.MovieResult> cb);

}
