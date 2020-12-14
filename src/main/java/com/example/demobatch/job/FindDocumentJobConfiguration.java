package com.example.demobatch.job;

import com.example.demobatch.FirebaseClient;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class FindDocumentJobConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final FirebaseClient firebaseClient;

  private final String COLLECTION_ALPHA = "alpha";
  private final String DOCUMENT_ORDER = "order";
  private final String COLLECTION_STATUS = "status";

  private final String DOC_ORDER_ID = "orderID";
  private final String DOC_STATUS = "status";
  private final String ODST000003 = "ODST000003";
  private final String ODST000013 = "ODST000013";
  private final String ODST000005 = "ODST000005";

  @Bean
  public Job findDocumentJob(){
    return jobBuilderFactory.get("findDocumentJob")
        .start(findDocumentStep())
        .build();
  }

  @Bean
  public Step findDocumentStep() {
    return stepBuilderFactory.get("findDocumentStep")
        .tasklet((contribution, chunkContext) -> {
          ApiFuture<QuerySnapshot> snapshots = firebaseClient.getFirestoreDB()
              .collection(COLLECTION_ALPHA).document(DOCUMENT_ORDER).collection(COLLECTION_STATUS)
              .get();

          List<QueryDocumentSnapshot> documents = snapshots.get().getDocuments();
          List<QueryDocumentSnapshot> collect = documents.stream()
              .filter(document -> ODST000005.equals(document.getData().get(DOC_STATUS)))
              .collect(Collectors.toList());
          log.info(">>>>>>>>>>>>>>> count : {}", collect.size());

          for (QueryDocumentSnapshot queryDocumentSnapshot : collect) {
            OrderDto orderDto = queryDocumentSnapshot.toObject(OrderDto.class);
            log.info(">>>>>>>>>>>>>>> id : {}", orderDto.getOrderId());
            log.info(">>>>>>>>>>>>>>> status : {}", orderDto.getStatus());
          }

          return RepeatStatus.FINISHED;
        })
        .build();
  }
}
