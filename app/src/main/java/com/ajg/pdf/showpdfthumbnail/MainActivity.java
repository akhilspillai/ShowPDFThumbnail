package com.ajg.pdf.showpdfthumbnail;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity
        implements ThumbnailService.OnThumbnailCreateListener{

    private ImageView mIvThumbnail;
    private ThumbnailService mThumbNailService;

    private ServiceConnection mThumbnailConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ThumbnailService.ThumbnailBinder binder = (ThumbnailService.ThumbnailBinder)service;
            mThumbNailService = binder.getService();

            mThumbNailService.setThumbnailCreateListener(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mThumbNailService.setThumbnailCreateListener(MainActivity.this);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIvThumbnail = (ImageView) findViewById(R.id.iv_thumbnail);
        Intent thumbnailIntent = new Intent(MainActivity.this, ThumbnailService.class);
        bindService(thumbnailIntent, mThumbnailConnection, Context.BIND_AUTO_CREATE);
        startService(thumbnailIntent);
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

    @Override
    public void thumbnailCreated(final Bitmap b) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIvThumbnail.setImageBitmap(b);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        unbindService(mThumbnailConnection);
//        mThumbNailService.stopSelf();
    }
}
