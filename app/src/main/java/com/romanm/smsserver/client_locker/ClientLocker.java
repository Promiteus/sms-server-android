package com.romanm.smsserver.client_locker;


import com.romanm.smsserver.Const.Constants;

import java.util.Date;
import java.util.List;
import java.util.Map;


public class ClientLocker {
    private int attemptCount = 3;  //количество неудачных попыток
    private float lockDuration = 30; //минуты блокировки
    private List<BlockedClients> blockedClientsList;
    private String ipAddress;
    private BlockedClients blockedClients;

    private Date blockTime = null; //время, в которое пользователь был заблокирован
    private Date curTime;
    private int attemptCountBuff;


    private void addBlockedClient(String ipAddress, String dateTime) {
      if (blockedClientsList != null) {
          blockedClients = new BlockedClients(ipAddress, dateTime);
          blockedClientsList.add(blockedClients);
      }
    };

    private void delBlockedClient() {
        if (blockedClientsList != null) {
         blockedClientsList.remove(blockedClients);
        }
    }

    public ClientLocker(int attemptCount, float lockDuration, List<BlockedClients> blockedClientsList, String ipAddress) {
        this.attemptCount = attemptCount;
        this.lockDuration = lockDuration;
        this.blockedClientsList = blockedClientsList;
        this.attemptCountBuff = attemptCount;
        this.ipAddress = ipAddress;
    }

    public int getAttemptCount() {
        return attemptCount;
    }


    public Date getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(Date blockTime) {
        this.blockTime = blockTime;
    }


    private boolean compareDates() {
        if (blockTime != null) {
            float dt = (curTime.getTime() - blockTime.getTime()) / 1000;
            dt = dt / 60;

            if (dt >= lockDuration) {
                attemptCount = attemptCountBuff;
                this.blockTime = null;
                delBlockedClient();
                return true;
            } else if (dt < lockDuration) {
                Constants.MsgLog(getClass().getSimpleName(),"Вход запрещен! Время блокировки не снято!", Constants.MsgTypes.MSG);
                return false;
            }

        } else {
            blockTime = new Date();
            addBlockedClient(this.ipAddress, blockTime.toString());
            return false;
        }
       return false;
    }

    public boolean checkClientForDatesDuration() {
        Date curDate = new Date(); //Текущее время
        this.curTime = curDate;
        return compareDates();
    }

    public boolean ckeckClient(boolean authorized) {
        Date ldate = new Date();
        curTime = ldate;
        if (attemptCount == 0) {
           return compareDates();
        } else if (attemptCount > 0) {
            if (!authorized) {
               --attemptCount;
               return true;
            } else {
               return true;
            }
        }
      return false;
    }



}
