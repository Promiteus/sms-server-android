package com.romanm.smsserver.model;

import java.io.PrintWriter;
import java.util.Date;

public abstract class SimpleCommunicationProtocol {

    public static final String KEY_LOGIN = "username";
    public static final String KEY_PASS = "password";
    public static final String KEY_COMMENTS = "comments";
    public static final String KEY_SMS_MSG = "smsMessage";
    public static final String KEY_TEL_LIST = "tels";
    public static final String KEY_STATUS = "status";
    public static final String KEY_CRYPT = "encryption";
    public static final String KEY_AUTHORIZED = "authorized";
    public static final String KEY_ERR_TYPE = "errType";
    public static final String KEY_ERR_MSG = "errMsg";
    public static final String KEY_SOCK_CLOSE = "closeSocket";

    public static final String VAL_AUTH_OK = "Авторизация пройдена!";
    public static final String VAL_AUTH_DENINE = "Нет такого пользователя!";
    public static final String VAL_SMS_OK = "%d сообщение(ий) было отправлено на номера:\n%s\n";
    public static final String VAL_SMS_LIMIT = "Достигнут суточный лимит по отправки SMS-сообщений!\n";
    public static final String VAL_SMS_ERR = "Не удалось отправить сообщения: %s.";

    public static final String PROT_HELLO = "greeting";
    public static final String PROT_AUTH = "auth";
    public static final String PROT_SMS = "sms";
    public static final String PROT_UNKNOWN = "error";
    public static final String PROT_CONNECTION = "connection";
    public static final String PROT_CODE = "code";

    public static final String ERR_INFO = "Error [class: %s] >> описание: %s";
    public static final String MSG_INFO = "Info [class: %s] >> %s";
    public static final String MSG_INFO_MAIN = "Info [main] >> %s";
    public static final String MSG_INFO_IP = "[Server]: %s >> %s:%d >> %s";



    public enum CmdType {
        GREETING, ACCESS, SMS, UNKNOWN_CMD, CLOSE_SOCK;
    }

    public enum MsgType {
        ERR, INFO, INFO_IP;
    }


    public abstract boolean hasCommand(String cmd);
    public abstract void parseCmd(String cmd, CmdType stat);
    public abstract void sendAuth(UserClient user);
    public abstract void sendGreeting(UserClient user);
    public abstract void sendInternalError(String errType, String errMsg, boolean closeSocket);
    public abstract void sendConnectionStat(UserClient user);
}
