package com.hcmute.prse_be.response;

import net.minidev.json.JSONObject;

public class Response {

    public static JSONObject success(JSONObject data) {
        JSONObject error_message = new JSONObject();
        JSONObject json = new JSONObject();
        json.put("code", 1);
        json.put("error_message", error_message);
        json.put("data", data);
        return json;
    }

    public static JSONObject success() {
        JSONObject error_message = new JSONObject();
        JSONObject json = new JSONObject();
        json.put("code", 1);
        json.put("error_message", error_message);
        json.put("data", new JSONObject());
        return json;
    }

    public static JSONObject error(String message) {
        JSONObject json = new JSONObject();
        json.put("code", 0);
        json.put("error_message", message);
        json.put("data", new JSONObject());
        return json;
    }

    public static JSONObject logout(String message) {
        JSONObject json = new JSONObject();
        json.put("code", -1);
        json.put("error_message", message);
        json.put("data", new JSONObject());
        return json;
    }

    public static JSONObject requestLogin(String message) {
        JSONObject json = new JSONObject();
        json.put("code", -2);
        json.put("error_message", message);
        json.put("data", new JSONObject());
        return json;
    }
}
