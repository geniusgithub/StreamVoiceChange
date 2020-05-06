package com.geniusgithub.voicechange.jni;

public class NDKUtil {

    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

}
