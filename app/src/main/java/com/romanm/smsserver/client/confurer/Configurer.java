package com.romanm.smsserver.client.confurer;

import android.content.Context;
import android.util.Log;


import com.romanm.smsserver.Const.Constants;
import com.romanm.smsserver.model.Configs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configurer {
    private Configs configs;
    private Properties props;
    private FileInputStream fileConf;
    private InputStream inStream = null;



    public Configs getConfigs() {
      /**  if (props != null && configs != null) {
            configs.setLogin(props.getProperty(Constants.CONF_LOGIN));
            configs.setPassword(props.getProperty(Constants.CONF_PASS));
            configs.setEncryption(Boolean.getBoolean(props.getProperty(Constants.CONF_ENCRYPTION)));
            configs.setUsersCnt(Integer.getInteger(props.getProperty(Constants.CONF_USERS_CNT)));
            configs.setSmsNum(Integer.getInteger(props.getProperty(Constants.CONF_SMS_NUM)));
            configs.setConnectionPort(props.getProperty(Constants.CONF_CONN_PORT));*/

           // return configs;
       // }
      //  Constants.MsgLog(getClass().getSimpleName(), "configs = null", Constants.MsgTypes.MSG);
        return null;
    }

    public void setConfigs(Configs configs) {
      /*  if (props != null && configs != null) {
            props.setProperty(Constants.CONF_LOGIN, configs.getLogin());
            props.setProperty(Constants.CONF_PASS, configs.getPassword());
            props.setProperty(Constants.CONF_SMS_NUM, String.valueOf(configs.getSmsNum()));
            props.setProperty(Constants.CONF_USERS_CNT, String.valueOf(configs.getUsersCnt()));
            props.setProperty(Constants.CONF_ENCRYPTION, String.valueOf(configs.isEncryption()));
            props.setProperty(Constants.CONF_CONN_PORT, configs.getConnectionPort());
        }*/
        this.configs = configs;
    }



    public Configurer(Configs configs, Context context) throws IOException {
        props = new Properties();
          inStream = context.getAssets().open(Constants.CONFIG_PROPERTIES_PATHS);
          props.load(inStream);

        this.configs = configs;
    }
}
