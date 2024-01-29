package com.dydzik.jetapp.ship.configuration;

import com.dydzik.jetapp.common.processor.MessageConvertor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagesConfiguration {

    @Bean
    public MessageConvertor convertor() {
        return new MessageConvertor();
    }
}
