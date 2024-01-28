package com.example.testtextrecognitioncamera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ExperimentalGetImage;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;

import com.example.testtextrecognitioncamera.databinding.ActivityMainBinding;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity{

    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private final OkHttpClient client = new OkHttpClient();

    @Override
    @ExperimentalGetImage
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ResultViewModel viewModel = new ViewModelProvider(this).get(ResultViewModel.class);
        com.example.testtextrecognitioncamera.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        AllCardSets allCardSets = new AllCardSets(client);
        CardInfoController cardInfoController;
        allCardSets.requestGetAllSets();

        cardInfoController = new CardInfoController(viewModel, fragmentManager, allCardSets, client);

        LifecycleOwner lifecycleOwner = this;

        TextRecognition textRecognition = new TextRecognition(this, binding, viewModel, lifecycleOwner, cardInfoController);
        cardInfoController.startCameraAndAnalyze(textRecognition);
    }
}
