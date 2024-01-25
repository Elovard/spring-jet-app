package com.dydzik.jetapp.office.listener.processors;

import com.dydzik.jetapp.common.messages.AirportStateMessage;
import com.dydzik.jetapp.common.messages.OfficeStateMessage;
import com.dydzik.jetapp.common.processor.MessageConvertor;
import com.dydzik.jetapp.common.processor.MessageProcessor;
import com.dydzik.jetapp.office.provider.AirportsProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("OFFICE_STATE")
@RequiredArgsConstructor
public class OfficeStateProcessor implements MessageProcessor<OfficeStateMessage> {

    private final MessageConvertor messageConvertor;
    private final AirportsProvider airportsProvider;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void process(String jsonMessage) {
        airportsProvider.getPorts().forEach(airport -> {
            kafkaTemplate.sendDefault(messageConvertor.toJson(new AirportStateMessage(airport)));
        });
    }
}
