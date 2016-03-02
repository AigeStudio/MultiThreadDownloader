package cn.aigestudio.downloader.interfaces;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.lang.ref.SoftReference;

/**
 * Created by yjwfn on 16-3-2.
 */
public final class AsyncDListener implements IDListener {

    private static final String EXTRA_ARGS_1 = "extra_args_1";
    private static final String EXTRA_ARGS_2 = "extra_args_2";
    private static final String EXTRA_ARGS_3 = "extra_args_3";

    private static final int OP_PREPARE =   0x0;
    private static final int OP_START   =   0x1;
    private static final int OP_PROGRESS =  0x2;
    private static final int OP_STOP    =   0x3;
    private static final int OP_FINISH  =   0x4;
    private static final int OP_ERROR   =   0x5;

    private final SoftReference<IDListener>   mRef;


    private static final SoftReference<Handler>     mHandlerRef;

    static {
        mHandlerRef = new SoftReference<Handler>(new Handler(){
            @Override
            public void handleMessage(Message msg) {

                @SuppressWarnings("unchecked")
                SoftReference<IDListener> ref = (SoftReference<IDListener>) msg.obj;
                IDListener realListener = ref.get();

                if(realListener == null)
                    return;

                Bundle bundle = msg.getData();
                switch (msg.what){
                    case OP_PREPARE:
                        realListener.onPrepare();
                        break;
                    case OP_START:
                        realListener.onStart(bundle.getString(EXTRA_ARGS_1),
                                bundle.getString(EXTRA_ARGS_2),
                                bundle.getInt(EXTRA_ARGS_3));
                        break;
                    case OP_STOP:
                        realListener.onStop(bundle.getInt(EXTRA_ARGS_1));
                        break;
                    case OP_PROGRESS:
                        realListener.onProgress(bundle.getInt(EXTRA_ARGS_1));
                        break;
                    case OP_FINISH:
                        realListener.onFinish((File) bundle.getSerializable(EXTRA_ARGS_1));
                        break;
                    case OP_ERROR:
                        realListener.onError(bundle.getInt(EXTRA_ARGS_1),
                                bundle.getString(EXTRA_ARGS_3) );
                        break;

                }
            }
        });
    }

    public AsyncDListener(IDListener listener){
        mRef = new SoftReference<>(listener);
    }


    @Override
    public void onPrepare() {
        invokeOnMainThread(OP_START, null);
    }

    @Override
    public void onStart(String fileName, String realUrl, int fileLength) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_ARGS_1, fileName);
        bundle.putString(EXTRA_ARGS_2, realUrl);
        bundle.putInt(EXTRA_ARGS_3, fileLength);
        invokeOnMainThread(OP_START, bundle);
    }

    @Override
    public void onProgress(int progress) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_ARGS_1, progress);
        invokeOnMainThread(OP_PROGRESS, bundle);
    }

    @Override
    public void onStop(int progress) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_ARGS_1, progress);
        invokeOnMainThread(OP_STOP, bundle);
    }

    @Override
    public void onFinish(File file) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_ARGS_1, file);
        invokeOnMainThread(OP_FINISH, bundle);
    }

    @Override
    public void onError(int status, String error) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_ARGS_1, status);
        bundle.putString(EXTRA_ARGS_2, error);
        invokeOnMainThread(OP_ERROR, bundle);
    }


    private void invokeOnMainThread(int what, Bundle data){
        if(mHandlerRef.get() != null) {
            Handler handler = mHandlerRef.get();
            Message message = Message.obtain(handler, what, mRef);
            message.setData(data);
            handler.sendMessage(message);
        }
    }


    public static IDListener  wrap(IDListener listener){
        return listener != null ? new AsyncDListener(listener) : null;
    }
}
