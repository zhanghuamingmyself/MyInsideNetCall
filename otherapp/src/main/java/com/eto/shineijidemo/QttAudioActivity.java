package com.eto.shineijidemo;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.qttaudio.sdk.QttAudioEngine;
import com.qttaudio.sdk.QttAudioStream;
import com.qttaudio.sdk.QttCaptureCallbak;
import com.qttaudio.sdk.QttException;



public class QttAudioActivity extends Activity {
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_qtt);
        setTitle("QttAudio");
        testQttAudio();



    }
    QttAudioStream stream;
    void testQttAudio(){
        try {
            //初始化QttAudioEngine
            QttAudioEngine.me().init(this, "8cf5982bd64d47c4036694c2c364d2b8");
            QttAudioEngine.me().enableAudioCompatibilityMode(true);
            //创建QttAudioStream
            stream = QttAudioEngine.me().createStream();
            //设置mic的编码参数,pcm，单声道，48000hz采样率
//            QttAudioEngine.me().setMicCodecParams("pcm", 1, 48000, 0);
            QttAudioEngine.me().setMicCodecParams("opus", 1, 48000, 48000);
            //设置mic采集回调函数
            QttAudioEngine.me().setMicCaptureCb(new QttCaptureCallbak() {
                @Override
                public void onCapture(byte[] buf, int bufLength, Object userdata) {
                    QttAudioStream tmpStream = (QttAudioStream) userdata;
                    //将mic采集到的播放出来，实现音频回放功能
                    tmpStream.playBuffer(buf, 0, bufLength);
                }
            }, stream);

        } catch (QttException e) {
            e.printStackTrace();
        }
    }




    public void onClick(View v){
        int ret = 0;
        switch (v.getId()){
            case R.id.btn01:

//启动stream，开始工作
                log("start");
                ret = stream.start();
                log("start end ret = "+ret);

                break;
            case R.id.btn02:
//停止stream，停止工作
                log("stop");
                stream.stop();
                log("stop end");
                break;
            case R.id.btn03:

                break;
        }
    }


    public void log(String log){
        System.out.println("###>:QttActivity:"+log);
    }


}
