package com.geniusgithub.voicechange.audio.common;


public abstract class TransFormTool {

    public abstract void setSampleRate(int sampleRate);

    /**
     * 原声通道数
     */
    public abstract void setChannels(int channel);

    /**
     * 调整节拍
     *
     * @param newTempo 指定节拍，原始值为0，大快小慢,(-50 .. +100)
     */
    public abstract void setTempoChange(float newTempo);

    /**
     * 调整音调
     *
     * @param newPitch (-12 .. +12)
     */
    public abstract void setPitchSemiTones(int newPitch);

    /**
     * 改变播放速度
     *
     * @param newRate 指定速度，原始值为0，大快小慢,(-50 .. +100)
     */
    public abstract void setRateChange(float newRate);


    public abstract void putSamples(short[] samples, int len);

    public abstract short[] receiveSamples();
}
