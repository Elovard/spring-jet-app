package com.dydzik.jetapp.common.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoutePoint {
    private String name;
    private double x;
    private double y;

    public RoutePoint(Airport airport) {
        this.x = airport.getX();
        this.y = airport.getY();
        this.name = airport.getName();
    }
}
