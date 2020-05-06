package com.geniusgithub.voicechange.audio;

public class NetworkClient {

    private NetworkReceiver mNetworkReceive;
    private boolean mConnectNetworkSuccess = false;

    public NetworkClient(){

    }

    public boolean connectNetworkService(NetworkReceiver networkReceiver){
        mNetworkReceive = networkReceiver;
        if (mNetworkReceive != null) {
            mConnectNetworkSuccess = true;
        }

        return true;
    }

    public boolean disConnectNetworkService(){
        mNetworkReceive = null;
        mConnectNetworkSuccess = false;
        return true;
    }

    public boolean sendAudio(byte data[]){
        if (data == null || mConnectNetworkSuccess == false) {
            return false;
        }

        return mNetworkReceive.receiveAudio(data);
    }
}
