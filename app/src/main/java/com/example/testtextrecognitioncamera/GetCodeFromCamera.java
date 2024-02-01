package com.example.testtextrecognitioncamera;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.util.Size;
import androidx.camera.core.*;
import androidx.camera.core.Camera;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import com.example.testtextrecognitioncamera.databinding.ActivityMainBinding;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import com.google.mlkit.vision.text.TextRecognition;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class GetCodeFromCamera {

    private final Context context;
    private final ActivityMainBinding binding;
    private final ResultViewModel viewModel;
    private final LifecycleOwner lifecycleOwner;
    private final CardInfoController cardInfoController;

    public GetCodeFromCamera(Context context, ActivityMainBinding binding, ResultViewModel viewModel, LifecycleOwner lifecycleOwner, CardInfoController cardInfoController) {
        this.context = context;
        this.binding = binding;
        this.viewModel = viewModel;
        this.lifecycleOwner = lifecycleOwner;
        this.cardInfoController = cardInfoController;
    }

    @ExperimentalGetImage
    public void imageAnalyzer() {

        ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(context);

        try {

            listenableFuture.addListener(() -> {
                try {
                    ProcessCameraProvider cameraProvider = listenableFuture.get();


                    ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                            .setTargetResolution(new Size(1280, 720))
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build();
                    bindPreview(cameraProvider, imageAnalysis);

                    ExecutorService executor = Executors.newSingleThreadExecutor();

                    imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
                        @Override
                        @ExperimentalGetImage
                        public void analyze(@NotNull ImageProxy imageProxy) {
                            int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                            if (imageProxy.getImage() == null) {
                                return;
                            }
                            Image image = imageProxy.getImage();
                            InputImage inputImage = InputImage.fromMediaImage(image, rotationDegrees);
                            TextRecognizer textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                            if(Boolean.FALSE.equals(viewModel.getCardFindState())) {
                                processTextRecognizer(textRecognizer, inputImage);
                            }
                            imageProxy.close();
                        }
                    });

                } catch (ExecutionException | InterruptedException e) {
                    Log.e(this.getClass().getName(), e.toString());
                    Thread.currentThread().interrupt();
                }
            }, ContextCompat.getMainExecutor(context));
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.toString());
        }
    }


    private String extractCodeFromText(Text text) {
        for (Text.TextBlock block : text.getTextBlocks()) {
            for (Text.Line line : block.getLines()) {
                for (Text.Element element : line.getElements()) {
                    String elementText = element.getText();
                    if (elementText.indexOf('-') != -1) {
                        Log.v(this.getClass().getName(), elementText);
                        return elementText;
                    }
                }
            }
        }
        return null;
    }

    private void processTextRecognizer(TextRecognizer textRecognizer, InputImage inputImage) {
        textRecognizer.process(inputImage)
                .addOnSuccessListener(text -> {
                    viewModel.setCardFindState(true);

                    String finalResult = extractCodeFromText(text);

                    if (!"".equals(finalResult) && finalResult != null) {
                        Log.i(this.getClass().getName(), "Text recognize: " + finalResult);
                        try {
                            cardInfoController.retrieveCard(finalResult);
                        } catch (JSONException | InterruptedException | ExecutionException e) {
                            Log.e(this.getClass().getName(), e.toString());
                            viewModel.setCardFindState(false);
                            Thread.currentThread().interrupt();
                        }
                    } else {
                        viewModel.setCardFindState(false);
                    }
                })
                .addOnFailureListener(e -> Log.i(this.getClass().getName(), "text not recognized"));
    }

    private void bindPreview(ProcessCameraProvider cameraProvider, ImageAnalysis imageAnalysis) {
        Preview preview = new Preview.Builder().build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

        preview.setSurfaceProvider(binding.cameraPreview.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis);

        camera.getCameraControl().setZoomRatio(3);
    }

}
