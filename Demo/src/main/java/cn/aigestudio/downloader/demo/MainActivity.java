package cn.aigestudio.downloader.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.DLTaskListener;

public class MainActivity extends Activity {
    private static final String[] URLS = {
            "http://xiazai.zol.com.cn/down.php?nn=f4afd4327335fd3a1&softid=325408&subcateid=103&site=10&server=10&rand=3331525",
//            "http://dlsw.baidu.com/sw-search-sp/soft/94/23191/BaiduWubiSetup-1_2_0_13.1401780898.exe",
            "http://dlsw.baidu.com/sw-search-sp/soft/4f/20605/BaiduType_Setup_Light.1413528566.exe",
            "http://dlsw.baidu.com/sw-search-sp/soft/4e/16671/freewb10th.322658870.exe",
            "http://dlsw.baidu.com/sw-search-sp/soft/50/15251/zzbihua_V7.0.7.4_Setup.1422241616.exe",
            "http://dlsw.baidu.com/sw-search-sp/soft/7b/33461/freeime.1406862029.exe",
            "http://dlsw.baidu.com/sw-search-sp/soft/46/16696/jpwb2015.2.2.10.1423557071.exe"
    };

    private static final int[] RES_ID_BTN_START = {
            R.id.main_dl_start_btn1,
            R.id.main_dl_start_btn2,
            R.id.main_dl_start_btn3,
            R.id.main_dl_start_btn4,
            R.id.main_dl_start_btn5,
            R.id.main_dl_start_btn6};
    private static final int[] RES_ID_BTN_STOP = {
            R.id.main_dl_stop_btn1,
            R.id.main_dl_stop_btn2,
            R.id.main_dl_stop_btn3,
            R.id.main_dl_stop_btn4,
            R.id.main_dl_stop_btn5,
            R.id.main_dl_stop_btn6};
    private static final int[] RES_ID_PB = {
            R.id.main_dl_pb1,
            R.id.main_dl_pb2,
            R.id.main_dl_pb3,
            R.id.main_dl_pb4,
            R.id.main_dl_pb5,
            R.id.main_dl_pb6};
    private static final int[] RES_ID_NOTIFY = {
            R.id.main_notify_btn1,
            R.id.main_notify_btn2,
            R.id.main_notify_btn3,
            R.id.main_notify_btn4,
            R.id.main_notify_btn5,
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
            final int finalI = i;
            btnStarts[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DLManager.getInstance(MainActivity.this).dlStart(URLS[finalI], saveDir,
                            new DLTaskListener() {
                                @Override
                                public void onProgress(int progress) {
                                    pbDLs[finalI].setProgress(progress);
                                }
                            });
                }
            });
        }

        Button[] btnStops = new Button[RES_ID_BTN_STOP.length];
        for (int i = 0; i < btnStops.length; i++) {
            btnStops[i] = (Button) findViewById(RES_ID_BTN_STOP[i]);
            final int finalI = i;
            btnStops[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DLManager.getInstance(MainActivity.this).dlStop(URLS[finalI]);
                }
            });
        }

        pbDLs = new ProgressBar[RES_ID_PB.length];
        for (int i = 0; i < pbDLs.length; i++) {
            pbDLs[i] = (ProgressBar) findViewById(RES_ID_PB[i]);
            pbDLs[i].setMax(100);
        }

        Button[] btnNotifys = new Button[RES_ID_NOTIFY.length];
        for (int i = 0; i < btnNotifys.length; i++) {
            btnNotifys[i] = (Button) findViewById(RES_ID_NOTIFY[i]);
            final int finalI = i;
            btnNotifys[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NotificationUtil.notificationForDLAPK(MainActivity.this, URLS[finalI]);
                }
            });
        }

        saveDir = Environment.getExternalStorageDirectory() + "/AigeStudio/";
    }
}
