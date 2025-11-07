package com.romanm.smsserver.client_locker;

import com.romanm.smsserver.Const.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthListener {
    private Map<String, ClientLocker> ipClients;
    private int attemptCount = 3;  //количество неудачных попыток
    private List<BlockedClients> blockedClientsList;
    private float lockDuration = 30; //минуты блокировки

    public void setBlockedClientsList(List<BlockedClients> blockedClientsList) {
        this.blockedClientsList = blockedClientsList;
    }

    public List<BlockedClients> getBlockedClientsList() {
        return blockedClientsList;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }

    public float getLockDuration() {
        return lockDuration;
    }

    public void setLockDuration(float lockDuration) {
        this.lockDuration = lockDuration;
    }


    public AuthListener() {
        ipClients = new HashMap<>();
        blockedClientsList = new ArrayList<>();
    }

    public Map<String, ClientLocker> getIpClients() {
        return ipClients;
    }

    private synchronized void addClient(String ipAddress) {
        if (ipAddress != null && !ipClients.containsKey(ipAddress)) {
            ipClients.put(ipAddress, new ClientLocker(attemptCount, lockDuration, blockedClientsList, ipAddress));
        }
    }

    public synchronized void removeClient(String ipAddress) {
        if (ipAddress != null && ipClients.containsKey(ipAddress)) {
            ipClients.remove(ipAddress);

            for (BlockedClients client: blockedClientsList) {
                if (client.getIpAddress().equals(ipAddress)) {
                    blockedClientsList.remove(client);
                }
            }
        }
    }

    public void removeAllClientsAndBlockedList() {
        ipClients.clear();
        blockedClientsList.clear();
    }

    public synchronized boolean checkBadLoginAtteptCounts(String ipAddress, boolean isAutorized) {
        if (ipAddress != null && ipClients.containsKey(ipAddress)) {
            ClientLocker locker = ipClients.get(ipAddress);
            if (locker != null) {
                return locker.ckeckClient(isAutorized);
            } else {
                return false;
            }
        }
        return false;
    }

    public synchronized boolean checkClientAttempts(String ipAddress) {
        if (ipAddress != null) {
            if (!ipClients.containsKey(ipAddress)) {
                addClient(ipAddress);
                return true;
            } else {
               ClientLocker locker = ipClients.get(ipAddress);
               if (locker.getAttemptCount() != 0) {
                   return true;
               } else if (locker.getAttemptCount() <= 0) {
                   return locker.checkClientForDatesDuration();
               }

            }
        }
        return false;
    }
}
