package com.romanm.smsserver.client;

import android.content.Context;
import android.telephony.SmsManager;

import com.romanm.smsserver.Const.Constants;
import com.romanm.smsserver.model.ClientStatus;
import com.romanm.smsserver.model.SmsThreadNotifier;
import com.romanm.smsserver.model.UserClient;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SmsClient /**implements Runnable*/ {
    private String message;
    private Set<String> tels;
    private List<String> sendTels;
    private Context context;

  //  private SmsThreadNotifier smsNotifier;

    private int smsCountDelta = 0;

    public Set<String> getTels() {
        return tels;
    }
    public void setTels(Set<String> tels) {
        this.tels = tels;
    }
    public Integer getDuration() {
        return duration;
    }
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    public Integer getTimeInterval() {
        return timeInterval;
    }
    public void setTimeInterval(Integer timeInterval) {
        this.timeInterval = timeInterval;
    }
    private Integer duration;
    private Integer timeInterval;

    private static SmsClient smsClient = null;
    private SmsClient() {}

    public static SmsClient getInstance() {
       if (smsClient == null) {
        synchronized (SmsClient.class) {
            if (smsClient == null) {
                smsClient = new SmsClient();
            }
        }
       }
        return smsClient;
    }


    public String getMessage() {
        return message;
    }

    public List<String> getSendTels() {
        return sendTels;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public void setSmsCountDelta(int smsCountDelta) {
        this.smsCountDelta = smsCountDelta;
    }



    private void sendMessage(String msg, String tel) {
        if ((msg != null && !msg.isEmpty()) && (tel != null && !tel.isEmpty() && tel.length() >= 6)) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(tel, null, msg, null, null);
        }
    }


    public synchronized String sendSMS(boolean isSendSms) throws InterruptedException {
        String info = null;
        sendTels  = new ArrayList<>();
        int i = 0;


        for (String tel: this.tels) {
          try {
              Constants.MsgLog(getClass().getSimpleName(),
                      "(i >= (smsCountDelta)) >> " + String.valueOf(i) + " >= " + String.valueOf(smsCountDelta), Constants.MsgTypes.MSG);
              if (i >= smsCountDelta) break;
              sendTels.add(tel);

              Constants.MsgLog(getClass().getSimpleName(), "sendTels: " + String.valueOf(sendTels.get(i)), Constants.MsgTypes.MSG);

              if (isSendSms) {
               sendMessage(this.message, tel);
              }

              info += String.format(Constants.SMS_OK, tel);
              i++;
          } catch (Exception e) {
              return String.format(Constants.ERR_SEND_SMS, new Date(), e.getMessage());
          }
         //   return info;
        }
        return info;
    }


   /** @Override
    public void run() {
        try {
            sendSMS();
            if (smsNotifier != null) {
                smsNotifier.realSendAddressats(sendTels);
            }
            Thread.yield();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/


}
