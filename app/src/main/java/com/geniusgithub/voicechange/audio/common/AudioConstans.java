package com.geniusgithub.voicechange.audio.common;

import android.media.AudioFormat;

public interface AudioConstans {
    public static int FREQUENCY = 16000;
    public static int RECORD_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    public static int PLAY_CHANNEL = AudioFormat.CHANNEL_OUT_MONO;
    public static int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
}
