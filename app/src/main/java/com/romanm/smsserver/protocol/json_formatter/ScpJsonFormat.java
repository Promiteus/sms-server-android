package com.romanm.smsserver.protocol.json_formatter;

import com.romanm.smsserver.Const.Constants;
import com.romanm.smsserver.model.UserClient;
import com.romanm.smsserver.protocol.ScpTCP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class ScpJsonFormat {



    public static String createAuthJsonString(UserClient user) throws JSONException {
        if (user == null) return null;

        JSONObject authJson = new JSONObject();

        JSONObject innerJson = new JSONObject();

          innerJson.put(ScpTCP.KEY_AUTHORIZED, user.isAuthorized());
          innerJson.put(ScpTCP.KEY_COMMENTS, user.getComments());
          innerJson.put(ScpTCP.KEY_SOCK_CLOSE, user.isCloseSocket());

        authJson.put(ScpTCP.PROT_AUTH, innerJson);

        return authJson.toString();
    }


    /**
     * {"connection":{"comment":"", "closeSocket":"true"}}
     * */

    public static String createConnStatJsonString(UserClient user) throws JSONException {

        if (user == null) return null;
        JSONObject connJson = new JSONObject();

        JSONObject innerJson = new JSONObject();

        innerJson.put(ScpTCP.KEY_COMMENTS, user.getComments());
        innerJson.put(ScpTCP.KEY_SOCK_CLOSE, user.isCloseSocket());
        innerJson.put(ScpTCP.PROT_CODE, user.getErrCode());

        connJson.put(ScpTCP.PROT_CONNECTION, innerJson);



        return connJson.toString();
    }




    public static String createGreetingJsonString(UserClient user) throws JSONException {

        if (user == null) return null;
        JSONObject greetingJson = new JSONObject();

        JSONObject innerJson = new JSONObject();
          innerJson.put(ScpTCP.KEY_CRYPT, user.isEncryptEnabled());
          innerJson.put(ScpTCP.KEY_COMMENTS, user.getComments());
          innerJson.put(ScpTCP.KEY_AUTHORIZED, user.isAuthorized());
        greetingJson.put(ScpTCP.PROT_HELLO, innerJson);

        return greetingJson.toString();
    }

    /**
     * {"error":{"errType":"", "errMsg":"", "closeSocket":"true"}}
     * */

    public static String createErrorJsonString(UserClient user) throws JSONException {
        if (user == null) return null;
        JSONObject errJson = new JSONObject();
        JSONObject innerJson = new JSONObject();
           innerJson.put(ScpTCP.KEY_ERR_MSG, user.getErrMsg());
           innerJson.put(ScpTCP.KEY_ERR_TYPE, user.getErrType());
           innerJson.put(ScpTCP.KEY_SOCK_CLOSE, user.isCloseSocket());
        errJson.put(ScpTCP.PROT_UNKNOWN, innerJson);
        return errJson.toString();
    }



    /**
     * {"greeting":{"encryption":"false", "comment":""}}
     * */

    public static void getJsonGreetingData(String json, UserClient user) throws JSONException {
        if (user == null) return;

      //  Constants.MsgLog("log","Json: "+json, Constants.MsgTypes.MSG);

        JSONObject greetingJson = new JSONObject(json);

        JSONObject greet = (JSONObject) greetingJson.get(ScpTCP.PROT_HELLO);

        user.setComments(greet.get(ScpTCP.KEY_COMMENTS).toString());
        user.setEncryptEnabled(Boolean.parseBoolean(greet.get(ScpTCP.KEY_CRYPT).toString()));
    }

    /**
     * {"sms":{"smsMessage":"Any message for sending",
     *  "tels":["",""], "comment":"sending"}}
     *
     * */

    public static void getJsonSmsData(String json, UserClient user) throws JSONException {
        if (user == null) return;
        JSONObject joSms = new JSONObject(json);
        JSONObject joSmsValues = (JSONObject) joSms.get(ScpTCP.PROT_SMS);

        user.setSmsMsg(joSmsValues.getString(ScpTCP.KEY_SMS_MSG));
        user.setComments(joSmsValues.getString(ScpTCP.KEY_COMMENTS));
        JSONArray tels = joSmsValues.getJSONArray(ScpTCP.KEY_TEL_LIST);

        for (int i = 0; i < tels.length(); i++) {
            user.getTelNumbers().add(tels.getString(i));
        }
    }

    /**
     * {"auth":{"username":"123", "password":"123", "comment":""}}
     *
     * */

    public static void getJsonAuthData(String json, UserClient user) throws JSONException {
        if (user == null) return;
        JSONObject joAuth = new JSONObject(json);
        JSONObject joAuthValues = (JSONObject) joAuth.get(ScpTCP.PROT_AUTH);

        user.setPassword(joAuthValues.get(ScpTCP.KEY_PASS).toString());
        user.setComments(joAuthValues.get(ScpTCP.KEY_COMMENTS).toString());
        user.setLogin(joAuthValues.get(ScpTCP.KEY_LOGIN).toString());
    }

    /**
     * {"connection":{"comment":"", "closeSocket":"true"}}
     * */

    public static void getJsonConnectionData(String json, UserClient user) throws JSONException {
        if (user == null) return;
        JSONObject joConnection = new JSONObject(json);
        JSONObject joConnectionValues = (JSONObject) joConnection.get(ScpTCP.PROT_CONNECTION);

        user.setComments(joConnectionValues.get(ScpTCP.KEY_COMMENTS).toString());
        user.setCloseSocket(Boolean.parseBoolean(joConnectionValues.get(ScpTCP.KEY_SOCK_CLOSE).toString()));
    }




}

