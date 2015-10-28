package cn.aigestudio.downloader.demo;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.io.File;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.SimpleDListener;

/**
 * 执行下载的Service
 *
 * @author AigeStudio 2015-05-18
 */
public class DLService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra("url");
        String path = intent.getStringExtra("path");
        final int id = intent.getIntExtra("id", -1);
        final NotificationManager nm = (NotificationManager) getSystemService(Context
                .NOTIFICATION_SERVICE);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher);

        DLManager.getInstance(this).dlStart(url, path, new SimpleDListener() {
            @Override
            public void onStart(String fileName, String realUrl, int fileLength) {
                super.onStart(fileName, realUrl, fileLength);
                builder.setContentTitle(fileName);
            }

            @Override
            public void onProgress(int progress) {
                builder.setProgress(100, progress, false);
                nm.notify(id, builder.build());
            }

            @Override
            public void onFinish(File file) {
                nm.cancel(id);
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
