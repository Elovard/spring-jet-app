package com.dydzik.jetapp.office.service;

import com.dydzik.jetapp.common.bean.Route;
import com.dydzik.jetapp.common.bean.RoutePath;
import com.dydzik.jetapp.common.bean.RoutePoint;
import com.dydzik.jetapp.office.provider.AirportsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PathService {

    private final AirportsProvider airportsProvider;

    public RoutePath makePath(String from, String to) {
        return new RoutePath(airportsProvider.getRoutePoint(from), airportsProvider.getRoutePoint(to), 0);
    }

    public Route convertLocationsToRoute(List<String> locations) {
        Route route = new Route();
        List<RoutePath> path = new ArrayList<>();
        List<RoutePoint> points = new ArrayList<>();

        locations.forEach(location -> {
            airportsProvider.getPorts().stream()
                    .filter(airport -> airport.getName().equals(location))
                    .findFirst()
                    .ifPresent(airport -> points.add(new RoutePoint(airport)));
        });

        for (int i = 0; i < points.size() - 1; i++) {
            path.add(new RoutePath());
        }

        path.forEach(row -> {
            int ind = path.indexOf(row);
            if (row.getFrom() == null) {
                row.setFrom(points.get(ind));
                if (points.size() > ind + 1) {
                    row.setTo(points.get(ind + 1));
                } else {
                    row.setTo(points.get(ind));
                }
            }
        });

        route.setPath(path);

        return route;
    }
}
