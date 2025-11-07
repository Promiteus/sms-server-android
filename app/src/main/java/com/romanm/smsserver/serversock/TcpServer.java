package com.romanm.smsserver.serversock;


import android.content.SharedPreferences;


import com.romanm.smsserver.Const.Constants;

import com.romanm.smsserver.client.ClientManager;

import com.romanm.smsserver.client.confurer.SmsController;
import com.romanm.smsserver.client_locker.AuthListener;
import com.romanm.smsserver.client_locker.BlockedClients;
import com.romanm.smsserver.model.ClientStatus;
import com.romanm.smsserver.model.OnMessageReceiveUI;
import com.romanm.smsserver.model.UserClient;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;

import java.util.List;

public class TcpServer extends Thread implements ClientStatus {
    private ServerSocket serverSock = null;

    private boolean started = false;

    private List<ClientManager> clients = null;
    private SharedPreferences conf;
    private OnMessageReceiveUI onMessageReceiveUI;
    private List<BlockedClients> blockedClientsList;
    private AuthListener authListener = null;
    private SmsController smsController;


    public List<BlockedClients> getBlockedClientsList() {
        return blockedClientsList;
    }


    public TcpServer(SmsController smsController, SharedPreferences conf, AuthListener authListener, OnMessageReceiveUI onMessageReceiveUI) {
        clients = new ArrayList<ClientManager>();
        this.onMessageReceiveUI = onMessageReceiveUI;
        this.conf = conf;
        this.authListener = authListener;
        this.smsController = smsController;
    }



    private void runServer() {
        try {
            serverSock = new ServerSocket(Integer.valueOf(conf.getString(Constants.PREF_LISTEN_PORT, Constants.defaultPort)));
            started = (serverSock != null);
            onMessageReceiveUI.messageInfo_UI(String.format(Constants.INFO_LISTEN_PORT,
                    conf.getString(Constants.PREF_LISTEN_PORT, Constants.defaultPort)));

            while (started) {
                Socket socket = serverSock.accept();
                if (socket != null) {
                  ClientManager client = new ClientManager(smsController, socket, this, conf, authListener);
                  clients.add(client);
                  client.start();
                }
            }
        } catch (Exception e) {
            onMessageReceiveUI.errorReceived_UI(getClass().getSimpleName()+": "+e.getMessage()+"\n");
        }
    }

    @Override
    public void run() {
        super.run();
        runServer();
    }

    public void StopServer() throws Exception {
        if (clients != null) {
           if (clients.size() > 0) {
               for (ClientManager client: clients) {
                   client.closeConnection();
               }
           }
        }
        started = false;
        if (serverSock != null) {
            serverSock.close();
            serverSock = null;
        }

    }

    public List<ClientManager> getClients() {
        return clients;
    }



    @Override
    public void userConnected(ClientManager user) {
        onMessageReceiveUI.updateClientList_UI(user);
    }

    @Override
    public void userDisconnected(ClientManager user) {
        onMessageReceiveUI.updateClientList_UI(user);
        clients.remove(user);
    }

    public boolean isStarted() {
        return started;
    }

    @Override
    public void messageReceived(ClientManager user) {
        onMessageReceiveUI.messageReceived_UI(user);
    }

    @Override
    public void messageRecieved(UserClient user) {
        onMessageReceiveUI.messageReceived_UI(user);
    }

    @Override
    public void errorReceived(String err) {
        onMessageReceiveUI.errorReceived_UI(err);
    }

    @Override
    public void blockedClients(List<BlockedClients> blockedClients) {
        onMessageReceiveUI.blockedClients_UI(blockedClients);
    }
}
