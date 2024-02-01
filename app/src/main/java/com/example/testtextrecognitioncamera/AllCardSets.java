package com.example.testtextrecognitioncamera;

import android.util.Log;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.IOException;
import java.util.concurrent.*;

public class AllCardSets {

    private final OkHttpClient client;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private JSONArray jsonArrayAllSets;

    public AllCardSets(OkHttpClient client) {
        this.client = client;
    }

    public JSONArray getJsonArrayAllSets() {
        return jsonArrayAllSets;
    }

    public void requestGetAllSets() {
        try {
            FutureTask<JSONArray> future = getJsonObjectFutureTask();

            executorService.execute(future);

            while (!future.isDone()) {
                Log.i(this.getClass().getName(), "Calculating...");
            }
            Log.i(this.getClass().getName(), "Done");
            jsonArrayAllSets = future.get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(this.getClass().getName(), e.toString());
            Thread.currentThread().interrupt();
        }


    }

    @NotNull
    private FutureTask<JSONArray> getJsonObjectFutureTask() {
        String setsApiUrl = "https://apiyugiho.fallforrising.com/api_ygh/set_api.php";

        return new FutureTask<>(new Callable<JSONArray>() {
            @Override
            public JSONArray call() throws Exception {
                Request request = new Request.Builder().url(setsApiUrl).build();

                try {
                    Response response = client.newCall(request).execute();
                    if(response.body() != null) {
                        JSONObject jsonObjectCardInfo = new JSONObject(response.body().string());
                        response.close();
                        return jsonObjectCardInfo.getJSONArray("data");
                    } else {
                        throw new IOException();
                    }

                } catch (IOException e) {
                    throw new IOException(e);
                }
            }
        });
    }
}
