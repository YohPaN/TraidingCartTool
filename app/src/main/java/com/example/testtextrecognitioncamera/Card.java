package com.example.testtextrecognitioncamera;

import org.json.JSONException;
import org.json.JSONObject;


public class Card {

    private final String id;
    private final String name;
    private final CardSet cardSet;
    private final CardPrices cardPrices;

    public Card(JSONObject cardInfo) throws JSONException {
        this.id = cardInfo.getString("id");
        this.name = cardInfo.getString("name");
        this.cardSet = new CardSet(cardInfo.getJSONObject("card_sets"));
        this.cardPrices = new CardPrices(cardInfo.getJSONArray("card_prices").getJSONObject(0));
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CardSet getCardSet() {
        return cardSet;
    }

    public CardPrices getCardPrices() {
        return cardPrices;
    }
}
