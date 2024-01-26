package com.example.testtextrecognitioncamera;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONObject;

public class ResultViewModel extends ViewModel {
    private final MutableLiveData<Card> cardMutable = new MutableLiveData<>();
    private Boolean cardFindState = false;

    public void card(Card card) {
        cardMutable.setValue(card);
    }

    public void setCardFindState(Boolean state) {
        Log.v("stateCardFind", state.toString());
        cardFindState = state;
    }

    public Boolean getCardFindState() {
        return cardFindState;
    }

    public LiveData<Card> returnCardJson() {
        return cardMutable;
    }
}
