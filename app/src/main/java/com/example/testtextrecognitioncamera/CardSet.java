package com.example.testtextrecognitioncamera;

import org.json.JSONException;
import org.json.JSONObject;

public class CardSet {

    private final String setName;
    private final String setRarity;
    private final String setRarityCode;
    private final String setPrice;

    public CardSet(JSONObject cardSet) throws JSONException {
        this.setName = cardSet.getString("set_name");
        this.setRarity = cardSet.getString("set_rarity");
        this.setRarityCode = cardSet.getString("set_rarity_code");
        this.setPrice = cardSet.getString("set_price");
    }

    public String getSetName() {
        return setName;
    }

    public String getSetRarity() {
        return setRarity;
    }

    public String getSetRarityCode() {
        return setRarityCode;
    }

    public String getSetPrice() {
        return setPrice;
    }
}
