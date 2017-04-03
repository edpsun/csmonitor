package com.edpsun.st.monitor.data;

import java.util.List;

public interface DataParser<S,T> {
    List<T> parse(S source);
}
