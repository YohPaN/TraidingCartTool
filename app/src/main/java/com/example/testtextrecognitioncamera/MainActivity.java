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
import androidx.fragment.app.FragmentTransaction;
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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.util.concurrent.*;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity{


    JSONArray jsonArrayAllSets;
    private ActivityMainBinding binding;

    private ResultViewModel viewModel;
    private FragmentManager fragmentManager = getSupportFragmentManager();

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


        RequestGetAllSets();

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
                                                    CardInfoController.retrieveCard(finalText, jsonArrayAllSets, viewModel, fragmentManager);
                                                } else {
                                                    viewModel.setCardFindState(false);
                                                }
                                            } catch (JSONException ignored) {
                                            } catch (ExecutionException e) {
                                                throw new RuntimeException(e);
                                            } catch (InterruptedException e) {
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

    public void RequestGetAllSets() {
        OkHttpClient client = new OkHttpClient();
        String sets_api_url = "https://apiyugiho.fallforrising.com/api_ygh/set_api.php";

        Request request = new Request.Builder().url(sets_api_url).build();
        client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (response.body() != null) {
                                    JSONObject jsonObjectAllSets = new JSONObject(response.body().string());
                                    jsonArrayAllSets = jsonObjectAllSets.getJSONArray("data");
                                    System.out.println("Retriving data finish");
                                }
                            } catch (IOException | JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
            });
    }

}
