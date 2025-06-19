package com.hertz.hertz_be.global.batch;

import com.hertz.hertz_be.domain.tuningreport.entity.TuningReport;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TuningReportVisibilityReader {

    private final EntityManagerFactory entityManagerFactory;
    private static final int CHUNK_SIZE = 20;

    public JpaPagingItemReader<TuningReport> reader() {
        LocalDateTime limitDate = LocalDateTime.now().minusDays(30);

        return new JpaPagingItemReaderBuilder<TuningReport>()
                .name("tuningReportVisibilityReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                    SELECT r FROM TuningReport r
                    WHERE r.isVisible = false
                      AND r.createdAt >= :startDate
                """)
                .parameterValues(Map.of("startDate", limitDate))
                .pageSize(CHUNK_SIZE)
                .build();
    }
}
