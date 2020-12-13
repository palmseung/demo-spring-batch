package com.example.demobatch.job;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SimpleDeleteJobConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final EntityManager entityManager;

  @Bean
  public Job SimpleJob(){
    return jobBuilderFactory.get("simpleJob")
        .start(simpleDeleteStep())
        .build();
  }

  @Bean
  @JobScope
  public Step simpleDeleteStep() {
    return stepBuilderFactory.get("simpleStep1")
        .tasklet((contribution, chunkContext) -> {
          Query delete_from_pay = entityManager.createQuery("DELETE FROM Pay");
          int i = delete_from_pay.executeUpdate();
          log.info(">>>>>>>>>>>>>>> Deleted rows count{} ", i);
          return RepeatStatus.FINISHED;
        })
        .build();
  }

}
