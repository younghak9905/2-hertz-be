package com.hertz.hertz_be.global.batch;

import com.hertz.hertz_be.domain.alarm.service.AlarmService;
import com.hertz.hertz_be.domain.tuningreport.entity.TuningReport;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TuningReportVisibilityWriter implements ItemWriter<TuningReport> {

    private final AlarmService alarmService;

    @Override
    public void write(Chunk<? extends TuningReport> chunk) {
        for (TuningReport report : chunk.getItems()) {
            report.setVisible();

            // 구현할 알람 서비스
            // alarmService.sendTuningReportVisibleAlarm(report);
        }
    }
}
