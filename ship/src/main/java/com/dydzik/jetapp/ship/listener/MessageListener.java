package com.dydzik.jetapp.ship.listener;

import com.dydzik.jetapp.common.messages.Message;
import com.dydzik.jetapp.common.processor.MessageConvertor;
import com.dydzik.jetapp.common.processor.MessageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {

    private final MessageConvertor messageConvertor;

    @Autowired
    private Map<String, MessageProcessor<? extends Message>> processors = new HashMap<>();

    @KafkaListener(id="BoardId", topics="officeRoutes")
    public void radarListener(String data){
        String code = messageConvertor.extractCode(data);
        processors.values().forEach(System.out::println);
        try {
            processors.get(code).process(data);
        } catch (Exception e) {
            log.error("Code: " + code + " . " +  e.getLocalizedMessage());
        }
    }

}
