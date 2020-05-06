package com.geniusgithub.voicechange.audio;

import com.amoment.audio.JNISoundTouch;
import com.geniusgithub.voicechange.audio.common.TransFormTool;

public class SoundTouchTransFormTool extends TransFormTool {

    private JNISoundTouch ndkUtil = new JNISoundTouch();

    public void setSampleRate(int sampleRate) {
        ndkUtil.setSampleRate(sampleRate);
    }

    public void setChannels(int channel) {
        ndkUtil.setChannels(channel);
    }

    public void setTempoChange(float newTempo) {
        ndkUtil.setTempoChange(newTempo);
    }

    public void setPitchSemiTones(int newPitch) {
        ndkUtil.setPitchSemiTones(newPitch);
    }

    public void setRateChange(float newRate) {
        ndkUtil.setRateChange(newRate);
    }

    public void putSamples(short[] samples, int len){
        ndkUtil.putSamples(samples, len);
    }

    public short[] receiveSamples() {
        return ndkUtil.receiveSamples();
    }
}
