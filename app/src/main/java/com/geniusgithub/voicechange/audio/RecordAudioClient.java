package com.geniusgithub.voicechange.audio;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

import com.example.soundtouch.common.RegistrantList;
import com.geniusgithub.voicechange.audio.common.AudioConstans;
import com.geniusgithub.voicechange.audio.common.RecordState;

import java.util.concurrent.BlockingQueue;

public class RecordAudioClient {

    private final static String TAG = RecordAudioClient.class.getName();
    private final RegistrantList mRecordStateChangeRegistrants = new RegistrantList();
    private RecordThread mRecrodThread;
    private volatile boolean mRecordAudioComplete = true;

    public RecordAudioClient(){

    }

    public void unregisterForRecordStateChanged(Handler h) {
        mRecordStateChangeRegistrants.remove(h);
    }

    public void registerForRecordStateChanged(Handler h, int what) {
        mRecordStateChangeRegistrants.addUnique(h, what, null);
    }


    public boolean startRecord(BlockingQueue<short[]> recordQueue){

        if (mRecrodThread != null) {
            return true;
        }

        mRecrodThread = new RecordThread(recordQueue);
        mRecrodThread.start();
        mRecordAudioComplete = false;

        return true;
    }


    public boolean stopRecord(){

        if (mRecrodThread == null) {
            return true;
        }

        mRecrodThread.quitThread();
        mRecrodThread = null;
        waitRecordAudioComplete();

        return true;
    }

    private void waitRecordAudioComplete(){
        while (!mRecordAudioComplete){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private static int FREQUENCY = AudioConstans.FREQUENCY;
    private static int CHANNEL = AudioConstans.RECORD_CHANNEL;
    private static int ENCODING = AudioConstans.ENCODING;
    private static int bufferSize = AudioRecord.getMinBufferSize(FREQUENCY, CHANNEL, ENCODING);
    public class RecordThread extends Thread{

        private BlockingQueue<short[]> mRecordQueue;
        private volatile boolean mExitFlag = false;

        public RecordThread(BlockingQueue<short[]> recordQueue){
            mRecordQueue = recordQueue;
        }

        public void quitThread(){
            mExitFlag = true;
        }

        @Override
        public void run(){

            AudioRecord audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC, FREQUENCY,
                    CHANNEL, ENCODING, bufferSize);

            int state = audioRecord.getState();
            if (state == AudioRecord.STATE_INITIALIZED) {

                short[] buffer = new short[bufferSize];
                boolean flag = true;
                audioRecord.startRecording();
                notifyRecordStart();

                while (!mExitFlag) {

                    int len = audioRecord.read(buffer, 0, buffer.length);
                    Log.d(TAG, "audioRecord read len = " + len);
                    // 去掉全0数据
                    if(flag){

                        double sum = 0.0;
                        for(int i = 0; i < len; i++){
                            sum += buffer[i];
                        }

                        if(sum == 0.0){
                            continue;
                        }else{
                            flag = false;
                        }
                    }

                    short[] data = new short[len];
                    System.arraycopy(buffer, 0, data, 0, len);
                    mRecordQueue.add(data);
                }

                audioRecord.release();
                notifyRecordStop();

            } else{
                notifyRecordError();
            }

            mRecordAudioComplete = true;
        }


        private void notifyRecordStart(){
            mRecordStateChangeRegistrants.notifyResult(RecordState.MSG_RECORDING_START);
        }

        private void notifyRecordStop(){
            mRecordStateChangeRegistrants.notifyResult(RecordState.MSG_RECORDING_STOP);
        }

        private void notifyRecordError(){
            mRecordStateChangeRegistrants.notifyResult(RecordState.MSG_RECORDING_STATE_ERROR);
        }


    }



}


