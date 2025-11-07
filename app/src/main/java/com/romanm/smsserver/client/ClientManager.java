package com.romanm.smsserver.client;


import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;


import com.romanm.smsserver.Const.Constants;

import com.romanm.smsserver.client.confurer.SmsController;
import com.romanm.smsserver.client_locker.AuthListener;

import com.romanm.smsserver.model.ClientStatus;

import com.romanm.smsserver.model.SimpleCommunicationProtocol;
import com.romanm.smsserver.model.UserClient;
import com.romanm.smsserver.protocol.ScpTCP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**Одицетворяет нового подключенного к серверу клиента*/
public class ClientManager extends Thread {
    private Socket clientSocket = null;
    private boolean clientRun = false;
    private PrintWriter sendBuffer;
    private String inMessage;
    private ClientStatus clientStatus;
    private AuthListener authListener;
    private SmsController smsController;

    private UserClient userClient;
    private SharedPreferences conf;




    @Override
    public void run() {
      //  super.run();
        try {
            //Сокетный буфер для отправки сообщений
            sendBuffer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
            //Сокетный буфер для приема сообщений
            BufferedReader readBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            clientRun = true;

            //Создать индивидуальный объект, описывающий параметры клиента, отправляющего SMS сообщения
            userClient = new UserClient(clientSocket.getInetAddress().getHostAddress(),
                    clientSocket.getPort());

            //Записать в объект пользователя параметры сокетного соединения
            userClient.setConnMsg(String.format(Constants.INFO_NEW_CONNECTION, new Date(),
                    userClient.getIpAdress(), userClient.getPort()));

            //Сигнализируем событием о том, что соединение было установлено успешно
            clientStatus.userConnected(this);


            SimpleCommunicationProtocol scp = new ScpTCP(smsController, userClient, conf, sendBuffer, clientStatus, authListener);

            /**Проверка пользователя на предмет подбора паролей*/
            if (!authListener.checkClientAttempts(userClient.getIpAdress())) {

                clientStatus.blockedClients(authListener.getBlockedClientsList());
                closeConnection();
                return;
            }
            clientStatus.blockedClients(authListener.getBlockedClientsList());


            while (clientRun) {
                inMessage = readBuffer.readLine();

                if (scp.hasCommand(inMessage)) {
                    /**Сообщения, получаемые при обмене командами с клиентом
                     * при открытом соединении*/
                  //  clientStatus.messageReceived(this);
                    Thread.yield();
                    continue;
                }

                /**Зафиксировать сообщение перед закрытием соединения*/
              //  clientStatus.messageReceived(this);
                closeConnection();
            }

        } catch (Exception e) {
            if (e.getMessage() != null) {
                clientStatus.errorReceived(String.format(Constants.ERR_INTERNAL, new Date(),
                        e.getClass().getSimpleName(), e.getMessage()));
            }
        }

    }

    public UserClient getUserClient() {
        return userClient;
    }



    public ClientManager(SmsController smsController, Socket socket, ClientStatus clientStatus, SharedPreferences conf, AuthListener authListener) {
        this.clientStatus = clientStatus;
        clientSocket = socket;
        this.conf = conf;
        this.authListener = authListener;
        this.smsController = smsController;
    }

    public void closeConnection() throws IOException {
      if (sendBuffer != null) {
          sendBuffer.flush();
          sendBuffer.close();
          sendBuffer = null;
      }

      if (clientSocket != null) {
          clientSocket.close();
          clientSocket = null;
      }
        clientRun = false;

        userClient.setConnMsg(String.format(Constants.INFO_CLIENT_CLOSING,
                userClient.getIpAdress(), userClient.getPort()));

        if (clientStatus != null)
           clientStatus.userDisconnected(this);
    }
}
