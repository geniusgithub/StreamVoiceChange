package com.geniusgithub.voicechange.audio;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.soundtouch.common.AsyncResult;
import com.example.soundtouch.common.RegistrantList;
import com.geniusgithub.voicechange.audio.common.AudioConstans;
import com.geniusgithub.voicechange.audio.common.AudioParam;
import com.geniusgithub.voicechange.audio.common.HandleAudioClient;
import com.geniusgithub.voicechange.audio.common.IHandleAudioCallback;
import com.geniusgithub.voicechange.audio.common.TransFormParam;
import com.geniusgithub.voicechange.audio.common.WaveHeader;

import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AudioEngine extends Handler implements IHandleAudioCallback {

    private final static String TAG = AudioEngine.class.getName();

    private final static int MSG_RECORD_STATE = 0x01;
    private volatile boolean mStartFlag = false;

    private RecordAudioClient mRecordAudioClient;
    private HandleAudioClient mHandleAudioClient;

    private BlockingQueue<short[]> mRcordQueue = new LinkedBlockingQueue<short[]>();
    private LinkedList<byte[]> mAudioResultDatas = new LinkedList<>();
    private byte[] mAudioByteDatas;
    private int mAudioDataSize = 0;


    private final RegistrantList mRecordStateChangeRegistrants = new RegistrantList();
    private volatile IHandleAudioCallback mHandleAudioRegistrants;

    private static int FREQUENCY = AudioConstans.FREQUENCY;
    private static int CHANNEL = AudioConstans.PLAY_CHANNEL;
    private static int ENCODING = AudioConstans.ENCODING;
    private SampleAudioPlayer mAudioPlay = new SampleAudioPlayer();

    public AudioEngine() {
        init();
    }

    private void init(){
        mRecordAudioClient = new RecordAudioClient();
        mHandleAudioClient = new HandleAudioClient();
        mRecordAudioClient.registerForRecordStateChanged(this, MSG_RECORD_STATE);
        mHandleAudioClient.registerForHandleCallback(this);
    }

    public void unregisterForRecordStateChanged(Handler h) {
        mRecordStateChangeRegistrants.remove(h);
    }

    public void registerForRecordStateChanged(Handler h, int what) {
        mRecordStateChangeRegistrants.addUnique(h, what, null);
    }


    public void unRegisterForHandleCallback() {
        mHandleAudioRegistrants = null;
    }

    public void registerForHandleCallback(IHandleAudioCallback callback) {
        mHandleAudioRegistrants = callback;
    }


    public void start(TransFormParam transFormParam){
        if (mStartFlag) {
            return ;
        }
        mRcordQueue.clear();
        mAudioResultDatas.clear();
        mAudioDataSize = 0;
        mRecordAudioClient.startRecord(mRcordQueue);
        mHandleAudioClient.startHandleAudio(mRcordQueue, mAudioResultDatas, transFormParam);

        mStartFlag = true;
    }

    public void stop(){
        if (!mStartFlag) {
            return ;
        }

        mRecordAudioClient.stopRecord();
        mHandleAudioClient.stopHandleAudio();

        mStartFlag = false;
    }

    public boolean replayAudioCache(){
        if (mAudioResultDatas.size() == 0) {
            return false;
        }

        AudioParam audioParam = new AudioParam();
        audioParam.mFrequency = FREQUENCY;
        audioParam.mChannelConfig = CHANNEL;
        audioParam.mSampBitConfig = ENCODING;

        mAudioPlay.setAudioParam(audioParam);
        mAudioPlay.prepare();
        mAudioPlay.setDataSource(mAudioByteDatas);

        return mAudioPlay.play();
    }

    public boolean stopReplayAudioCache() {
        return mAudioPlay.release();
    }

    public boolean saveToWAVFile(String filePath) {

        if (mAudioDataSize == 0) {
            return false;
        }

        try {

            WaveHeader header = new WaveHeader(mAudioDataSize);
            byte[] headers = header.getHeader();

            // 保存文件
            FileOutputStream out = new FileOutputStream(filePath);
            out.write(headers);
            out.write(mAudioByteDatas);
            out.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } finally{
        }

        return true;
    }

    public boolean saveToPCMFile(String filePath) {

        if (mAudioDataSize == 0) {
            return false;
        }

        try {
            // 保存文件
            FileOutputStream out = new FileOutputStream(filePath);
            out.write(mAudioByteDatas);
            out.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } finally{
        }

        return true;
    }


    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_RECORD_STATE:
                AsyncResult ar = (AsyncResult)msg.obj;
                handleRecordState(ar);
                break;
        }
    }

    private void handleRecordState(AsyncResult ar){
        int recordState = (Integer) ar.result;
        Log.d(TAG, "handleRecordState recordState = " + recordState);
        mRecordStateChangeRegistrants.notifyResult(recordState);
    }

    @Override
    public void onHandleStart() {
        Log.d(TAG, "onHandleStart");
        if (mHandleAudioRegistrants != null) {
            mHandleAudioRegistrants.onHandleStart();
        }
    }

    @Override
    public void onHandleProcess(byte[] data) {
        Log.d(TAG, "onHandleProcess");
        mAudioDataSize += data.length;
        if (mHandleAudioRegistrants != null) {
            mHandleAudioRegistrants.onHandleProcess(data);
        }
    }

    @Override
    public void onHandleComplete() {
        Log.d(TAG, "onHandleComplete");
        mAudioByteDatas = new byte[mAudioDataSize];
        int offset = 0;
        for(byte[] bytes: mAudioResultDatas){
            System.arraycopy(bytes, 0, mAudioByteDatas, offset, bytes.length);
            offset += bytes.length;
        }
        Log.d(TAG, "mAudioByteDatas.size = " + mAudioDataSize);

        if (mHandleAudioRegistrants != null) {
            mHandleAudioRegistrants.onHandleComplete();
        }
    }
}
