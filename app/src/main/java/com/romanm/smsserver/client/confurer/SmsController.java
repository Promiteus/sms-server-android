package com.romanm.smsserver.client.confurer;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;


import com.romanm.smsserver.Const.Constants;
import com.romanm.smsserver.R;
import com.romanm.smsserver.client.SmsClient;
import com.romanm.smsserver.model.Configs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import static android.util.Half.NaN;

public class SmsController {
    private Context context;
    private int smsNum;
    private long lastDate;
    private long curDate;
    private int smsCount;
    SharedPreferences sharePref;


    private void localMsg(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public int getSmsNum() {
        return smsNum;
    }

    public int getSmsCount() {
        return smsCount;
    }

    private long getLastDateSetts() {
        long last = 0;
        if (sharePref != null) {
           last = sharePref.getInt(Constants.CONF_LAST_DATE, 0);
        }
        return last;
    }



    private void setLastDateSetts(long lastDate) {
        this.lastDate = lastDate;
        if (sharePref != null) {
            SharedPreferences.Editor editor = sharePref.edit();
            editor.putInt(Constants.CONF_LAST_DATE, (int) lastDate);
            editor.apply();
        }
    }



    public int getSmsCountSetts() {
        int smsCnt = 0;
        if (sharePref != null) {
            smsCnt = sharePref.getInt(Constants.CONF_SMS_COUNT, 0);
        }
        return smsCnt;
    }



    public void setSmsCountSetts(int smsCount) {
         this.smsCount = smsCount;
         if (sharePref != null) {
            SharedPreferences.Editor editor = sharePref.edit();
            editor.putInt(Constants.CONF_SMS_COUNT, smsCount);
            editor.apply();
        }
    }


    /**Инициализируем счетчик SMS в значения по умолчанию*/
    private void init() {
         curDate = getNowTime();
         setLastDateSetts(curDate);
         setSmsCountSetts(0);
         localMsg(context.getResources().getString(R.string.warn_sms_day_updated));
    }


    /**Получить текущую мгновенную дату*/
    private long getNowTime() {
        return new Date().getTime()/1000;
    }



    private long compareDates(long nowDate, long lastDate) {
        long dif = nowDate - lastDate;
        return dif;
    }



    /**Проверить, достиг ли лимит отправки SMS за сутки*/
    public synchronized int getSmsLimit(int smsSum) {
        curDate = getNowTime();

        if (compareDates(curDate, lastDate) < Constants.secondsDay) {

            if (smsCount >= smsNum) {
                setSmsCountSetts(smsCount);
               // localMsg(context.getResources().getString(R.string.warn_sms_limit));
                return (smsNum - smsCount);
            } else if (smsCount < smsNum) {
                smsCount +=smsSum;
            }

        } else if (compareDates(curDate, lastDate) >= Constants.secondsDay) {
           init();
           return (smsNum - smsCount);
        }

        return (smsNum - smsCount);
    }



    public synchronized void commitSmsCounter() {
        setSmsCountSetts(smsCount);
    }



    public SmsController(Context context, int smsNum) {
        this.context = context;
        this.smsNum = smsNum;

       // SmsClient smsClient = new SmsClient();

        sharePref = context.getSharedPreferences(Constants.SETTS_PREFS, Context.MODE_PRIVATE);

        if (getSmsCountSetts() < 0) {
            init();
        } else {
            this.curDate = getNowTime();
            this.lastDate = getLastDateSetts();
            this.smsCount = getSmsCountSetts();
        }
    }
}
