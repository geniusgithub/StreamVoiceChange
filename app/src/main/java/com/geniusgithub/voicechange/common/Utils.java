package com.example.soundtouch.common;

import android.media.AudioFormat;
import android.os.Environment;

public class Utils {

	public static String localExternalPath = Environment.getExternalStorageDirectory().getPath();
	
	public static byte[] shortToByteSmall(short[] buf){
		
		byte[] bytes = new byte[buf.length * 2];
		for(int i = 0, j = 0; i < buf.length; i++, j+=2){
			short s = buf[i];
			
			byte b1 = (byte) (s & 0xff);
			byte b0 = (byte) ((s >> 8) & 0xff);
			
			bytes[j] = b1;
			bytes[j+1] = b0;
		}
		return bytes;
		
	}
	
    /** 
     * 通过byte数组取到short 
     *  
     * @param b 
     * @param index 
     *            第几位开始取 
     * @return 
     */  
    public static short[] getShort(byte[] b) {
    	if(b == null) {
    		return null;
    	}
    	short[] s = new short[b.length / 2];
    	for(int i = 0; i<s.length; i++) {
    		s[i] = (short) (((b[i*2 + 1] << 8) | b[i*2 + 0] & 0xff));
    	}
    	return s;
    }
	
}
