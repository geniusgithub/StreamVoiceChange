package com.geniusgithub.voicechange.audio.common;

public class AudioParam {

	public int mFrequency;					// 采样率

	public int mChannelConfig;				// 声道config

	public int mSampBitConfig;				// 采样精度


	public String toString(){
		return "[mFrequency = " + mFrequency + ", mChannelConfig = " + mChannelConfig + ", mSampBitConfig = " + mSampBitConfig +"]";
	}
}
