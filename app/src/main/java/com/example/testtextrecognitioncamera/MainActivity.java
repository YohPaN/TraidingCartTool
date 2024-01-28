package com.example.testtextrecognitioncamera;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;


import com.example.testtextrecognitioncamera.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;


import org.json.JSONException;

import java.util.concurrent.*;


import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity{


    private ActivityMainBinding binding;

    private ResultViewModel viewModel;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private AllCardSets allCardSets;
    private final OkHttpClient client = new OkHttpClient();

    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if(result) {
                imageAnalyzer();
            }
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ResultViewModel.class);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        allCardSets = new AllCardSets(client);
        try {
            allCardSets.requestGetAllSets();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        imageAnalyzer();

    }

     private void imageAnalyzer() {

        ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(this);

        listenableFuture.addListener(() -> {
            try {

                ProcessCameraProvider cameraProvider = listenableFuture.get();

                Preview preview = new Preview.Builder()
                        .build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

                preview.setSurfaceProvider(binding.cameraPreview.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();


                imageAnalysis.setAnalyzer(AsyncTask.THREAD_POOL_EXECUTOR, new ImageAnalysis.Analyzer() {
                    @Override
                    @ExperimentalGetImage
                    public void analyze(@NonNull ImageProxy imageProxy) {
                        int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                        if(imageProxy.getImage() == null) {
                            return;
                        }
                        Image image = imageProxy.getImage();

                        InputImage inputImage = InputImage.fromMediaImage(image, rotationDegrees);

                        TextRecognizer textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                        if(!viewModel.getCardFindState()) {
//                            Log.v("stateCardFind", viewModel.getCardFindState().toString());
                            Task<Text> result = textRecognizer.process(inputImage)
                                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                                        @Override
                                        public void onSuccess(Text text) {
                                            viewModel.setCardFindState(true);
                                            String finalText = "";
                                            outerLoop: for (Text.TextBlock block : text.getTextBlocks()) {
                                                for (Text.Line line : block.getLines()) {
                                                    for (Text.Element element : line.getElements()) {
                                                        String elementText = element.getText();
                                                        if(elementText.indexOf('-') != -1) {
                                                            Log.v("codeFind", elementText);
                                                            finalText = elementText;
                                                            break outerLoop;
                                                        }
                                                    }
                                                }
                                            }
                                            try {
                                                if(finalText != "") {
                                                    CardInfoController.retrieveCard(finalText, viewModel, fragmentManager, allCardSets, client);
                                                } else {
                                                    viewModel.setCardFindState(false);
                                                }
                                            } catch (JSONException ignored) {
                                            } catch (ExecutionException | InterruptedException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(
                                            new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                }
                                            }
                                    );
                        };
                        imageProxy.close();
                    }
                });

                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

                camera.getCameraControl().setZoomRatio(2);
            }
            catch (ExecutionException | InterruptedException e) {
                Log.e("myMessageError", e.toString());

                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }
}
