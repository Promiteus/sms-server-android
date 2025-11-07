package com.romanm.smsserver.model;

import com.romanm.smsserver.client.ClientManager;
import com.romanm.smsserver.client_locker.BlockedClients;

import java.util.List;

public interface ClientStatus {

    void userConnected(ClientManager user);
    void userDisconnected(ClientManager user);
    void messageReceived(ClientManager user);
    void messageRecieved(UserClient user);
    void errorReceived(String err);
    void blockedClients(List<BlockedClients> blockedClients);
}
