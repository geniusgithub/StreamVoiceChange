package com.geniusgithub.voicechange.audio;

import com.geniusgithub.voicechange.audio.common.AudioConstans;
import com.geniusgithub.voicechange.audio.common.AudioParam;

public class NetworkReceiver {

    private StreamAudioPlayer mSteamAudioPlay = new StreamAudioPlayer();
    private static int FREQUENCY = AudioConstans.FREQUENCY;
    private static int CHANNEL = AudioConstans.PLAY_CHANNEL;
    private static int ENCODING = AudioConstans.ENCODING;

    public NetworkReceiver() {
    }

    public void init(){
        AudioParam audioParam = new AudioParam();
        audioParam.mFrequency = FREQUENCY;
        audioParam.mChannelConfig = CHANNEL;
        audioParam.mSampBitConfig = ENCODING;
        mSteamAudioPlay.setAudioParam(audioParam);
        mSteamAudioPlay.prepare();
    }

    public void unInit(){
        mSteamAudioPlay.release();
    }

    public boolean receiveAudio(byte data[]){
        if (data == null) {
            return false;
        }

        return mSteamAudioPlay.play(data);
    }
}
