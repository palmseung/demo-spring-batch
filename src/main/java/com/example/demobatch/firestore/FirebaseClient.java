package com.example.demobatch.firestore;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@NoArgsConstructor
@Component
public class FirebaseClient {
    private Firestore firestoreDB;

//    private final String COLLECTION_MEMBERS = "members";
//    private final String DOC_NAME_KEY = "name";
//    private final String DOC_AGE_KEY = "age";

    private final String COLLECTION_ALPHA = "alpha";
    private final String DOCUMENT_ORDER = "order";
    private final String COLLECTION_STATUS = "status";

    private final String DOC_ORDER_ID = "orderID";
    private final String DOC_STATUS = "status";

    @PostConstruct
    public void init() throws IOException, ExecutionException, InterruptedException {
        ClassPathResource resource = new ClassPathResource("credentials.json");
        InputStream inputStream = resource.getInputStream();
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(inputStream))
                .setDatabaseUrl("https://fir-firestore-2668e.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
        this.firestoreDB = FirestoreClient.getFirestore();
//
//        CollectionReference collection = firestoreDB
//            .collection(COLLECTION_ALPHA)
//            .document(DOCUMENT_ORDER)
//            .collection(COLLECTION_STATUS);
//
//
//        Map<String, Object> data = new ImmutableMap.Builder<String, Object>()
//            .put(DOC_ORDER_ID, 6)
//            .put(DOC_STATUS, "ODST000003")
//            .build();
//
//        DocumentReference documentReference = collection.add(data).get();
//
//        Timestamp timeStamp = documentReference.get().get().getCreateTime();
//        log.info(">>>>>>>>>>>>>>> timeStamp : {}", timeStamp);
    }
}
