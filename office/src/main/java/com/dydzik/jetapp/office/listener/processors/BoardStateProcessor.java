package com.dydzik.jetapp.office.listener.processors;

import com.dydzik.jetapp.common.bean.Airport;
import com.dydzik.jetapp.common.bean.Board;
import com.dydzik.jetapp.common.bean.Route;
import com.dydzik.jetapp.common.messages.AirportStateMessage;
import com.dydzik.jetapp.common.messages.BoardStateMessage;
import com.dydzik.jetapp.common.processor.MessageConvertor;
import com.dydzik.jetapp.common.processor.MessageProcessor;
import com.dydzik.jetapp.office.provider.AirportsProvider;
import com.dydzik.jetapp.office.provider.BoardProvider;
import com.dydzik.jetapp.office.service.WaitingRoutesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component("BOARD_STATE")
@RequiredArgsConstructor
public class BoardStateProcessor implements MessageProcessor<BoardStateMessage> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MessageConvertor messageConvertor;

    private final WaitingRoutesService waitingRoutesService;
    private final BoardProvider boardProvider;
    private final AirportsProvider airportsProvider;

    @Override
    public void process(String jsonMessage) {
        BoardStateMessage message = messageConvertor.extractMessage(jsonMessage, BoardStateMessage.class);
        Board board = message.getBoard();
        Optional<Board> previousOpt = boardProvider.getBoard(board.getName());
        Airport airport = airportsProvider.getAirport(board.getLocation());

        boardProvider.addBoard(board);
        if (previousOpt.isPresent() && board.hasRoute() && !previousOpt.get().hasRoute()) {
            Route route = board.getRoute();
            waitingRoutesService.remove(route);
        }

        if (previousOpt.isEmpty() || !board.isBusy() && previousOpt.get().isBusy()) {
            airport.addBoard(board.getName());
            kafkaTemplate.sendDefault(messageConvertor.toJson(new AirportStateMessage(airport)));
        }
    }
}
