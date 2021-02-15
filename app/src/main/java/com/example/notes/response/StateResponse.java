package com.example.notes.response;

import com.google.gson.annotations.SerializedName;

public class StateResponse {

    @SerializedName("state")
    private boolean state;

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
