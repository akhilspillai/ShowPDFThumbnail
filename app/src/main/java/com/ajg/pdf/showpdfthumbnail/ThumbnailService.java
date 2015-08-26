package com.ajg.pdf.showpdfthumbnail;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.FileOutputStream;

public class ThumbnailService extends Service {

    private final IBinder mThumbnailBind = new ThumbnailBinder();
    private WebView webView;

    private OnThumbnailCreateListener mListener;

    public ThumbnailService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mThumbnailBind;
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        webView = new WebView(this);
        webView.setLayoutParams(new ViewGroup.LayoutParams(
                Math.round(300 * getResources().getDisplayMetrics().density),
                Math.round(390 * getResources().getDisplayMetrics().density)));

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setBuiltInZoomControls(true);

        webView.addJavascriptInterface(
                new JsObject(ThumbnailService.this), "injectedObject");

        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.setInitialScale(70);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("file:///android_asset/pdfviewer/index.html");
        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(final WebView view, String url) {

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        fetchThumbnail();
//                    }
//                }, 2000);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    public void setThumbnailCreateListener(OnThumbnailCreateListener listener) {
        this.mListener = listener;

    }

    public class ThumbnailBinder extends Binder {
        public ThumbnailService getService() {
            return ThumbnailService.this;
        }
    }

    public interface OnThumbnailCreateListener {
        void thumbnailCreated(Bitmap b);
    }

    public void fetchThumbnail() {

        Bitmap b = Bitmap.createBitmap(
                Math.round(300 * getResources().getDisplayMetrics().density),
                Math.round(390 * getResources().getDisplayMetrics().density),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        webView.draw(c);

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(
                    Environment.getExternalStorageDirectory()+"/thumbnail_new.jpg");
            if (fos != null) {
                b = Bitmap.createScaledBitmap(b, 100, 130,
                        true);
                b.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                fos.close();
            }
            if (mListener != null) {
                mListener.thumbnailCreated(b);
            }
        } catch (Exception e) {
            Log.e("Akhil", "Error " + e.toString());
        }
    }

}


class JsObject {

    ThumbnailService mThumbnailService;

    public JsObject(ThumbnailService thumbnailService) {
        this.mThumbnailService = thumbnailService;
    }

    @JavascriptInterface
    public void rendered() {
        mThumbnailService.fetchThumbnail();
    }
}


