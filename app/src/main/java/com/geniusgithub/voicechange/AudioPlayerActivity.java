package com.geniusgithub.voicechange;


import android.app.Activity;
import android.media.AudioFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.soundtouch.common.Utils;
import com.geniusgithub.voicechange.audio.SampleAudioPlayer;
import com.geniusgithub.voicechange.audio.common.AudioConstans;
import com.geniusgithub.voicechange.audio.common.AudioParam;
import com.geniusgithub.voicechange.audio.common.PlayState;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class AudioPlayerActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	private final static String TAG = "AudioPlayerDemoActivity";
	private TextView mTextViewState;				// 播放状态

	private Button mBtnPlayButton;					// 播放

	private Button mBtnPauseButton;					// 暂停

	private Button mBtnStopButton;					// 停止

	private SampleAudioPlayer mAudioPlayer;				// 播放器

	private TextView frequencyEditView;
	private TextView channelEditView;
	private TextView mTVFilePath;
	private int mFrequency = AudioConstans.FREQUENCY;
	private int mChannel = 1;
	private int mSampBit = AudioConstans.ENCODING;

	private Handler mHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what)
			{
				case SampleAudioPlayer.STATE_MSG_ID:
					showState((Integer)msg.obj);
					break;
			}
		}


	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_main);

		initView();

		initLogic();
	}




	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		mAudioPlayer.release();
	}




	public void initView()
	{
		frequencyEditView = findViewById(R.id.et_sample_rate);
		channelEditView = findViewById(R.id.et_channels);


		mBtnPlayButton = findViewById(R.id.buttonPlay);
		mBtnPlayButton.setOnClickListener(this);

		mBtnPauseButton = findViewById(R.id.buttonPause);
		mBtnPauseButton.setOnClickListener(this);

		mBtnStopButton = findViewById(R.id.buttonStop);
		mBtnStopButton.setOnClickListener(this);

		mTextViewState = findViewById(R.id.tvPlayState);
		mTVFilePath = findViewById(R.id.tv_filePaths);
		mTVFilePath.setText(filePath);
	}

	public void initLogic()
	{
		mAudioPlayer = new SampleAudioPlayer(mHandler);
		frequencyEditView.setText(String.valueOf(mFrequency));
		channelEditView.setText(String.valueOf(mChannel));
	}


	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId())
		{
			case R.id.buttonPlay:
				play();
				break;
			case R.id.buttonPause:
				pause();
				break;
			case R.id.buttonStop:
				stop();
				break;
		}
	}

	public void play()
	{

		if (mAudioPlayer.getPlayState() == PlayState.MPS_PAUSE) {
			mAudioPlayer.play();
			return ;
		}

		// 获取音频数据
		byte[] data = getPCMData();
		if (data == null)
		{
			mTextViewState.setText(filePath + "：该路径下不存在文件！");
			return ;
		}

		// 获取音频参数
		AudioParam audioParam = getAudioParam();
		mAudioPlayer.setAudioParam(audioParam);
		mAudioPlayer.setDataSource(data);

		// 音频源就绪
		mAudioPlayer.prepare();
		mAudioPlayer.play();
	}

	public void pause()
	{
		mAudioPlayer.pause();
	}

	public void stop()
	{
		mAudioPlayer.stop();
	}


	public void showState(int state)
	{
		String showString = "";

		switch(state)
		{
			case PlayState.MPS_UNINIT:
				showString = "MPS_UNINIT";
				break;
			case PlayState.MPS_PREPARE:
				showString = "MPS_PREPARE";
				break;
			case PlayState.MPS_PLAYING:
				showString = "MPS_PLAYING";
				break;
			case PlayState.MPS_PAUSE:
				showString = "MPS_PAUSE";
				break;
		}

		showState(showString);
	}


	public void showState(String str)
	{
		mTextViewState.setText(str);
	}


	/*
	 * 获得PCM音频数据参数
	 */
	public AudioParam getAudioParam()
	{

		String frequency = frequencyEditView.getText().toString();
        mFrequency = Integer.valueOf(frequency);
        String channel = channelEditView.getText().toString();
        mChannel = Integer.valueOf(channel);


		AudioParam audioParam = new AudioParam();
		audioParam.mFrequency = mFrequency;
		audioParam.mChannelConfig = mChannel == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;
		audioParam.mSampBitConfig = AudioFormat.ENCODING_PCM_16BIT;

		return audioParam;
	}

	final String filePath = Utils.localExternalPath + "/soundtouch.pcm";
	/*
	 * 获得PCM音频数据
	 */
	public byte[] getPCMData()
	{

		File file = new File(filePath);
		if (!file.exists()){
            Log.d(TAG, "pcm  can't find path:" + filePath);
			return null;
		}
        Log.d(TAG, "pcm  find path:" + filePath);

		FileInputStream inStream;
		try {
			inStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
            Log.e(TAG, "FileNotFoundException:" + e.getMessage());
            // TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		byte[] data_pack = null;
		if (inStream != null){
			long size = file.length();

			data_pack = new byte[(int) size];
			try {
				inStream.read(data_pack);
			} catch (IOException e) {
                Log.e(TAG, "IOException:" + e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

		}

		return data_pack;
	}

}