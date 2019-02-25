package io.github.v7lin.iflytek;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

public class MainActivity extends Activity {

    SpeechSynthesizer speechSynthesizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (SpeechUtility.getUtility() == null) {
            // 请勿在“=”与 appid 之间添加任务空字符或者转义符
            SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID + "=" + "5c61057f");
        }

        findViewById(R.id.text_to_speech).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginPlay(getResources().getString(R.string.test_text));
            }
        });
    }

    void beginPlay(String text) {
        if (speechSynthesizer == null) {
            speechSynthesizer = SpeechSynthesizer.createSynthesizer(getApplicationContext(), new IFlyTekInitListener(text));
        } else {
            beginPlayReact(text);
        }
    }

    private final class IFlyTekInitListener implements InitListener {
        private String text;

        IFlyTekInitListener(String text) {
            super();
            this.text = text;
        }

        @Override
        public void onInit(int code) {
            if (code == ErrorCode.SUCCESS) {
                beginPlayReact(text);
            } else {
                Log.e("IFlyTek", "init speak error: " + code);
                getWindow().getDecorView().postDelayed(new InitFailAction(), 300);
            }
        }
    }

    class InitFailAction implements Runnable {

        @Override
        public void run() {
            // 初始化失败
            if (speechSynthesizer != null) {
                speechSynthesizer.destroy();
                speechSynthesizer = null;
            }
            showShortToast(getResources().getString(R.string.iflytek_init_failure));
        }
    }

    void beginPlayReact(String text) {
        if (speechSynthesizer != null) {
            /** 清空参数 **/
            speechSynthesizer.setParameter(SpeechConstant.PARAMS, null);
            /** 设置引擎模式 */
            speechSynthesizer.setParameter(SpeechConstant.ENGINE_MODE, SpeechConstant.MODE_MSC);
            /** 设置引擎类型 **/
            speechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            /** 设置发音人 **/
            speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
            /** 设置语速 **/
            speechSynthesizer.setParameter(SpeechConstant.SPEED, String.valueOf(50));
            /** 设置音调 **/
            speechSynthesizer.setParameter(SpeechConstant.PITCH, String.valueOf(50));
            /** 设置音量，范围 0 - 100 **/
            speechSynthesizer.setParameter(SpeechConstant.VOLUME, String.valueOf(80));
            /** 设置播放器音频流类型 **/
//            speechSynthesizer.setParameter(SpeechConstant.STREAM_TYPE, "3");
            /** 设置播放合成音频打断音乐播放，默认为true **/
            speechSynthesizer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
            /**
             * 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
             * 注：AUDIO_FORMAT参数语记需要更新版本才能生效
             */
//            speechSynthesizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//            speechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.wav");
            try {
                int code = speechSynthesizer.startSpeaking(text, synthesizerListener);
                if (code != ErrorCode.SUCCESS) {
                    Log.e("IFlyTek", "start speak error: " + code);
                    // 初始化失败
                    speechSynthesizer.destroy();
                    speechSynthesizer = null;
                    // 朗读时，把语记卸载了
                    if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                        showShortToast("未安装讯飞语音插件");
                    } else {
                        showShortToast("讯飞语音初始化失败");
                    }
                }
            } catch (Exception ignored) {
                // 华硕 Tooj 使用的是内置的灵犀语音进行朗读，会导致崩溃
                speechSynthesizer.destroy();
                speechSynthesizer = null;
                showShortToast(ignored.getMessage());
            }
        }
    }

    private SynthesizerListener synthesizerListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            // 开始播放
        }

        @Override
        public void onSpeakPaused() {
            // 暂停播放
        }

        @Override
        public void onSpeakResumed() {
            // 继续播放
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            // 合成进度
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度 - progress 最大只会跑到 99
        }

        @Override
        public void onCompleted(SpeechError error) {
            // 播放结束
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            if (SpeechEvent.EVENT_SESSION_ID == eventType) {
                String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
                Log.d("TAG", "session id =" + sid);
            }
        }
    };

    void showShortToast(CharSequence text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechSynthesizer != null) {
            speechSynthesizer.destroy();
            speechSynthesizer = null;
        }
        if (SpeechUtility.getUtility() != null) {
            SpeechUtility.getUtility().destroy();
        }
    }
}
