package com.geniusgithub.voicechange;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soundtouch.common.AsyncResult;
import com.example.soundtouch.common.Utils;
import com.geniusgithub.voicechange.audio.AudioEngine;
import com.geniusgithub.voicechange.audio.NetworkClient;
import com.geniusgithub.voicechange.audio.NetworkReceiver;
import com.geniusgithub.voicechange.audio.common.IHandleAudioCallback;
import com.geniusgithub.voicechange.audio.common.RecordState;
import com.geniusgithub.voicechange.audio.common.TransFormParam;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

public class MainRecordActivity extends Activity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener, View.OnTouchListener, IHandleAudioCallback {

    private final static String TAG = MainRecordActivity.class.getName();
    private RadioGroup mRadioGroup;

    private EditText mEt_sample_rate;
    private EditText mEt_channels;
    private EditText mEt_pitch_semi_tones;
    private EditText mEt_rate_change;
    private EditText mEt_tempo_change;

    private Button mBtnRecord;
    private Button mBtnPlay;
    private Button mBtnSave;
    private Button mBtnJump;

    private TextView mTvRecord;
    private CheckBox mCheckBoxPlaying;

    private final static int MSG_RECORD_STATE = 0x01;
    private Handler mRecordStateHandler;

    private NetworkClient mNetworkClient;
    private NetworkReceiver mNetworkReceiver;
    private AudioEngine mAudioEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_layout);

        initView();
        initLogic();
    }

    private void initView(){
        mRadioGroup = findViewById(R.id.radioGroup);
        mRadioGroup.setOnCheckedChangeListener(this);
        RadioButton rbCustom = findViewById(R.id.radioButtonCustom);
        rbCustom.setChecked(true);
        mCheckBoxPlaying = findViewById(R.id.checkBoxPlaying);
        mCheckBoxPlaying.setChecked(true);

        mEt_sample_rate = findViewById(R.id.et_sample_rate);
        mEt_sample_rate.setEnabled(false);
        mEt_channels = findViewById(R.id.et_channel);
        mEt_channels.setEnabled(false);
        mEt_pitch_semi_tones = findViewById(R.id.et_pitch);
        mEt_rate_change = findViewById(R.id.et_ratch);
        mEt_tempo_change = findViewById(R.id.et_tempo_change);

        mBtnRecord = findViewById(R.id.btnRecord);
        mBtnRecord.setOnTouchListener(this);
        mBtnPlay = findViewById(R.id.btnPlay);
        mBtnPlay.setOnClickListener(this);
        mBtnSave = findViewById(R.id.btnSave);
        mBtnSave.setOnClickListener(this);
        mBtnJump = findViewById(R.id.btnJump);
        mBtnJump.setOnClickListener(this);

        mTvRecord = findViewById(R.id.tv_recordState);


    }

    private void initLogic(){

        initAudioEngine();

        requestPermisson();
    }

    private void requestPermisson (){
        RxPermissions rxPermission = new RxPermissions(this);
        rxPermission.request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) {
                        if (granted) {
                            Log.d(TAG, "granted success");
                        } else {
                            Toast.makeText(MainRecordActivity.this, "请授予相关权限再使用该应用",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }

    private void initAudioEngine() {

        mRecordStateHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.i(TAG, "msg.what = " + msg.what );
                switch (msg.what) {
                    case MSG_RECORD_STATE:
                        AsyncResult ar = (AsyncResult)msg.obj;
                        handleRecordState(ar);
                        break;

                    default:
                        break;
                }
            }

        };

        mAudioEngine = new AudioEngine();
        mAudioEngine.registerForRecordStateChanged(mRecordStateHandler, MSG_RECORD_STATE);
        mAudioEngine.registerForHandleCallback(this);

        mNetworkClient = new NetworkClient();
        mNetworkReceiver = new NetworkReceiver();
        mNetworkReceiver.init();
        mNetworkClient.connectNetworkService(mNetworkReceiver);
    }

    private void unInitAudioEngine() {
        mAudioEngine.stopReplayAudioCache();
        mAudioEngine.unregisterForRecordStateChanged(mRecordStateHandler);
        mAudioEngine.unRegisterForHandleCallback();
        mNetworkClient.disConnectNetworkService();
        mNetworkReceiver.unInit();
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        unInitAudioEngine();
    }


/*    float[] pitch = {0, 10F, 7.0F, 7.0F, -11.0F, 0.0F, -8.0F, 7.8F, 0.0F, 5F, 12F, -10F, -5, 0, -15};
    float[] rate = {0, -0.7f, 20.5F, -12.0F, -2.0F, 50.0F, -1.7F, 1.0F, -50.0F, 12, 5, 5, -30, -50, -10};
    float[] tempo = {0, 0.5f, -9.0F, -15.0F, 20.0F, 50.0F, -3.6F, 0.0F, 50.0F, 10, 15, 50, 50, 100, 60};*/
    private float[] pitch = {5F, 12F, -10F, -5, 0, -15};
    private float[] rate = {12, 5, 5, -30, -50, -10};
    private float[] tempo = {10, 15, 50, 50, 100, 60};


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        enableEditText(false);
        switch (checkedId){
            case R.id.radioButtonCustom:
                enableEditText(true);
                updateAudioParamUI(0, 0, 0);
                break;
            case R.id.radioButton1:
                updateAudioParamUI(pitch[0], rate[0], tempo[0]);
                break;
            case R.id.radioButton2:
                updateAudioParamUI(pitch[1], rate[1], tempo[1]);
                break;
            case R.id.radioButton3:
                updateAudioParamUI(pitch[2], rate[2], tempo[2]);
                break;
            case R.id.radioButton4:
                updateAudioParamUI(pitch[3], rate[3], tempo[3]);
                break;
            case R.id.radioButton5:
                updateAudioParamUI(pitch[4], rate[4], tempo[4]);
                break;
            case R.id.radioButton6:
                updateAudioParamUI(pitch[5], rate[5], tempo[5]);
                break;
        }
    }

    private void enableEditText(boolean enable){
        mEt_pitch_semi_tones.setEnabled(enable);
        mEt_rate_change.setEnabled(enable);
        mEt_tempo_change.setEnabled(enable);
    }

    private void updateAudioParamUI(float pitch, float rate, float tempo){
        mEt_pitch_semi_tones.setText(pitch + "");
        mEt_rate_change.setText(rate + "");
        mEt_tempo_change.setText(tempo + "");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnPlay:
                playCacheAudio();
                break;
            case R.id.btnSave:
                saveToFile();
                break;
            case R.id.btnJump:
                btnJumpToPlayUI();
                break;
        }
    }

    private void playCacheAudio(){
        mAudioEngine.replayAudioCache();
    }

    private void saveToFile(){

        boolean ret = mAudioEngine.saveToPCMFile(Utils.localExternalPath + "/soundtouch.pcm");
        if (ret) {
            Toast.makeText(this, "保存成功:" + Utils.localExternalPath + "/soundtouch.pcm", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this, "保存失败:" + Utils.localExternalPath + "/soundtouch.pcm", Toast.LENGTH_SHORT).show();
        }

        mAudioEngine.saveToWAVFile(Utils.localExternalPath + "/soundtouch.wav");
    }

    public void btnJumpToPlayUI() {
        Intent intent = new Intent();
        intent.setClass(this, AudioPlayerActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(v.getId()) {
            case R.id.btnRecord:
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        startRecord();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_OUTSIDE:
                    case MotionEvent.ACTION_UP:
                        stopRecord();
                        break;
                    default:
                }
                break;
        }
        return false;
    }

    private void startRecord(){
        mAudioEngine.start(getTransFormParam());
    }

    private void stopRecord() {
        mAudioEngine.stop();
    }


    private TransFormParam getTransFormParam(){
        TransFormParam transFormParam = new TransFormParam();
        String newPitch = mEt_pitch_semi_tones.getText().toString();
        transFormParam.mSampleRate= (int) Float.parseFloat(TextUtils.isEmpty(newPitch) ? "0" : newPitch);
        String newRate = mEt_rate_change.getText().toString();
        transFormParam.mNewRate = Float.parseFloat(TextUtils.isEmpty(newRate) ? "0" : newRate);
        String newTempo = mEt_tempo_change.getText().toString();
        transFormParam.mNewTempo = Float.parseFloat(TextUtils.isEmpty(newTempo) ? "0" : newTempo);
        return transFormParam;
    }


    private void handleRecordState(AsyncResult ar){
        int recordState = (Integer) ar.result;
        switch (recordState){
            case RecordState.MSG_RECORDING_START:
                mTvRecord.setText("录音中...");
                break;
            case RecordState.MSG_RECORDING_STOP:
                mTvRecord.setText("");
                break;
            case RecordState.MSG_RECORDING_STATE_ERROR:
                mTvRecord.setText("录音异常");
                break;

        }
    }

    @Override
    public void onHandleStart() {

    }

    @Override
    public void onHandleProcess(byte[] data) {
        if (mCheckBoxPlaying.isChecked()) {
            mNetworkClient.sendAudio(data);
        }
    }

    @Override
    public void onHandleComplete() {

    }
}
