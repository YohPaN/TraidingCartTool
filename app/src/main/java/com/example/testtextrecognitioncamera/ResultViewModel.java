package com.example.testtextrecognitioncamera;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONObject;

public class ResultViewModel extends ViewModel {
    private final MutableLiveData<JSONObject> json = new MutableLiveData<>();

    public void cardJson(JSONObject jsonObject) {
        json.setValue(jsonObject);
    }

    public LiveData<JSONObject> returnCardJson() {
        return json;
    }
}
