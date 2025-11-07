package com.romanm.smsserver.model;

import com.romanm.smsserver.Const.Constants;

import java.util.HashSet;
import java.util.Set;

public class UserClient {
    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIpAdress() {
        return ipAdress;
    }

    public void setIpAdress(String ipAdress) {
        this.ipAdress = ipAdress;
    }

    private String ipAdress;
    private String msg;
    private String smsMsg;
    private String login;
    private String password;
    private boolean authorized = false;
    private String errType;
    private int errCode = -1;

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getConnMsg() {
        return connMsg;
    }

    public void setConnMsg(String connMsg) {
        this.connMsg = connMsg;
    }

    private String connMsg;


    public String getErrType() {
        return errType;
    }

    public void setErrType(String errType) {
        this.errType = errType;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    private String errMsg;

    public boolean isCloseSocket() {
        return closeSocket;
    }

    public void setCloseSocket(boolean closeSocket) {
        this.closeSocket = closeSocket;
    }

    private boolean closeSocket;

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public boolean isEncryptEnabled() {
        return encryptEnabled;
    }

    public void setEncryptEnabled(boolean encryptEnabled) {
        this.encryptEnabled = encryptEnabled;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private boolean encryptEnabled = false;
    private String status;

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    private String comments;

    public String getInfoMsg() {
        return infoMsg;
    }

    public void setInfoMsg(String infoMsg) {
        this.infoMsg = String.format(Constants.NET_PARS,
                getIpAdress(), getPort())+infoMsg;
    }

    private String infoMsg;
    private Set<String> telNumbers;
    private int timeInterval;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSmsMsg() {
        return smsMsg;
    }

    public void setSmsMsg(String smsMsg) {
        this.smsMsg = smsMsg;
    }

    public Set<String> getTelNumbers() {
        return telNumbers;
    }

    public void setTelNumbers(Set<String> telNumbers) {
        this.telNumbers = telNumbers;
    }

    public int getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(int timeInterval) {
        this.timeInterval = timeInterval;
    }


    public UserClient(String ipAdress, int port) {
        this.ipAdress = ipAdress;
        this.port = port;
        this.telNumbers = new HashSet<String>();
    }


    @Override
    public String toString() {
        return String.format(">> %s: %d [%s]", ipAdress, port, login);
    }
}
