package com.dydzik.jetapp.common.processor;

import com.dydzik.jetapp.common.messages.Message;
import com.google.gson.Gson;

public class MessageConvertor {

    private final Gson gson = new Gson();

    public String extractCode(String data) {
        return gson.fromJson(data, Message.class).getCode();
    }

    public <T extends Message> T extractMessage(String data, Class<T> clazz) {
        return gson.fromJson(data, clazz);
    }

    public String toJson(Object message) {
        return gson.toJson(message);
    }
}
