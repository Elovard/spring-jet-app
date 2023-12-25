package com.dydzik.jetapp.common.messages;

import com.dydzik.jetapp.common.bean.Source;
import com.dydzik.jetapp.common.bean.Type;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Message {
    protected Type type;
    protected Source source;

    public String getCode() {
        return source.name() + " " + type.name();
    }
}
