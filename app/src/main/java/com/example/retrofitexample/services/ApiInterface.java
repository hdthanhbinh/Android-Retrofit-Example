package com.example.retrofitexample.services;

import com.example.retrofitexample.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiInterface {

    @GET("users/")
    Call<List<User>> getAllUsers();

    @PUT("users/{id}/")
    Call<User> setUserById(@Path("id") int id, @Body User user);

    @DELETE("users/{id}/")
    Call<User> deleteUserById(@Path("id") int id);

    @POST("users/")
    Call<User> addUser(@Body User user);
}
