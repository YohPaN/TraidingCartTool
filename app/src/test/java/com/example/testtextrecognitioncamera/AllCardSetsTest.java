package com.example.testtextrecognitioncamera;

import okhttp3.OkHttpClient;
import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class AllCardSetsTest {

    private AllCardSets allCardSets;

    @Before
    public void initialization() {
        allCardSets = new AllCardSets(new OkHttpClient());
    }

    @Test
    public void testRequestGetAllSets() throws ExecutionException, InterruptedException {
        allCardSets.requestGetAllSets();
        JSONArray jsonArray = allCardSets.getJsonArrayAllSets();
        Assert.assertNotEquals(0, jsonArray.length());
    }
}
