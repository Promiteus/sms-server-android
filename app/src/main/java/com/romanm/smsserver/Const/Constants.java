package com.romanm.smsserver.Const;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

import com.romanm.smsserver.R;

public class Constants {

    public static final int REQUEST_SEND_SMS_PERMISSION = 1000;
    public static final int REQUEST_ACCESS_READ_PHONE_STATE = 1001;

    public static final String CONFIG_PROPERTIES_PATHS = "cfg.properties";

    public static final String ERR_UNKNOWN_CMD = "Неизвестная команда: %s\n";
    public static final String ERR_UNKNOWN_CMD_TYPE = "Unknown command. ";


    public static final String CONF_SMS_NUM = "sms_num";
    public static final String CONF_SMS_COUNT = "sms_cnt";
    public static final String CONF_LAST_DATE = "last_date";
    public static final String SETTS_PREFS = "main_setts";

    public static final int secondsDay = 86400;


    public static final String ERR_LOG = "ERR_INFO";
    public static final String MSG_INFO = "LOG_INFO";

    public static final String SMS_OK = "\nSMS на номер \"%s\" отправлено!";

    public static final String NET_PARS = "%s:%d >> ";

    public static final String INFO_NEW_CONNECTION = "[Client] %s << [%s:%d] << Новое соединение.\n";
    public static final String INFO_HELLO_SERVER = "[Server] %s -- Приветствую тебя, %s клиент.\n";
    public static final String INFO_AUTHORIZED_CLIENT = "авторизованный";
    public static final String INFO_UNAUTHORIZED_CLIENT = "неавторизованный";

    public static final String GREATING_FOR_CLIENT = "Hello, client! It's ok!";

    public static final String ERR_INTERNAL = "%s -- [Type: %s]: %s";
    public static final String ERR_SEND_SMS = "\n[Server] Error %s -- Ошибка при отправки SMS: %s.\n";

    public static final String INFO_AUTH_DENINE = "[Server] %s -- Логин: %s или пароль: %s не верны! \n";
    public static final String INFO_AUTH_OK = "[Server] %s -- Клиент [%s] авторизован!\n";

    public static final String INFO_WRONG_PROTOCOL = "Не соблюдается протокол...]\n";
    public static final String INFO_CANT_PARSE_JSON = "Не могу распарсить данные: \n %s\n";
    public static final String INFO_NULL_CONFIGS = "Конфигурации сервера не были получены!\n";

    public static final String INFO_ADDRESS_LIST_FOR_MSG = "Получено %d адресатов для" +
            " передачи сообщения:\n%s\n******************\nТекст письма: %s\n******************\n";
    public static final String INFO_CLIENT_CLOSING = "[Server] -- Соединение с клиентом [%s:%d] закрыто...\n --------------------------\n\n";
    public static final String INFO_STARTING_SMS_SENDING = "[Server] -- Приступаю к отправке SMS сообщений...\n";
    public static final String INFO_ALL_MSG_SENT = "Сообщения отправлены адресатам!\n";
    public static final String INFO_LISTEN_PORT = "[Server] >> 0.0.0.0:%s >> Ожидаю запросы клиентов...\n" +
            "--------------------------\n\n";

    public static final String INFO_SERVER_STARTED = "\nSMS-сервер запущен...\n";
    public static final String INFO_SERVER_STOPPED = "\nSMS-сервер остановлен.\n";

    public static final String BLOCK_LIST_MAIN_ACT_MSG = "android.mymessage.TO_MAIN_ACT";

    public static final String EMAIL_BODY = "<p>По ссылкам внизу вы можете скачать как исходники SMS клиента, чтобы\n" +
            "     использовать его в своих проектах, так и готовые приложения для Windows для отправки SMS.</p>\n" +
            "    Исходники SMS клиента на <b>Java</b> и <b>Delphi FMX</b> можно скачать тут: https://github.com/Promiteus/sms_client/tree/master/source_ru <br/>\n" +
            "    Программы для рассылки SMS:<br/>\n" +
            "    1. Консольная программа для выполнения в командной строке: <br/>\n" +
            "        https://drive.google.com/open?id=17N07DrhFFk-WxwZ9Pre0yg06qSD3bgth <br/>\n" +
            "        https://yadi.sk/d/U_o4qQl8xv0GAA <br/>\n" +
            "    2. Программа с привычным графическим интерфейсом:<br/>\n" +
            "        https://drive.google.com/open?id=17NHt6sGPqm_3fZjSkeLGwBHVZ0DIMJtZ <br/>\n" +
            "        https://yadi.sk/d/4eJxEOzfX5nLeg <br/>";

    public static final String SERVER_GREETING = "\nПеред использованием приложения ознакомьтесь с" +
            " Политикой конфиденциальности: http://transporter-app.ru/docs/privacy.htm \n";

    public static final String PREF_LOGIN = "edit_login";
    public static final String PREF_PASS = "edit_pass";
    public static final String PREF_BLOCK_DUR = "edit_block_duration";
    public static final String PREF_AUTH_ATT = "edit_auth_attempts";
    public static final String PREF_ENCRYPT_EN = "enable_encrypt";
    public static final String PREF_LISTEN_PORT = "edit_listen_port";
    public static final String PREF_MAX_SMS = "edit_max_sms";
    public static final String PREF_SMS_TEST = "enable_sms_test";

    public static final String defaultPort = "12200";
    public static final int maxSmsNum = 6000;


    public enum MsgTypes {
        ERR, MSG
    }



    public static void MsgLog(String className, String msg, MsgTypes type) {
        if (type == MsgTypes.ERR) {
            Log.d(ERR_LOG, String.format("%s: %s", className, msg));
        } else {
            Log.d(MSG_INFO, String.format("%s: %s", className, msg));
        }
    }


}
