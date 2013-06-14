package com.gmail.edpsun.hystock.intf;

import com.gmail.edpsun.hystock.inbound.InboundContext;

public interface Processor {
    int process(InboundContext ctx);
}
