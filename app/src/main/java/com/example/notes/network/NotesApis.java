package com.example.notes.network;

import com.example.notes.request.AddNoteRequest;
import com.example.notes.request.LoginRequest;
import com.example.notes.request.RegisterRequest;
import com.example.notes.response.LoginResponse;
import com.example.notes.response.NotesResponse;
import com.example.notes.response.RegisterResponse;
import com.example.notes.response.StateResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NotesApis {

    @POST("public/api/register")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);

    @POST("public/api/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("public/api/notes")
    Call<NotesResponse> getNotes(@Header("Authorization") String token);

    @POST("public/api/notes/add")
    Call<StateResponse> addNotes(@Header("Authorization") String token, @Body AddNoteRequest addNoteRequest);

    @GET("public/api/notes/delete/{noteId}")
    Call<StateResponse> deleteNotes( @Path("noteId") int noteId,@Header("Authorization") String token);

    @POST("public/api/notes/edit/{noteId}")
    Call<StateResponse> editNotes(@Header("Authorization") String token, @Body AddNoteRequest addNoteRequest,@Path("noteId") int noteId);

}
