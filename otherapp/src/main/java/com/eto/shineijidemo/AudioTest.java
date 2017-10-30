package com.eto.shineijidemo;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import com.eto.udpvideo.mamager.LogUtils;
import com.eto.udpvideo.mamager.VideoCall;
import com.eto.udpvideo.message.AudioMessage;
import com.eto.udpvideo.message.Utils;
import com.eto.udpvideo.sender.SendAudioThread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2017/8/3.
 */

public class AudioTest {

    static  int Port = 6152;
    DatagramSocket socket;
    InetAddress serverAddress ;
    String mIp;
    byte[] messageBuf = new byte[1024*64];
    byte[] recBuf = new byte[1024*64];
    static AudioTest mAudioUtils;
    boolean isRunning;


    SendAudioThread mSendThread;

    int mCallStatus = 0; //呼叫状态
    static Context mContext;


    VideoCall.CallStateListener mCallStateListener;

    //


    private AudioTest(){
    }




    public static AudioTest getInstance(){
        if (mAudioUtils == null){
            mAudioUtils = new AudioTest();

        }
        return  mAudioUtils;
    }

    //--------------------------QttAudio----------------------------







    ////录音//////////////////////

    boolean isRecording = false;
    AudioRecord audioRecord;
    boolean isPlaying = false;
    AudioTrack audioTrack;

    int frequency = 16000; //采样率
    int bufferSizeRecord = AudioRecord.getMinBufferSize(frequency, AudioFormat.CHANNEL_IN_MONO,  AudioFormat.ENCODING_PCM_16BIT)*2;
    int bufferSizePlay = AudioTrack.getMinBufferSize(frequency, AudioFormat.CHANNEL_OUT_MONO,  AudioFormat.ENCODING_PCM_16BIT)*2;

    //在线程调用
    public void startRecord() {
        if(isRecording){
            LogUtils.logTxt("is already start...... ");
            return;
        }
        if(bufferSizeRecord < 2048){
            bufferSizeRecord = 4096;
        }
        if(bufferSizePlay < 4096){
            bufferSizePlay = 4096;
        }
        LogUtils.logTxt("startRecord"+",bufferSizeRecord="+bufferSizeRecord+",bufferSizePlay="+bufferSizePlay);
        try {
            if (audioRecord == null) {
                audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                        frequency, AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, bufferSizeRecord);

            }

            //定义缓冲
            byte[] buffer = new byte[bufferSizeRecord];

            audioRecord.startRecording();
            isRecording = true ;

            LogUtils.logTxt("initDenoise:"+"bufferSizeRecord="+bufferSizeRecord);
            while (isRecording) {
                int bufferReadResult = audioRecord.read(buffer, 0, bufferSizeRecord);


                if(bufferReadResult == bufferSizeRecord){
                    play(buffer);

                }else{

                    if(bufferReadResult > 0) {
                        byte[] buf = new byte[bufferReadResult];
                        System.arraycopy(buffer, 0, buf, 0, bufferReadResult);
                        play(buf);
                    }
                }



            }

        }catch (Exception e){
            log("start Record fail"+e.getMessage());
            LogUtils.logTxt("start Record fail");
           // SpeexDspUtils.destoryDenoise();
           // LogUtils.logTxt("destoryDenoise()");
            stopRecord();
        }





    }

    public  void stopRecord(){
        LogUtils.logTxt("stopRecord");
        try {
            isRecording = false;
            if(audioRecord != null){
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void play(byte[] buf) {
        log("rec buf size = "+buf.length);
    try{

            if(audioTrack == null){
                startPlay();
            }
            audioTrack.write(buf, 0, buf.length);

        } catch (Exception e) {
            log("play Failed");
            stopPlay();
        }
    }

     void startPlay(){
         LogUtils.logTxt("startPlay");
        log("startPlay");
        try{
            if(audioTrack == null) {
                audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
                        AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSizePlay, AudioTrack.MODE_STREAM);
            }
            // Start playback
            audioTrack.play();
        } catch (Exception e) {
            log("startPlay Failed"+e.getMessage());
            stopPlay();
        }
    }

    public void stopPlay(){
//        isPlaying = false;
        LogUtils.logTxt("stopPlay");
        if(audioTrack != null){
            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
        }
    }

    public void setDefaultVolume(Context context,int volume){
        mContext = context;
        try {
            AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            call_Vol = volume;
            if(call_Vol > maxVolume){
                call_Vol =  maxVolume;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static  int bak_Vol = 6; //保存音量
    public  static  int call_Vol =6;
    public  void  setCallVolume(){

        try {
            AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
//        //最大音量
//        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//        //当前音量
//        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            bak_Vol =mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, call_Vol, 0); //tempVolume:音量绝对值

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void resumeVolume(){

        try {
            AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, bak_Vol, 0); //tempVolume:音量绝对值
        } catch (Exception e){

        }
    }





     public void log(String log){
        System.out.println("###>:AudioUtils:"+log);
    }




}
