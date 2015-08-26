package com.ajg.pdf.showpdfthumbnail;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class ThumbnailService extends Service {

    private final IBinder thumbnailBind = new ThumbnailBinder();

    public ThumbnailService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    public class ThumbnailBinder extends Binder {
        public ThumbnailService getService() {
            return ThumbnailService.this;
        }
    }
}
