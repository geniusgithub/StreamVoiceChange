package com.geniusgithub.voicechange.audio.common;

import com.example.soundtouch.common.Utils;
import com.geniusgithub.voicechange.audio.SoundTouchTransFormTool;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class HandleAudioClient {

    private final static String TAG = HandleAudioClient.class.getName();

    private HandleAudioThread mHandleAudioThread;
    private TransFormTool mTransFormTool = new SoundTouchTransFormTool();
    private volatile boolean mHandleAudioComplete = true;
    private volatile IHandleAudioCallback mHandleAudioRegistrants;


    public HandleAudioClient(){

    }

    public void unRegisterForHandleCallback() {
        mHandleAudioRegistrants = null;
    }

    public void registerForHandleCallback(IHandleAudioCallback callback) {
        mHandleAudioRegistrants = callback;
    }

    public boolean startHandleAudio(BlockingQueue<short[]> recordQueue, LinkedList<byte[]> resultData, TransFormParam transFormParam){

        if (mHandleAudioThread != null) {
            return true;
        }

        mHandleAudioThread = new HandleAudioThread(recordQueue, resultData, transFormParam);
        mHandleAudioThread.start();
        mHandleAudioComplete = false;

        return true;
    }

    public boolean stopHandleAudio(){

        if (mHandleAudioThread == null) {
            return true;
        }

        mHandleAudioThread.quitThread();
        mHandleAudioThread = null;
        waitHandleAudioComplete();

        return true;
    }

    private void waitHandleAudioComplete(){
        while (!mHandleAudioComplete){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static final long TIME_WAIT_RECORDING = 100;
    public class HandleAudioThread extends Thread{

        private BlockingQueue<short[]> mRecordQueue;
        private  LinkedList<byte[]> mAudioDatas;
        private volatile boolean mExitFlag = false;
        private TransFormParam mTransFormParam;

        public HandleAudioThread(BlockingQueue<short[]> recordQueue, LinkedList<byte[]> audioDatas, TransFormParam transFormParam) {
            mRecordQueue = recordQueue;
            mAudioDatas = audioDatas;
            mTransFormParam = transFormParam;
        }

        public void quitThread(){
            mExitFlag = true;
        }

        @Override
        public void run(){

            notifyHandleStart();

            mTransFormTool.setSampleRate(mTransFormParam.mSampleRate);//设置声音的采样频率
            mTransFormTool.setChannels(mTransFormParam.mChannel);//设置声音的声道
            mTransFormTool.setPitchSemiTones(mTransFormParam.mNewPitch);//设置声音的pitch
            mTransFormTool.setRateChange(mTransFormParam.mNewRate);//设置声音的速率
            mTransFormTool.setTempoChange(mTransFormParam.mNewTempo); //这个就是传说中的变速不变调


            short[] recordingData;
            while(true)	{

                try{
                    recordingData = mRecordQueue.poll(TIME_WAIT_RECORDING, TimeUnit.MILLISECONDS);

                    if(recordingData != null){

                        mTransFormTool.putSamples(recordingData, recordingData.length);

                        short[] buffer;
                        do {
                            buffer = mTransFormTool.receiveSamples();
                            byte[] bytes = Utils.shortToByteSmall(buffer);
                            if (bytes.length > 0){
                                mAudioDatas.add(bytes);
                                notifyHandleProcess(bytes);
                            }
                        } while (buffer.length > 0);

                    }

                    if(mExitFlag && mRecordQueue.size() == 0) {
                        break;
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            mHandleAudioComplete = true;
            notifyHandleComplete();
        }


        private void notifyHandleStart(){
            if (mHandleAudioRegistrants != null) {
                mHandleAudioRegistrants.onHandleStart();
            }
        }

        private void notifyHandleProcess(byte data[]){
            if (mHandleAudioRegistrants != null) {
                mHandleAudioRegistrants.onHandleProcess(data);
            }

        }

        private void notifyHandleComplete(){
            if (mHandleAudioRegistrants != null) {
                mHandleAudioRegistrants.onHandleComplete();
            }
        }

    }




}
