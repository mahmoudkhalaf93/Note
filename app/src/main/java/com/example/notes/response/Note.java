package com.example.notes.response;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
@Entity(tableName = "notes")
public class Note {
    @ColumnInfo(name = "idOffline")
    @PrimaryKey(autoGenerate = true)
    private  int idOffline;
    @SerializedName("id")
    private  int id;
    @ColumnInfo(name = "title")
    @SerializedName("title")
    private String title;
    @ColumnInfo(name = "body")
    @SerializedName("body")
    private String body;
    @ColumnInfo(name = "uploadedState")
    private Boolean uploadedState;




    public Boolean getUploadedState() {
        return uploadedState;
    }

    public void setUploadedState(Boolean uploadedState) {
        this.uploadedState = uploadedState;
    }

    public int getId() {
        return id;
    }

    public int getIdOffline() {
        return idOffline;
    }

    public void setIdOffline(int idOffline) {
        this.idOffline = idOffline;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
