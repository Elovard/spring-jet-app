package com.dydzik.jetapp.office.job;

import com.dydzik.jetapp.common.bean.Airport;
import com.dydzik.jetapp.common.bean.Board;
import com.dydzik.jetapp.common.bean.Route;
import com.dydzik.jetapp.common.bean.RoutePath;
import com.dydzik.jetapp.common.messages.AirportStateMessage;
import com.dydzik.jetapp.common.messages.OfficeRouteMessage;
import com.dydzik.jetapp.common.processor.MessageConvertor;
import com.dydzik.jetapp.office.provider.AirportsProvider;
import com.dydzik.jetapp.office.provider.BoardProvider;
import com.dydzik.jetapp.office.service.PathService;
import com.dydzik.jetapp.office.service.WaitingRoutesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class RouteDistributeJob {

    private final PathService pathService;
    private final BoardProvider boardProvider;
    private final WaitingRoutesService waitingRoutesService;
    private final AirportsProvider airportsProvider;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MessageConvertor messageConvertor;

    @Scheduled(initialDelay = 500, fixedDelay = 2500)
    private void distribute() {
        waitingRoutesService.list().stream()
                .filter(Route::notAssigned)
                .forEach(route -> {
                    String startLocation = route.getPath().get(0).getFrom().getName();

                    boardProvider.getBoards().stream()
                            .filter(board -> startLocation.equals(board.getLocation()) && board.noBusy())
                            .findFirst()
                            .ifPresent(board -> sendBoardToRoute(route, board));

                    if (route.notAssigned()) {
                        boardProvider.getBoards().stream()
                                .filter(Board::noBusy)
                                .findFirst()
                                .ifPresent(board -> {
                                    String currentLocation = board.getLocation();
                                    if (!currentLocation.equals(startLocation)) {
                                        RoutePath routePath = pathService.makePath(currentLocation, startLocation);
                                        route.getPath().add(0, routePath);
                                    }

                                    sendBoardToRoute(route, board);
                                });
                    }
                });
    }

    private void sendBoardToRoute(Route route, Board board) {
        route.setBoardName(board.getName());
        Airport airport = airportsProvider.findAirportAndRemoveBoard(board.getName());
        board.setLocation(null);
        kafkaTemplate.sendDefault(messageConvertor.toJson(new OfficeRouteMessage(route)));
        kafkaTemplate.sendDefault(messageConvertor.toJson(new AirportStateMessage(airport)));
    }
}
