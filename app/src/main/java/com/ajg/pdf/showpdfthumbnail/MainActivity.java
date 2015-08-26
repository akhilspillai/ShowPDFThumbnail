package com.ajg.pdf.showpdfthumbnail;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private ImageView mIvThumbnail;

    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIvThumbnail = (ImageView) findViewById(R.id.iv_thumbnail);
        webView = new WebView(this);
        webView.setLayoutParams(new ViewGroup.LayoutParams(
                Math.round(300 * getResources().getDisplayMetrics().density),
                Math.round(390 * getResources().getDisplayMetrics().density)));

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setBuiltInZoomControls(true);

        webView.addJavascriptInterface(new JsObjectActivity(MainActivity.this), "injectedObject");
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
//                }, 1000);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            mIvThumbnail.setImageBitmap(b);
        } catch (Exception e) {
            Log.e("Akhil", "Error " + e.toString());
        }
    }

}

class JsObjectActivity {

    MainActivity mMainActivity;

    public JsObjectActivity(MainActivity mainActivity) {
        this.mMainActivity = mainActivity;
    }

    @JavascriptInterface
    public void rendered() {
        mMainActivity.fetchThumbnail();
    }
}
