package com.edpsun.st.monitor.data;

import lombok.NonNull;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

public interface DataRetriever {
    default String getData(@NonNull final String id, @NonNull final LocalDate date) {
        return getData(id, date, date);
    }

    String getData(@NonNull final String id, @NonNull final LocalDate startDate, @NonNull final LocalDate endDate);
}
