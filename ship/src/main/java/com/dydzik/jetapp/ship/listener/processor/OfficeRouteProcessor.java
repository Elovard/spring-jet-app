package com.dydzik.jetapp.ship.listener.processor;

import com.dydzik.jetapp.common.bean.Route;
import com.dydzik.jetapp.common.messages.OfficeRouteMessage;
import com.dydzik.jetapp.common.processor.MessageConvertor;
import com.dydzik.jetapp.common.processor.MessageProcessor;
import com.dydzik.jetapp.ship.provider.BoardProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("OFFICE_ROUTE")
@RequiredArgsConstructor
public class OfficeRouteProcessor implements MessageProcessor<OfficeRouteMessage> {

    private final MessageConvertor messageConvertor;
    private final BoardProvider boardProvider;

    @Override
    public void process(String jsonMessage) {
        OfficeRouteMessage msg = messageConvertor.extractMessage(jsonMessage, OfficeRouteMessage.class);
        Route route = msg.getRoute();
        boardProvider.getBoards().stream()
                .filter(board -> board.noBusy() && route.getBoardName().equals(board.getName()))
                .findFirst()
                .ifPresent(board -> {
                    board.setRoute(route);
                    board.setBusy(true);
                    board.setLocation(null);
                });
    }
}
