package com.example.gleetta.data;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

public class MediaScanner {
    private Context ctxt;
    private String file_Path;
    private MediaScannerConnection mMediaScanner;
    private MediaScannerConnection.MediaScannerConnectionClient mMediaScannerClient;

    public static MediaScanner newInstance(Context context){
        return new MediaScanner(context);
    }
    private MediaScanner(Context context){
        ctxt = context;
    }
    public void mediaScanning(final String path) {
        if (mMediaScanner == null) {
            mMediaScannerClient = new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onMediaScannerConnected() {
                    mMediaScanner.scanFile(file_Path, null);
                }
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    Log.d("응답", "미디어스캔 성공");
                    mMediaScanner.disconnect();
                }
            };
            mMediaScanner = new MediaScannerConnection(ctxt, mMediaScannerClient);
        }
        file_Path = path;
        mMediaScanner.connect();
    }
}
