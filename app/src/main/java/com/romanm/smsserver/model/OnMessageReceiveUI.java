package com.romanm.smsserver.model;

import com.romanm.smsserver.client.ClientManager;
import com.romanm.smsserver.client_locker.BlockedClients;

import java.util.List;

public interface OnMessageReceiveUI   {
    void updateClientList_UI(ClientManager user);
    void messageReceived_UI(ClientManager user);
    void messageReceived_UI(UserClient user);
    void messageInfo_UI(String msg);
    void errorReceived_UI(String err);
    void blockedClients_UI(List<BlockedClients> blockedClients);
}
