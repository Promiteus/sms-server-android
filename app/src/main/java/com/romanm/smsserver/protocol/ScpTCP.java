package com.romanm.smsserver.protocol;

import android.content.SharedPreferences;

import com.romanm.smsserver.Const.Constants;

import com.romanm.smsserver.R;
import com.romanm.smsserver.client.SmsClient;
import com.romanm.smsserver.client.confurer.SmsController;
import com.romanm.smsserver.client_locker.AuthListener;
import com.romanm.smsserver.model.ClientStatus;
import com.romanm.smsserver.model.SimpleCommunicationProtocol;
import com.romanm.smsserver.model.UserClient;
import com.romanm.smsserver.protocol.json_formatter.ScpJsonFormat;
import org.json.JSONException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ScpTCP extends SimpleCommunicationProtocol {
    private String cmd_msg = null;
    private UserClient user;
    private SharedPreferences configurations;
    private PrintWriter sender;
    private ClientStatus clientStatus;
    private AuthListener authListener;
    private SmsController smsController;



    public ScpTCP(SmsController smsController, UserClient user, SharedPreferences configurations, PrintWriter sender, ClientStatus clientStatus, AuthListener authListener) {
        this.user = user;
        this.configurations = configurations;
        this.sender = sender;
        this.clientStatus = clientStatus;
        this.authListener = authListener;
        this.smsController = smsController;
    }

    private void setDuration(int milisec) {
        if (milisec <= 0) return;
        try {
            Thread.sleep(milisec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasCommand(String cmd) {
      try {

          Constants.MsgLog(getClass().getSimpleName(),"READ Json: "+cmd, Constants.MsgTypes.MSG);

          if ((cmd == null) || (cmd.isEmpty()) || (cmd.trim().isEmpty())) {
              String unknownCmd = String.format(Constants.ERR_UNKNOWN_CMD,  cmd);

              sendInternalError(Constants.ERR_UNKNOWN_CMD_TYPE, unknownCmd, true);
              Constants.MsgLog(getClass().getSimpleName(), "Uncknown or Empty Cmd!", Constants.MsgTypes.MSG);
              setDuration(50);
              return false; //Не найдено подходящее соответствие текущей команде
          }

          clearMsgInfo();

          if (cmd != null) {

              /**Начальное приветсвие сервера и клиента*/
              if (cmd.lastIndexOf(ScpTCP.PROT_HELLO) != -1) {
                 /**Распарсить приветствие*/
                 parseCmd(cmd, CmdType.GREETING);

               //  setDuration(50);
                 return true;
              } else
              if (cmd.lastIndexOf(ScpTCP.PROT_AUTH) != -1) {
                  /**Распарсить авторизационные данные пользователя*/
                 parseCmd(cmd, CmdType.ACCESS);

                 /**Проверить IP-адрес на предмет подбора паролей*/
                 boolean badLogin = authListener.checkBadLoginAtteptCounts(user.getIpAdress(), user.isAuthorized());

               //  setDuration(50);
                 return user.isAuthorized();
              } else
              if (cmd.lastIndexOf(ScpTCP.PROT_SMS) != -1) {
                 /**Распарсить данные для отправки по SMS*/
                 parseCmd(cmd, CmdType.SMS);

              //   setDuration(50);
                 return false;//user.isAuthorized();
              } else
              if (cmd.lastIndexOf(ScpTCP.PROT_CONNECTION) != -1) {
                  /**Распарсть команду на статус связи с клиентом*/
                  parseCmd(cmd, CmdType.CLOSE_SOCK);

                //  setDuration(50);
                  return !user.isCloseSocket();
              }
          }
      } catch (Exception ex) {
          sendInternalError(ex.getClass().getSimpleName(), ex.getMessage(), true);

          clientStatus.errorReceived(String.format(Constants.ERR_INTERNAL, new Date(),
                  ex.getClass().getSimpleName(), ex.getMessage()));
      }

        //Constants.MsgLog(getClass().getSimpleName(), Constants.ERR_UNKNOWN_CMD, Constants.MsgTypes.MSG);
        String unknownCmd = String.format(Constants.ERR_UNKNOWN_CMD,  cmd);

        sendInternalError(Constants.ERR_UNKNOWN_CMD_TYPE, unknownCmd, true);

        setDuration(50);
        return false; //Не найдено подходящее соответствие текущей команде
    }



    @Override
    public void sendAuth(UserClient user) {
        try {
            sendMessage(ScpJsonFormat.createAuthJsonString(user));
        } catch (JSONException e) {
            clientStatus.errorReceived(String.format(Constants.ERR_INTERNAL, new Date(),
                    e.getClass().getSimpleName(), e.getMessage()));
        }
    }



    @Override
    public void sendGreeting(UserClient user)  {
        try {
            sendMessage(ScpJsonFormat.createGreetingJsonString(user));
        } catch (JSONException e) {
            sendInternalError(e.getClass().getSimpleName(), e.getMessage(), true);
            clientStatus.errorReceived(String.format(Constants.ERR_INTERNAL, new Date(),
                    e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
    public void sendInternalError(String errType, String errMsg, boolean closeSocket) {
        try {

            user.setErrType(errType);
            user.setErrMsg(errMsg);
            user.setCloseSocket(closeSocket);

            sendMessage(ScpJsonFormat.createErrorJsonString(user));

        } catch (JSONException e) {
            sendInternalError(e.getClass().getSimpleName(), e.getMessage(), true);
            clientStatus.errorReceived(String.format(Constants.ERR_INTERNAL, new Date(),
                    e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
    public void sendConnectionStat(UserClient user) {
        try {

            sendMessage(ScpJsonFormat.createConnStatJsonString(user));

        } catch (JSONException e) {
            sendInternalError(e.getClass().getSimpleName(), e.getMessage(), true);
            clientStatus.errorReceived(String.format(Constants.ERR_INTERNAL, new Date(),
                    e.getClass().getSimpleName(), e.getMessage()));
        }
    }


    private synchronized void sendMessage(String msg) {
        if (sender != null) {
            sender.println(msg);
            sender.flush();
        }
    }


    private void clearMsgInfo() {
        user.setInfoMsg("");
    }

    private void zeroErrCode() {
        user.setErrCode(1);
    }

    private void sendToMsglogSmsServer(String msg) {
       // clearMsgInfo();
        setDuration(50);
        user.setInfoMsg(msg);
        clientStatus.messageRecieved(user);
    }

    private boolean isSmsEnabled() {
       return  !configurations.getBoolean(Constants.PREF_SMS_TEST, false);
    }

    @Override
    public void parseCmd(String cmd, CmdType stat) {
        zeroErrCode();
        try {
          switch (stat) {
              case GREETING:
                  /**Распарсить приветствие*/
                  ScpJsonFormat.getJsonGreetingData(cmd, this.user);

                    String authorized = user.isAuthorized() ?
                          Constants.INFO_AUTHORIZED_CLIENT:Constants.INFO_UNAUTHORIZED_CLIENT;
                    //user.setInfoMsg(String.format(Constants.INFO_HELLO_SERVER, new Date(), authorized));

                  //clientStatus.messageRecieved(user);
                  sendToMsglogSmsServer(String.format(Constants.INFO_HELLO_SERVER, new Date(), authorized));

                  this.user.setComments(Constants.GREATING_FOR_CLIENT);
                  /**Отправить клиенту ответное приветствие*/
                  sendGreeting(this.user);
                  break;
              case ACCESS:
                  /**Распарсить данные для авторизации клиента*/
                  ScpJsonFormat.getJsonAuthData(cmd, this.user);

                    user.setAuthorized(configurations.getString(Constants.PREF_PASS, "").equals(user.getPassword()) &&
                          configurations.getString(Constants.PREF_LOGIN, "").equals(user.getLogin()));



                    Constants.MsgLog(getClass().getSimpleName(), configurations.getString(Constants.PREF_PASS, ""), Constants.MsgTypes.MSG);
                    Constants.MsgLog(getClass().getSimpleName(), configurations.getString(Constants.PREF_LOGIN, ""), Constants.MsgTypes.MSG);

                    String access = !user.isAuthorized() ? String.format(Constants.INFO_AUTH_DENINE,
                          new Date(), user.getLogin(), user.getPassword()) : String.format(Constants.INFO_AUTH_OK, new Date(), user.getLogin());
                   // user.setInfoMsg(access);

                    user.setComments(user.isAuthorized() ? ScpTCP.VAL_AUTH_OK:ScpTCP.VAL_AUTH_DENINE);
                    user.setCloseSocket(user.isAuthorized() ? false : true);

                //  clientStatus.messageRecieved(user);
                  sendToMsglogSmsServer(access);

                  /**Отправить пользователю результат авторизации*/
                  sendAuth(user);
                  break;
              case SMS:
                  if (user.isAuthorized()) {
                     /**Получить из пакета данные о SMS адресатах*/
                     ScpJsonFormat.getJsonSmsData(cmd, this.user);

                     String sms = String.format(Constants.INFO_ADDRESS_LIST_FOR_MSG, user.getTelNumbers().size(),
                              user.getTelNumbers().toString(), user.getSmsMsg());
                     List<String> sendSmsList = null;
                     String smsStatSending = "";

                     sendToMsglogSmsServer(sms);

                     int smsCountDelta = smsController.getSmsLimit(user.getTelNumbers().size());
                     if (smsCountDelta > 0) {
                          Constants.MsgLog(getClass().getSimpleName(),
                                 "smsCountDelta="+String.valueOf(smsCountDelta) , Constants.MsgTypes.MSG);

                          SmsClient smsClient = SmsClient.getInstance();
                          smsClient.setTels(user.getTelNumbers());

                          Constants.MsgLog(getClass().getSimpleName(),
                                 "SMS Message: "+user.getSmsMsg() , Constants.MsgTypes.MSG);


                          smsClient.setMessage(user.getSmsMsg());
                          smsClient.setSmsCountDelta(smsCountDelta);

                          sendToMsglogSmsServer(Constants.INFO_STARTING_SMS_SENDING);

                          setDuration(50);

                          if (isSmsEnabled()) {
                              Constants.MsgLog(getClass().getSimpleName(),
                                      "SMS test: false" , Constants.MsgTypes.MSG);
                          } else {
                              Constants.MsgLog(getClass().getSimpleName(),
                                      "SMS test: true" , Constants.MsgTypes.MSG);
                          }

                          /**Синхронизированный метод отправки SMS*/
                          String res = smsClient.sendSMS(isSmsEnabled());
                          if (res.lastIndexOf("Error") >= 0) {
                              user.setErrCode(-1);
                          } else if (res.lastIndexOf("Error") < 0) {
                              user.setErrCode(0);
                          }

                          Constants.MsgLog(getClass().getSimpleName(),
                                 "SMS res: "+res , Constants.MsgTypes.MSG);

                          sendToMsglogSmsServer(res);


                         sendSmsList = smsClient.getSendTels();
                         if (sendSmsList != null) {
                             smsStatSending = String.format(ScpTCP.VAL_SMS_OK, sendSmsList.size(), sendSmsList.toString());
                             sendToMsglogSmsServer(smsStatSending);
                         }
                     } else if (smsCountDelta <= 0) {
                         smsStatSending = ScpTCP.VAL_SMS_LIMIT;
                         sendToMsglogSmsServer(smsStatSending);
                     }
                     smsController.commitSmsCounter();


                     user.setComments(smsStatSending);
                     user.setCloseSocket(true);
                     /**Уведомить клиента об удачной или неудачной отправке SMS */
                     sendConnectionStat(this.user);
                  //   user.setInfoMsg(smsOk);
                  }
                  break;

              case CLOSE_SOCK:
                  ScpJsonFormat.getJsonConnectionData(cmd, user);
                  break;
          }

           //Очистить информацию о сообщении
          //  clearMsgInfo();

        } catch (Exception e) {
            sendInternalError(e.getClass().getSimpleName(), e.getMessage(), true);

            clientStatus.errorReceived(String.format(Constants.ERR_INTERNAL, new Date(),
                    e.getClass().getSimpleName(), e.getMessage()));
        }
    }
}
