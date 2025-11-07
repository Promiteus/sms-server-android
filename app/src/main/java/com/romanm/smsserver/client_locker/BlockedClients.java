package com.romanm.smsserver.client_locker;

import android.os.Parcel;
import android.os.Parcelable;

public class BlockedClients implements Parcelable {
    private String ipAddress;
    private String blockTime;

    public BlockedClients() {}

    public BlockedClients(String ipAddress, String blockTime) {
        this.blockTime = blockTime;
        this.ipAddress = ipAddress;
    }


    protected BlockedClients(Parcel in) {
        ipAddress = in.readString();
        blockTime = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ipAddress);
        dest.writeString(blockTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BlockedClients> CREATOR = new Creator<BlockedClients>() {
        @Override
        public BlockedClients createFromParcel(Parcel in) {
            return new BlockedClients(in);
        }

        @Override
        public BlockedClients[] newArray(int size) {
            return new BlockedClients[size];
        }
    };

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(String blockTime) {
        this.blockTime = blockTime;
    }


}
