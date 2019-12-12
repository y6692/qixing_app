package com.zxing.lib.scaner;

import android.os.Handler;
import android.os.Message;

import com.google.zxing.Result;
import com.vondear.rxtools.R;
import com.zxing.lib.scaner.activity.ActivityScanerCode;
import com.zxing.lib.scaner.activity.ActivityScanerCode2;


/**
 * @author vondear
 * 描述: 扫描消息转发
 */
public final class CaptureActivityHandler2 extends Handler {

    DecodeThread2 decodeThread = null;
    ActivityScanerCode2 activity = null;
    private State state;

    public CaptureActivityHandler2(ActivityScanerCode2 activity) {
        this.activity = activity;
        decodeThread = new DecodeThread2(activity);
        decodeThread.start();
        state = State.SUCCESS;
        CameraManager.get().startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        if (message.what == R.id.auto_focus) {
            if (state == State.PREVIEW) {
                CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
            }
        } else if (message.what == R.id.restart_preview) {
            restartPreviewAndDecode();
        } else if (message.what == R.id.decode_succeeded) {
            state = State.SUCCESS;
            activity.handleDecode((Result) message.obj);// 解析成功，回调
        } else if (message.what == R.id.decode_failed) {
            state = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        CameraManager.get().stopPreview();
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
        removeMessages(R.id.decode);
        removeMessages(R.id.auto_focus);
    }

    private void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
            CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
        }
    }

    private enum State {
        PREVIEW, SUCCESS, DONE
    }

}
