package com.example.demobatch.job;

import com.example.demobatch.FirebaseClient;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
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
public class FindAndDeleteDocumentJobConfiguration {

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
  public Job findAndDeleteDocumentJob(){
    return jobBuilderFactory.get("findAndDeleteDocumentJob")
        .start(findAndDeleteStep())
        .build();
  }

  @Bean
  public Step findAndDeleteStep() {
    return stepBuilderFactory.get("findAndDeleteStep")
        .tasklet((contribution, chunkContext) -> {
          ApiFuture<QuerySnapshot> snapshots = firebaseClient.getFirestoreDB()
              .collection(COLLECTION_ALPHA).document(DOCUMENT_ORDER).collection(COLLECTION_STATUS)
              .get();

          //find
          List<QueryDocumentSnapshot> documents = snapshots.get().getDocuments();
          List<QueryDocumentSnapshot> collect = documents.stream()
              .filter(document -> ODST000013.equals(document.getData().get(DOC_STATUS)))
              .collect(Collectors.toList());
          log.info(">>>>>>>>>>>>>>> count to find/delete : {}", collect.size());

          for (QueryDocumentSnapshot document : collect) {
            OrderDto orderDto = document.toObject(OrderDto.class);
            log.info(">>>>>>>>>>>>>>> id : {}", orderDto.getOrderId());
            log.info(">>>>>>>>>>>>>>> status : {}", orderDto.getStatus());


            //delete
            ApiFuture<WriteResult> delete = document.getReference().delete();
            Timestamp deleteTime = delete.get().getUpdateTime();
            log.info(">>>>>>>>>>>>>>> Timestamp to delete : {}", deleteTime);
          }

          return RepeatStatus.FINISHED;
        })
        .build();
  }


}
