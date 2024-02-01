package com.example.testtextrecognitioncamera;

import android.util.Log;
import androidx.camera.core.ExperimentalGetImage;
import androidx.fragment.app.FragmentManager;
import okhttp3.OkHttpClient;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

public class CardInfoController {

    private final AllCardSets allCardSets;
    private final FragmentManager fragmentManager;
    private final ResultViewModel viewModel;
    private final OkHttpClient client;

    public CardInfoController(ResultViewModel viewModel, FragmentManager fragmentManager, AllCardSets allCardSets, OkHttpClient client) {
        this.viewModel = viewModel;
        this.fragmentManager = fragmentManager;
        this.allCardSets = allCardSets;
        this.client = client;
    }

    @ExperimentalGetImage
    public void startCameraAndAnalyze(GetCodeFromCamera textRecognition) {
        try {
            textRecognition.imageAnalyzer();
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.toString());
        }
    }

    public void retrieveCard(String code) throws JSONException, ExecutionException, InterruptedException {
        CardCode cardCode= new CardCode(code);
        CardInfoService cardInfoService = new CardInfoService(client);

        if (cardCode.verifyCode(allCardSets)) {
            Card card;
            try {
                card= new Card(cardInfoService.getCardInfo(cardCode.getCode()));
                DisplayService.displayDataCard(card, viewModel, fragmentManager);
            } catch (JSONException e) {
                Log.e(this.getClass().getName(), "Error while retrieving JSON data card ! Error: " + e);
                throw new JSONException(e.toString());
            }
        } else {
            Log.i(this.getClass().getName(), "invalid code !");
            viewModel.setCardFindState(false);
        }
    }
}
