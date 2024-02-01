package com.example.testtextrecognitioncamera;

import org.json.JSONException;
import org.json.JSONObject;

public class CardPrices {

    private String cardmarketPrice;
    private String tcgplayerPrice;
    private String ebayPrice;
    private String amazonPrice;
    private String coolstuffincPrice;

    public CardPrices(JSONObject cardPrices) throws JSONException {
        this.cardmarketPrice = cardPrices.getString("cardmarket_price");
        this.tcgplayerPrice = cardPrices.getString("tcgplayer_price");
        this.ebayPrice = cardPrices.getString("ebay_price");
        this.amazonPrice = cardPrices.getString("amazon_price");
        this.coolstuffincPrice = cardPrices.getString("coolstuffinc_price");
    }
}
