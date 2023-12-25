package com.dydzik.jetapp.common.messages;

import com.dydzik.jetapp.common.bean.Airport;
import com.dydzik.jetapp.common.bean.Board;
import com.dydzik.jetapp.common.bean.Source;
import com.dydzik.jetapp.common.bean.Type;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardStateMessage extends Message {
    private Board board;

    public BoardStateMessage() {
        this.source = Source.BOARD;
        this.type = Type.STATE;
    }

    public BoardStateMessage(Board value) {
        this();
        this.board = value;
    }
}
