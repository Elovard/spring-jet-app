package com.dydzik.jetapp.ship.listener.processor;

import com.dydzik.jetapp.common.messages.BoardStateMessage;
import com.dydzik.jetapp.common.messages.OfficeStateMessage;
import com.dydzik.jetapp.common.processor.MessageConvertor;
import com.dydzik.jetapp.common.processor.MessageProcessor;
import com.dydzik.jetapp.ship.provider.BoardProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("OFFICE_STATE")
@RequiredArgsConstructor
public class OfficeStateProcessor implements MessageProcessor<OfficeStateMessage> {

    private final MessageConvertor messageConvertor;
    private final BoardProvider boardProvider;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void process(String jsonMessage) {
        boardProvider.getBoards().forEach(board -> {
            kafkaTemplate.sendDefault(messageConvertor.toJson(new BoardStateMessage(board)));
        });
    }
}
