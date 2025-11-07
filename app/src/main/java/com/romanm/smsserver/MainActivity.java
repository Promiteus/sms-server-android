package com.romanm.smsserver;



import android.Manifest;
import android.app.AlertDialog;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;

import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;


import android.widget.TextView;
import android.widget.Toast;

import com.romanm.smsserver.Const.Constants;
import com.romanm.smsserver.client.ClientManager;


import com.romanm.smsserver.client.confurer.SmsController;
import com.romanm.smsserver.client_locker.AuthListener;
import com.romanm.smsserver.client_locker.BlockedClients;
import com.romanm.smsserver.model.Configs;
import com.romanm.smsserver.model.OnMessageReceiveUI;
import com.romanm.smsserver.model.UserClient;

import com.romanm.smsserver.serversock.TcpServer;


import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;

import static com.romanm.smsserver.Const.Constants.*;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener /*implements EventExchangerToMainActivity.OnMessageExchangerListener*/  {

    private TextView serverLog = null;
    private TextView smsCntView = null;
    private TextView ipLocalView = null;
    private SmsController smsController = null;
    private Configs conf = null;
    private TcpServer smsTcpServer = null;
    private List<BlockedClients> blockedClientsList;
    private SharedPreferences prefSetts;
    private MenuItem menuItem = null;
    private StringBuilder stringBuilder = null;
    private BroadcastReceiver  activityReceiver;
    private IntentFilter intentFilter;
    private AuthListener authListener = null;
    private RememberActiviyViews rememberActiviyViews;



    private void updateClientList(final ClientManager clientManager) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addLogText(clientManager.getUserClient().getConnMsg());
            }
        });
    }


    private String getLocalIP() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        int localIP = wifiInfo.getIpAddress();
        String ip = String.format("%d.%d.%d.%d", (localIP & 0xFF), (localIP >> 8 & 0xFF),
                (localIP >> 16 & 0xFF), (localIP >> 24 & 0xFF));
        return ip;
    }


    private void messageReceived(final ClientManager clientManager) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (clientManager.getUserClient().getInfoMsg().isEmpty()) return;

                addLogText(clientManager.getUserClient().getInfoMsg());
                if (smsController != null) {
                    setSmsCouterCtrl(smsController.getSmsCount(), smsController.getSmsNum());
                }
            }
        });
    }




    private void messageReceived(final UserClient user) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (user.getInfoMsg().isEmpty()) return;

                addLogText(user.getInfoMsg());
                if (smsController != null) {
                    setSmsCouterCtrl(smsController.getSmsCount(), smsController.getSmsNum());
                }
            }
        });
    }



    private void messageInfo(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addLogText(msg);
              /*  if (smsController != null) {
                    setSmsCouterCtrl(smsController.getSmsCount(), smsController.getSmsNum());
                }*/
            }
        });
    }

    private void errMsgInfo(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                  addLogText(msg);
            }
        });
    }


    private void eventIU(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
               if (serverLog != null) {
                   serverLog.append(msg);
               }
            }
        });
        Toast.makeText(getApplicationContext(), "eventIU", Toast.LENGTH_SHORT).show();
    }




    public void addLogText(final String msg) {

       if (msg != null && serverLog != null) {
           serverLog.append(msg);

           final int scrollAmount = serverLog.getLayout().getLineTop(serverLog.getLineCount()) - serverLog.getHeight();
           // if there is no need to scroll, scrollAmount will be <=0
           if (scrollAmount > 0)
               serverLog.scrollTo(0, scrollAmount+serverLog.getLineHeight()*3);
           else
               serverLog.scrollTo(0, 0);
       }
       if (msg != null && stringBuilder != null) {
           stringBuilder.append(msg);
       }
    }




    private boolean isSmsServerStarted() {
     //   String str = smsTcpServer != null ? "started": "not started";
     //   Toast.makeText(this, str, Toast.LENGTH_LONG).show();
        return smsTcpServer!=null /*&& smsTcpServer.isStarted()*/;


    }




    private void setStartMenuBtnStat() {
       if (menuItem != null) {
           if (isSmsServerStarted()) {
               menuItem.setIcon(R.drawable.ic_stop_red_24dp);
           } else {
               menuItem.setIcon(R.drawable.ic_play_arrow_lime_24dp);
           }
       }
    }




    private void showCloseConfirmDialog() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_confirm_red_24dp)
                .setTitle(R.string.dialog_closing_app)
                .setMessage(R.string.dialog_confirm_exit)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton(R.string.dialog_no, null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);



        return true;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(this, "OK! "+requestCode, Toast.LENGTH_SHORT).show();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        menuItem = item;
        switch(id){
            case R.id.item_settings :
                /**Показать деятельность с настройками приложения*/
               /* Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);*/
                onShowSetts();
                return true;
            case R.id.item_start_server:
                /**Запустить или остановить сервер*/
                startServer();
                ShowLocalIp();
                return true;
            case R.id.item_downloads:
                onShowEmailer();
                return true;
            case R.id.item_black_list:
                /**Показать деятельность черного списка IP-адресов*/
                Intent intentBlockList = new Intent(this, IPList.class);

                if (blockedClientsList != null) {
                   intentBlockList.putParcelableArrayListExtra("intentBlockList",
                            (ArrayList<? extends Parcelable>) blockedClientsList);
                }
                startActivity(intentBlockList);
                return true;
            case  R.id.item_exit:
                /**Команда на завершение работы приложения*/
                showCloseConfirmDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_activity);

            serverLog = (TextView) findViewById(R.id.logText);
            smsCntView = (TextView)findViewById(R.id.smsCounterText);
            ipLocalView = (TextView)findViewById(R.id.ipLocalText);

            stringBuilder = new StringBuilder();

            //Прокрутка многострочного поля при добавлении новой строки
            serverLog.setMovementMethod(new ScrollingMovementMethod());

            serverLog.setText(Constants.SERVER_GREETING);
            Linkify.addLinks(serverLog, Linkify.WEB_URLS);
           // serverLog.setMovementMethod(LinkMovementMethod.getInstance());

            setSmsCouterCtrl(0, 0);




        //Получить и зарегистровать активити с настройками
        prefSetts = PreferenceManager.getDefaultSharedPreferences(this);
        prefSetts.registerOnSharedPreferenceChangeListener(this);


        //Ресивер, который получает сообщения из активити блоклиста для удаления IP-адреса из фильтра
        activityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                blockedClientsList = intent.getParcelableArrayListExtra("intentBlockList");
                String selIp = intent.getStringExtra("ipAddress");

             //   Toast.makeText(getApplicationContext(), selIp, Toast.LENGTH_SHORT).show();

                if (blockedClientsList != null && selIp != null && authListener != null) {
                    authListener.setBlockedClientsList(blockedClientsList);
                    authListener.removeClient(selIp);

                    blockedClientsList = authListener.getBlockedClientsList();
                } else {
                    //Toast.makeText(getApplicationContext(), "Not Ready", Toast.LENGTH_SHORT).show();
                }
            }
        };
      //  intentFilter = new IntentFilter(Constants.BLOCK_LIST_MAIN_ACT_MSG);



        intentFilter = new IntentFilter(Constants.BLOCK_LIST_MAIN_ACT_MSG);
        registerReceiver(activityReceiver, intentFilter);

        ShowLocalIp();

    }

    private void ShowLocalIp() {
        //Получить IP адрес WiFi
        ipLocalView.setText(String.format(getResources().getString(R.string.local_ip_mask), getLocalIP(),
                prefSetts.getString(Constants.PREF_LISTEN_PORT, "0")) );
    }

    //Обновить запись метки числа сообщений
    private void setSmsCouterCtrl(int curCnt, int commonCnt) {
        if (smsCntView != null) {
            smsCntView.setText(String.format(getString(R.string.control_sms_counter), curCnt, commonCnt));
        }
    }




    @Override
    protected void onResume() {
      /*  if (serverLog != null && stringBuilder != null) {
            serverLog.append(stringBuilder.toString());
        }*/
        super.onResume();
    }




    @Override
    protected void onDestroy() {
        unregisterReceiver(activityReceiver);
        prefSetts.unregisterOnSharedPreferenceChangeListener(this);

        Constants.MsgLog(getClass().getSimpleName(), "onDestroy(): " , Constants.MsgTypes.MSG);

        StopSmsServer();
        super.onDestroy();
    }





    //Показать деятельность с начтройкми
    public void onShowSetts() {
       Intent intent = new Intent(this, SettingsActivity.class);
       startActivity(intent);
    }

    /**Показать активность отправки почтового сообщения для скачавания файлов*/
    public  void onShowEmailer() {
        Intent intent = new Intent(this, EmaiActivity.class);
        startActivity(intent);
    }


    //Метод для старта SMS сервера
    private void startServer() {

        if (!isAllPermissionsWereSet()) return;

        if (serverLog == null || stringBuilder == null) return;

        if (getLocalIP().equals("0.0.0.0")) {
            Toast.makeText(getApplicationContext(), getString(R.string.switchon_wifi), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isSmsServerStarted()) {

            /**Если параметры сервера не устанолены, то вызвать окно настроек сервера*/
            if (prefSetts.getString(Constants.PREF_LISTEN_PORT, "").isEmpty()) {
                onShowSetts();
                return;
            }

            authListener = new AuthListener();

            int authAttempts = Integer.valueOf(prefSetts.getString(Constants.PREF_AUTH_ATT, "5"));
              authListener.setAttemptCount(authAttempts);
            int blockDuration = Integer.valueOf(prefSetts.getString(Constants.PREF_BLOCK_DUR, "10"));
              authListener.setLockDuration(blockDuration);


             int maxSms = Integer.parseInt(prefSetts.getString(Constants.PREF_MAX_SMS, "0"));
             setSmsCouterCtrl(0, maxSms);
             try {
                smsController = new SmsController(getApplicationContext(), maxSms);
                setSmsCouterCtrl(smsController.getSmsCountSetts(), maxSms);


                addLogText(INFO_SERVER_STARTED);

                smsTcpServer = new TcpServer(smsController, prefSetts, authListener, new OnMessageReceiveUI() {
                    @Override
                    public void updateClientList_UI(ClientManager user) {
                        updateClientList(user);
                    }

                    @Override
                    public void messageReceived_UI(ClientManager user) {
                        messageReceived(user);
                    }

                    @Override
                    public void messageReceived_UI(UserClient user) { messageReceived(user);}

                    @Override
                    public void messageInfo_UI(String msg) {
                        messageInfo(msg);
                    }

                    @Override
                    public void errorReceived_UI(String err) {
                        errMsgInfo(err);
                    }

                    @Override
                    public void blockedClients_UI(List<BlockedClients> blockedClients) {
                        blockedClientsList = blockedClients;
                    }

                });
                smsTcpServer.setDaemon(true);
                smsTcpServer.start();
                /**Заблокированные клиенты*/
                this.blockedClientsList = smsTcpServer.getBlockedClientsList();

                setStartMenuBtnStat();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            StopSmsServer();
        }
    }



    //Остановка SMS-сервера
    private void StopSmsServer() {
        smsController = null;
        conf = null;
        if (smsTcpServer != null) {
            try {
                smsTcpServer.StopServer();
                smsTcpServer = null;
                addLogText(INFO_SERVER_STOPPED);
                setStartMenuBtnStat();
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
    }

    //Проверить настройки на предмет заполненности
    private void validSetts(SharedPreferences pref, String key) {
        SharedPreferences.Editor editor;

       if (key.equals(Constants.PREF_LOGIN)) {

           String login = pref.getString(key, null);

           if (login == null || login.isEmpty()) {
               Toast.makeText(getApplicationContext(),
                       R.string.warn_emty_login, Toast.LENGTH_SHORT).show();
           }
       } else if (key.equals(Constants.PREF_PASS)) {

           String pass = pref.getString(key, null);

           if (pass == null || pass.isEmpty()) {
               Toast.makeText(getApplicationContext(),
                       R.string.warn_emty_pass, Toast.LENGTH_SHORT).show();
           }
       } else if (key.equals(Constants.PREF_BLOCK_DUR)) {

           String dur = pref.getString(key, null);

           if (dur == null || dur.isEmpty()) {
               Toast.makeText(getApplicationContext(),
                       R.string.warn_emty_duration, Toast.LENGTH_SHORT).show();
               editor = pref.edit();
               editor.putString(key, "3");
               editor.apply();
           } else if (Integer.valueOf(dur) == 0) {
               Toast.makeText(getApplicationContext(), R.string.warn_zero_duration, Toast.LENGTH_SHORT).show();
               editor = pref.edit();
               editor.putString(key, "3");
               editor.apply();
           }
       } else if (key.equals(Constants.PREF_ENCRYPT_EN)) {

           boolean encrypted = pref.getBoolean(key, false);
           String encryptEnable = encrypted ? getResources().getString(R.string.warn_encrypt_enable) :
                   getResources().getString(R.string.warn_encrypt_disable);

           Toast.makeText(getApplicationContext(), encryptEnable, Toast.LENGTH_SHORT).show();
       } else if (key.equals(Constants.PREF_SMS_TEST)) {

           boolean smsTestEnabled = pref.getBoolean(key, false);
           String smsStatus = smsTestEnabled ? getString(R.string.checkbox_pref_test_sms_summ_on):
                   getString(R.string.checkbox_pref_test_sms_summ_off);

           Toast.makeText(getApplicationContext(), smsStatus, Toast.LENGTH_SHORT).show();

       } else if (key.equals(Constants.PREF_AUTH_ATT)) {

           String attempts = pref.getString(key, null);

           if (attempts == null || attempts.isEmpty()) {
               Toast.makeText(getApplicationContext(),
                       R.string.warn_empty_attempts, Toast.LENGTH_SHORT).show();
               editor = pref.edit();
               editor.putString(key, "3");
               editor.apply();
           }
       } else if (key.equals(Constants.PREF_LISTEN_PORT)) {

           String port = pref.getString(key, null);

           if (port == null || port.isEmpty()) {
               Toast.makeText(getApplicationContext(),
                       R.string.warn_empty_listen_port, Toast.LENGTH_SHORT).show();
               editor = pref.edit();
               editor.putString(key, "11330");
               editor.apply();
           } else if (Integer.valueOf(port) <= 0) {
               Toast.makeText(getApplicationContext(),
                       R.string.warn_less_zero_listen_port, Toast.LENGTH_SHORT).show();
               editor = pref.edit();
               editor.putString(key, "11330");
               editor.apply();
           }
       } else if (key.equals(Constants.PREF_MAX_SMS)) {

           String max_sms = pref.getString(key, null);

           if (max_sms == null || max_sms.isEmpty()) {
               Toast.makeText(getApplicationContext(),
                       R.string.warn_emty_max_sms, Toast.LENGTH_SHORT).show();
           } else if (Integer.valueOf(max_sms) > Constants.maxSmsNum) {
               editor = pref.edit();
               editor.putString(key, String.valueOf(Constants.maxSmsNum));
               editor.apply();
               Toast.makeText(getApplicationContext(),
                      String.format(getResources().getString(R.string.warn_begger_than_5000), Constants.maxSmsNum) , Toast.LENGTH_SHORT).show();
           } else if (Integer.valueOf(max_sms) < 0) {
               editor = pref.edit();
               editor.putString(key, "100");
               editor.apply();
               Toast.makeText(getApplicationContext(),
                       R.string.warn_less_zero_max_sms, Toast.LENGTH_SHORT).show();
           } else if (Integer.valueOf(max_sms) == 0) {
               editor = pref.edit();
               editor.putString(key, "100");
               editor.apply();
               Toast.makeText(getApplicationContext(),
                       R.string.warn_zero_max_sms, Toast.LENGTH_SHORT).show();
           }
       }
    }



    //Проверить разрешения на отправку SMS и запрос местоположения
    private boolean isAllPermissionsWereSet() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    Constants.REQUEST_SEND_SMS_PERMISSION);
            return false;

        } else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    Constants.REQUEST_ACCESS_READ_PHONE_STATE);
            return false;
        }
        return true;
    }


    private class RememberActiviyViews implements Serializable {
        private TextView smsCounterView = null;
        private TextView logView = null;
        private StringBuilder strBuilder = null;


        public MenuItem getmItem() {
            return mItem;
        }

        private MenuItem mItem;

        public StringBuilder getStrBuilder() {
            return strBuilder;
        }

        public TextView getSmsCounterView() {
            return smsCounterView;
        }

        public TextView getLogView() {
            return logView;
        }

        public RememberActiviyViews() {}

        public void storeViews() {
            smsCounterView = smsCntView;
            logView = serverLog;
            strBuilder = stringBuilder;
            mItem = menuItem;
        }

    }

    //Вызывается каждый раз при попытке изменить любой параметр настройки, с целью проверить правильность ввода значения
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        validSetts(sharedPreferences, key);
    }
}
