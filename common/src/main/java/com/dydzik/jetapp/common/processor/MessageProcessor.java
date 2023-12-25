package com.dydzik.jetapp.common.processor;

import com.dydzik.jetapp.common.messages.Message;

public interface MessageProcessor<T extends Message> {

    void process(String jsonMessage);
}
