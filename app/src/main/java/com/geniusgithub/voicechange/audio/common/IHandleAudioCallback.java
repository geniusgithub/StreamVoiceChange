package com.geniusgithub.voicechange.audio.common;

public interface IHandleAudioCallback {
    public void onHandleStart();
    public void onHandleProcess(byte[] data);
    public void onHandleComplete();
}
