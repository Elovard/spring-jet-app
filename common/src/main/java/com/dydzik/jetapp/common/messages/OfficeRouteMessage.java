package com.dydzik.jetapp.common.messages;

import com.dydzik.jetapp.common.bean.Airport;
import com.dydzik.jetapp.common.bean.Route;
import com.dydzik.jetapp.common.bean.Source;
import com.dydzik.jetapp.common.bean.Type;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfficeRouteMessage extends Message {
    private Route route;

    public OfficeRouteMessage() {
        this.source = Source.OFFICE;
        this.type = Type.ROUTE;
    }

    public OfficeRouteMessage(Route route) {
        this();
        this.route = route;
    }
}
