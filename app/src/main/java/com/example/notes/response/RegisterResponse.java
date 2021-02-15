package com.example.notes.response;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {
    @SerializedName("state")
    private boolean state;
    @SerializedName("errors")
    private  String errors;

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }
}
