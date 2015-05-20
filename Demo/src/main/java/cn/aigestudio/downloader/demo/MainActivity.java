package cn.aigestudio.downloader.demo;

import android.app.Activity;
import android.app.Notification;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.DLTaskListener;

public class MainActivity extends Activity {
    private static final String URL1 = "http://183.230.95.76:9999/bcs.91.com/pcsuite-dev/apk/c684f54c3f941cd71baef085e2ac8662.apk";
    private static final String URL2 = "http://bcs.apk.r1.91.com/data/upload/apkres/2015/5_17/13/com.tencent.mobileqq_015020349.apk";
    private static final String URL3 = "http://dlsw.baidu.com/sw-search-sp/soft/9e/12035/BaiduHi_V4.7.1.2_setup.1429175376.exe";
    private static final String URL4 = "http://dlsw.baidu.com/sw-search-sp/soft/a2/12282/SinaUC_Release_8.3.4.22616.1396945592.exe";
    private static final String URL5 = "http://dlsw.baidu.com/sw-search-sp/soft/4b/17170/Install_WLMessenger14.0.8117.416.1393467029.exe";
    private static final String URL6 = "http://dlsw.baidu.com/sw-search-sp/soft/a2/25705/sinaSHOW-v1-1.1395901693.dmg";

    private static final int[] RES_ID_BTN_START = {R.id.main_dl_start_btn1, R.id.main_dl_start_btn2,
            R.id.main_dl_start_btn3, R.id.main_dl_start_btn4, R.id.main_dl_start_btn5,
            R.id.main_dl_start_btn6};
    private static final int[] RES_ID_BTN_STOP = {R.id.main_dl_stop_btn1, R.id.main_dl_stop_btn2,
            R.id.main_dl_stop_btn3, R.id.main_dl_stop_btn4, R.id.main_dl_stop_btn5,
            R.id.main_dl_stop_btn6};
    private static final int[] RES_ID_PB = {R.id.main_dl_pb1, R.id.main_dl_pb2, R.id.main_dl_pb3,
            R.id.main_dl_pb4, R.id.main_dl_pb5, R.id.main_dl_pb6};
    private static final int[] RES_ID_NOTIFY = {R.id.main_notify_btn1, R.id.main_notify_btn2,
            R.id.main_notify_btn3, R.id.main_notify_btn4, R.id.main_notify_btn5,
            R.id.main_notify_btn6};

    private String saveDir;

    private ProgressBar[] pbDLs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button[] btnStarts = new Button[RES_ID_BTN_START.length];
        for (int i = 0; i < btnStarts.length; i++) {
            btnStarts[i] = (Button) findViewById(RES_ID_BTN_START[i]);
            btnStarts[i].setOnClickListener(new MainClickListener());
        }

        Button[] btnStops = new Button[RES_ID_BTN_STOP.length];
        for (int i = 0; i < btnStops.length; i++) {
            btnStops[i] = (Button) findViewById(RES_ID_BTN_STOP[i]);
            btnStops[i].setOnClickListener(new MainClickListener());
        }

        pbDLs = new ProgressBar[RES_ID_PB.length];
        for (int i = 0; i < pbDLs.length; i++) {
            pbDLs[i] = (ProgressBar) findViewById(RES_ID_PB[i]);
            pbDLs[i].setMax(100);
            pbDLs[i].setOnClickListener(new MainClickListener());
        }

        Button[] btnNotifys = new Button[RES_ID_NOTIFY.length];
        for (int i = 0; i < btnNotifys.length; i++) {
            btnNotifys[i] = (Button) findViewById(RES_ID_NOTIFY[i]);
            btnNotifys[i].setOnClickListener(new MainClickListener());
        }

        saveDir = Environment.getExternalStorageDirectory() + "/AigeStudio/";
    }

    private class MainClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.main_dl_start_btn1:
                    DLManager.getInstance(MainActivity.this).dlStart(URL1, saveDir,
                            new DLTaskListener() {
                                @Override
                                public void onProgress(int progress) {
                                    pbDLs[1].setProgress(progress);
                                }
                            });
                    break;
                case R.id.main_dl_stop_btn1:
                    DLManager.getInstance(MainActivity.this).dlStop(URL1);
                    break;
                case R.id.main_dl_start_btn2:
                    DLManager.getInstance(MainActivity.this).dlStart(URL2, saveDir,
                            new DLTaskListener() {
                                @Override
                                public void onProgress(int progress) {
                                    pbDLs[2].setProgress(progress);
                                }
                            });
                    break;
                case R.id.main_dl_stop_btn2:
                    DLManager.getInstance(MainActivity.this).dlStop(URL2);
                    break;
                case R.id.main_dl_start_btn3:
                    DLManager.getInstance(MainActivity.this).dlStart(URL3, saveDir,
                            new DLTaskListener() {
                                @Override
                                public void onProgress(int progress) {
                                    pbDLs[3].setProgress(progress);
                                }
                            });
                    break;
                case R.id.main_dl_stop_btn3:
                    DLManager.getInstance(MainActivity.this).dlStop(URL3);
                    break;
                case R.id.main_dl_start_btn4:
                    DLManager.getInstance(MainActivity.this).dlStart(URL4, saveDir,
                            new DLTaskListener() {
                                @Override
                                public void onProgress(int progress) {
                                    pbDLs[4].setProgress(progress);
                                }
                            });
                    break;
                case R.id.main_dl_stop_btn4:
                    DLManager.getInstance(MainActivity.this).dlStop(URL4);
                    break;
                case R.id.main_dl_start_btn5:
                    DLManager.getInstance(MainActivity.this).dlStart(URL5, saveDir,
                            new DLTaskListener() {
                                @Override
                                public void onProgress(int progress) {
                                    pbDLs[5].setProgress(progress);
                                }
                            });
                    break;
                case R.id.main_dl_stop_btn5:
                    DLManager.getInstance(MainActivity.this).dlStop(URL5);
                    break;
                case R.id.main_dl_start_btn6:
                    DLManager.getInstance(MainActivity.this).dlStart(URL6, saveDir,
                            new DLTaskListener() {
                                @Override
                                public void onProgress(int progress) {
                                    pbDLs[6].setProgress(progress);
                                }
                            });
                    break;
                case R.id.main_dl_stop_btn6:
                    DLManager.getInstance(MainActivity.this).dlStop(URL6);
                    break;
                case R.id.main_notify_btn1:
                    NotificationUtil.notificationForDLAPK(MainActivity.this, URL1);
                    break;
                case R.id.main_notify_btn2:
                    NotificationUtil.notificationForDLAPK(MainActivity.this, URL2);
                    break;
                case R.id.main_notify_btn3:
                    NotificationUtil.notificationForDLAPK(MainActivity.this, URL3);
                    break;
                case R.id.main_notify_btn4:
                    NotificationUtil.notificationForDLAPK(MainActivity.this, URL4);
                    break;
                case R.id.main_notify_btn5:
                    NotificationUtil.notificationForDLAPK(MainActivity.this, URL5);
                    break;
                case R.id.main_notify_btn6:
                    NotificationUtil.notificationForDLAPK(MainActivity.this, URL6);
                    break;
            }
        }
    }
}
