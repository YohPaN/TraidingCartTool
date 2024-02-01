package com.example.testtextrecognitioncamera;

import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

public class CardCodeTest {
    private CardCode cardCodeWithIInIdentifier;
    private CardCode cardCodeWithOInIdentifier;
    private CardCode cardCodeWithNumberInLang;
    private CardCode cardCodeWithTInIdentifier;
    private JSONArray jsonArray;

    @Before
    public void initilisationCardCode() {
        cardCodeWithIInIdentifier = new CardCode("BLMR-FRI00");
        cardCodeWithOInIdentifier = new CardCode("BLMR-FR1O0");
        cardCodeWithNumberInLang = new CardCode("BLMR-F1100");
        cardCodeWithTInIdentifier = new CardCode("BLMR-FRT00");
    }

    @Test
    public void testFixIdentifier() {
        String cardCodeWithIInIdentifierFixed = cardCodeWithIInIdentifier.getCode();
        Assert.assertEquals("BLMR-FR100", cardCodeWithIInIdentifierFixed);

        String cardCodeWithOInIdentifierFixed = cardCodeWithOInIdentifier.getCode();
        Assert.assertEquals("BLMR-FR100", cardCodeWithOInIdentifierFixed);

        String cardCodeWithTInIdentifierFixed = cardCodeWithTInIdentifier.getCode();
        Assert.assertEquals("BLMR-FR100", cardCodeWithTInIdentifierFixed);

    }

    @Test
    public void testFixLang() {
        String cardCodeWithNumberInLangFixed = cardCodeWithNumberInLang.getCode();
        Assert.assertEquals("BLMR-FI100", cardCodeWithNumberInLangFixed);
    }
}
