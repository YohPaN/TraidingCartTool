package com.example.testtextrecognitioncamera;

import android.util.Log;
import android.widget.Toast;

import org.chromium.net.CronetException;
import org.chromium.net.UrlResponseInfo;
import org.json.JSONArray;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

class MyUrlCallbackRequest extends org.chromium.net.UrlRequest.Callback {

    private static final String TAG = "MyUrlRequestCallback";
    private static final String TAG2 = "MyResponse";


    @Override
    public void onRedirectReceived(org.chromium.net.UrlRequest request, UrlResponseInfo info, String newLocationUrl) {
        request.followRedirect();
    }

    @Override
    public void onResponseStarted(org.chromium.net.UrlRequest request, UrlResponseInfo info) {
        int httpCodeStatus = info.getHttpStatusCode();
        if(httpCodeStatus == 200) {
            request.read(ByteBuffer.allocateDirect(50000000));
//            Toast toast = Toast.makeText(request.)
        } else {
            request.read(ByteBuffer.allocateDirect(102400));
        }
    }

    @Override
    public void onReadCompleted(org.chromium.net.UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) {


        ///JUST TO SEE THE RESPONSE IN LOG BUT NOT OBLIGATE

        byteBuffer.flip(); // Prepare the buffer for reading

        byte[] byteArray = new byte[byteBuffer.remaining()];
        byteBuffer.get(byteArray);

        String responseString = new String(byteArray, Charset.forName("UTF-8"));

        // Log the response
        Log.d(TAG2, responseString);





        byteBuffer.clear();
        request.read(byteBuffer);
    }

    @Override
        public void onSucceeded(org.chromium.net.UrlRequest request, UrlResponseInfo info) {
            Log.v(TAG, "Success message");
        }

    @Override
    public void onFailed(org.chromium.net.UrlRequest request, UrlResponseInfo info, CronetException error) {
        Log.e(TAG, "There is an error: " + error + ". More information with: " + info);
    }

}


