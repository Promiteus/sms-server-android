package com.romanm.smsserver.model;

public class Configs {
    private String login = null;
    private String password = null;
    private Integer usersCnt = 0;
    private boolean encryption = false;
    private Integer smsNum = 50;
    private String connectionPort;


    public String getConnectionPort() {
        return connectionPort;
    }

    public void setConnectionPort(String connectionPort) {
        this.connectionPort = connectionPort;
    }





    public int getSmsNum() {
        return smsNum;
    }

    public void setSmsNum(Integer smsNum) {
        this.smsNum = smsNum;
    }

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

    public int getUsersCnt() {
        return usersCnt;
    }

    public void setUsersCnt(Integer usersCnt) {
        this.usersCnt = usersCnt;
    }

    public boolean isEncryption() {
        return encryption;
    }

    public void setEncryption(boolean encryption) {
        this.encryption = encryption;
    }



}
