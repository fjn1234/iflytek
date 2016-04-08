package com.iflytek;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.speech.util.ApkInstaller;

/**
 * Created by Administrator on 2016/1/22.
 */
public class SpeechHelper {
    private String TAG = SpeechHelper.class.getSimpleName();
    private static SpeechHelper speech = null;
    private Context mContext;
    ApkInstaller mInstaller;

    private static SpeechHelper getSpeech() {
        if (speech == null) {
            speech = new SpeechHelper();
        }
        return speech;
    }

    private String voicer = "xiaoyan";
    private String emot = "";
    private SpeechSynthesizer mTts;
    private String mEngineType = "cloud";

    public void doInit(Context context) {
        this.mContext = context;

        this.mTts = SpeechSynthesizer.createSynthesizer(this.mContext, this.mTtsInitListener);
        this.mInstaller = new ApkInstaller((Activity) this.mContext);

    }

    private InitListener mTtsInitListener = new InitListener() {
        public void onInit(int code) {
            Log.d(SpeechHelper.this.TAG, "InitListener init() code = " + code);
            if (code != 0) {
                Log.e(SpeechHelper.this.TAG, "初始化失败,错误码：" + code);
            } else {
                setParam();
            }
        }
    };

    private void setParam() {
        this.mTts.setParameter("params", null);
        if (this.mEngineType.equals("cloud")) {
            this.mTts.setParameter("engine_type", "cloud");

            this.mTts.setParameter("voice_name", this.voicer);
            if (!"neutral".equals(this.emot)) {
                this.mTts.setParameter("emot", this.emot);
            }
            this.mTts.setParameter("speed", "50");

            this.mTts.setParameter("pitch", "50");

            this.mTts.setParameter("volume", "50");
        } else {
            this.mTts.setParameter("engine_type", "local");

            this.mTts.setParameter("voice_name", "");
        }
        this.mTts.setParameter("stream_type", "3");

        this.mTts.setParameter("request_audio_focus", "true");
    }

    public void doSpeak(String text) {
        if (!TextUtils.isEmpty(text)) {
            int code = this.mTts.startSpeaking(text, null);
            if (code != 0) {
                if (code == 21001) {
                    this.mInstaller.install();
                } else {
                    Log.e(this.TAG, "语音合成失败,错误码: " + code);
                }
            }
        }
    }

    public void doPauseSpeaking() {
        this.mTts.pauseSpeaking();
    }

    public void doStopSpeaking() {
        this.mTts.stopSpeaking();
    }

    public void doCheckInstalled() {
        if (!SpeechUtility.getUtility().checkServiceInstalled()) {
            String url = SpeechUtility.getUtility().getComponentUrl();
            Uri uri = Uri.parse(url);
            Intent it = new Intent("android.intent.action.VIEW", uri);
            this.mContext.startActivity(it);
        }
    }

    public static void init(Context context) {
        getSpeech().doInit(context);
    }

    public static void speak(String text) {
        getSpeech().doSpeak(text);
    }

    public static void pauseSpeaking() {
        getSpeech().doPauseSpeaking();
    }

    public static void stopSpeaking() {
        getSpeech().doStopSpeaking();
    }

    public static void checkInstalled() {
        getSpeech().doCheckInstalled();
    }

}
