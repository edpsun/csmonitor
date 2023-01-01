package com.gmail.edpsun.hystock.inbound.parser;

import com.gmail.edpsun.hystock.inbound.InboundContext;

public interface ContextAware {
    public void setContext(InboundContext ctx);
}
