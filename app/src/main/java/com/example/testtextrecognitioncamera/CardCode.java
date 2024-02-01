package com.example.testtextrecognitioncamera;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Objects;

public class CardCode {

    private final String extension;
    private final String lang;
    private final String identifier;
    private final String code;

    public CardCode(String code) {
        int indexOfDash = code.indexOf('-');
        extension = code.substring(0, indexOfDash);
        if(code.length() < indexOfDash + 3) {
            lang =  "";
            identifier = "";
        } else {
            lang = fixLang(code.substring(indexOfDash + 1, indexOfDash + 3));
            identifier = fixIdentifier(code.substring(indexOfDash + 3));
        }
        this.code = extension.concat("-").concat(lang).concat(identifier);
    }

    public String getCode() {
        return code;
    }

    public boolean verifyCode(AllCardSets retrieveCardSetsService) throws JSONException {
        JSONArray jsonOfSetCode = retrieveCardSetsService.getJsonArrayAllSets();
        if(!lang.matches("^[A-Z]{2}")) {
            return false;
        }
        if(!identifier.matches("^\\d{3}")) {
            return false;
        }
        for(int i=0; i< jsonOfSetCode.length(); i++) {
            if(Objects.equals(jsonOfSetCode.getString(i), extension)) {
                return true;
            }
        }
        return false;
    }

    private String fixIdentifier(String identifier) {
        if(identifier.contains("I")) {
            identifier = identifier.replace("I", "1");
        }
        if(identifier.contains("O")) {
            identifier = identifier.replace("O", "0");
        }
        if(identifier.contains("T")) {
            identifier = identifier.replace("T", "1");
        }
        return identifier;
    }

    private String fixLang(String lang) {
        if(lang.contains("1")) {
            lang = lang.replace("1", "I");
        }
        if(lang.contains("0")) {
            lang = lang.replace("0", "O");
        }
        return lang;
    }
}
