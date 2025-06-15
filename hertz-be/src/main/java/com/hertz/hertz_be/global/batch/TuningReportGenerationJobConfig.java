package com.hertz.hertz_be.global.batch;

import com.hertz.hertz_be.domain.channel.entity.SignalRoom;
import com.hertz.hertz_be.domain.tuningreport.entity.TuningReport;
import com.hertz.hertz_be.global.exception.AiServerBadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
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
public class TuningReportGenerationJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final TuningReportGenerationReader tuningReportGenerationReader;
    private final TuningReportGenerationWriter tuningReportGenerationWriter;
    private final TuningReportGenerationProcessor tuningReportGenerationProcessor;
    private final TuningReportVisibilityReader tuningReportVisibilityReader;
    private final TuningReportVisibilityWriter tuningReportVisibilityWriter;

    private static final int CHUNK_SIZE = 20;

    @Bean
    public Job tuningReportGenerationJob() {
        return new JobBuilder("TuningReportGenerationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(tuningReportGenerationStep(null))
                .build();
    }

    @Bean
    @StepScope
    public Step tuningReportGenerationStep(
            @Value("#{jobParameters['category']}") String category
    )
    {
        return new StepBuilder("TuningReportGenerationStep", jobRepository)
                .<SignalRoom, TuningReport>chunk(CHUNK_SIZE, transactionManager)
                .reader(tuningReportGenerationReader.reader(category))
                .processor(tuningReportGenerationProcessor)
                .writer(tuningReportGenerationWriter)
                .faultTolerant()
                .retryLimit(3)
                .retry(AiServerBadRequestException.class)
                .build();
    }

    @Bean
    public Job tuningReportGenerationJobForFriendTest() {
        return new JobBuilder("TuningReportGenerationJobForFriendTest", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(tuningReportGenerationStepForFriendTest())
                .build();
    }

    @Bean
    public Step tuningReportGenerationStepForFriendTest() {
        return new StepBuilder("TuningReportGenerationStepForTest", jobRepository)
                .<SignalRoom, TuningReport>chunk(CHUNK_SIZE, transactionManager)
                .reader(tuningReportGenerationReader.reader("FRIEND"))
                .processor(tuningReportGenerationProcessor)
                .writer(tuningReportGenerationWriter)
                .build();
    }

    @Bean
    public Job tuningReportGenerationJobForCoupleTest() {
        return new JobBuilder("tuningReportGenerationJobForCoupleTest", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(tuningReportGenerationStepForFriendTest())
                .build();
    }

    @Bean
    public Step tuningReportGenerationStepForCoupledTest() {
        return new StepBuilder("TuningReportGenerationStepForTest", jobRepository)
                .<SignalRoom, TuningReport>chunk(CHUNK_SIZE, transactionManager)
                .reader(tuningReportGenerationReader.reader("COUPLE"))
                .processor(tuningReportGenerationProcessor)
                .writer(tuningReportGenerationWriter)
                .build();
    }

    @Bean
    public Job tuningReportVisibilityJobForTest() {
        return new JobBuilder("tuningReportVisibilityJobForTest", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(tuningReportVisibilityStepForTest())
                .build();
    }

    @Bean
    public Step tuningReportVisibilityStepForTest() {
        return new StepBuilder("tuningReportVisibilityStepForTest", jobRepository)
                .<TuningReport, TuningReport>chunk(CHUNK_SIZE, transactionManager)
                .reader(tuningReportVisibilityReader.reader())
                .writer(tuningReportVisibilityWriter)
                .build();
    }

}