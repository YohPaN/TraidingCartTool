package com.example.testtextrecognitioncamera;

import android.util.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.*;

public class CardInfoService {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final OkHttpClient client;

    public CardInfoService(OkHttpClient client) {
        this.client = client;
    }

    public JSONObject getCardInfo(String code) throws ExecutionException, InterruptedException {

        String cardInfoUrl = "https://apiyugiho.fallforrising.com/api_ygh/API.php?set_code=".concat(code);

        FutureTask<JSONObject> future = new FutureTask<>(() -> {
            Request request = new Request.Builder().url(cardInfoUrl).build();

            try {
                Response response = client.newCall(request).execute();
                if(response.body() != null) {
                    JSONObject jsonObjectCardInfo = new JSONObject(response.body().string());
                    response.close();
                    return jsonObjectCardInfo.getJSONObject("data");
                } else {
                    throw new IOException("Server not responding or data are null");
                }
            } catch (IOException e) {
                throw new IOException(e);
            }
        });

        executorService.execute(future);

        while(!future.isDone()) {
            Log.i(this.getClass().getName(), "Retrieving card data...");
        }
        Log.i(this.getClass().getName(), "Done...");
        return future.get();
    }
}
