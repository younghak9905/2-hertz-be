package com.hertz.hertz_be.global.batch;

import com.hertz.hertz_be.domain.tuningreport.entity.TuningReport;
import com.hertz.hertz_be.global.exception.AiServerBadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class TuningReportVisibilityJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final TuningReportVisibilityReader tuningReportVisibilityReader;
    private final TuningReportVisibilityWriter tuningReportVisibilityWriter;

    private static final int CHUNK_SIZE = 20;

    @Bean
    public Job tuningReportVisibilityJob() {
        return new JobBuilder("TuningReportVisibilityJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(updateVisibilityStep())
                .build();
    }

    @Bean
    public Step updateVisibilityStep() {
        return new StepBuilder("TuningReportVisibilityStep", jobRepository)
                .<TuningReport, TuningReport>chunk(CHUNK_SIZE, transactionManager)
                .reader(tuningReportVisibilityReader.reader())
                .writer(tuningReportVisibilityWriter)
                .faultTolerant()
                .retryLimit(3)
                .retry(AiServerBadRequestException.class)
                .build();
    }

}
