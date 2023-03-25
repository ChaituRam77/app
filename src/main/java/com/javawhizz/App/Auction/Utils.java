package com.javawhizz.App.Auction;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Utils {
    public static Map<String, Object> getAllDocsInCollection(Firestore db, String collectionName) throws Exception {
        Map<String, Object> docsInCollection = null;
        CollectionReference collectionRef = db.collection(collectionName);
        // Create a query to get all documents in the collection
        Query query = collectionRef;
        // Retrieve the documents in the query
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
            docsInCollection.put(document.getId(),document.getData());
            System.out.println(document.getId() + " => " + document.getData());
        }
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        // Generate a random number to select a random document
        Random random = new Random();
        int randomIndex = random.nextInt(documents.size());
        // Get the random document
        DocumentSnapshot randomDocument = documents.get(randomIndex);
        // Print the ID and data of the random document
        System.out.println("Random document ID: " + randomDocument.getId());
        System.out.println("Random document data: " + randomDocument.getData());
        // Close the connection
        db.close();
        return docsInCollection;
    }


    public static long getDocCountInCollection(Firestore db, String collectionName) throws Exception {
        CollectionReference collectionRef = db.collection(collectionName);
        // Create a query to get all documents in the collection
        Query query = collectionRef;
        // Retrieve the documents in the query
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        long documentCount = querySnapshot.get().size();
        System.out.println("Count of docs in collection : " + documentCount);
        // Close the connection
        db.close();
        return documentCount;
    }

    public static Map<String, Object> getRandomDocInCollection(Firestore db, String collectionName) throws Exception {

        Map<String, Object> randomDoc = new HashMap<>();
        // Get a reference to the collection
        CollectionReference collectionRef = db.collection(collectionName);

        // Retrieve the documents in the collection
        ApiFuture<QuerySnapshot> querySnapshot = collectionRef.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        // Generate a random number to select a random document
        Random random = new Random();
        int randomIndex = random.nextInt(documents.size());

        // Get the random document
        DocumentSnapshot randomDocument = documents.get(randomIndex);

        // Print the ID and data of the random document
        System.out.println("Random document ID: " + randomDocument.getId());
        System.out.println("Random document data: " + randomDocument.getData());
        randomDoc.put(randomDocument.getId().toString(),randomDocument.getData());
        // Close the connection
        db.close();

        return randomDoc;
    }

    public void printAllValuesInDocument() throws Exception{
        // Get the Firestore instance
        Firestore firestore = FirestoreClient.getFirestore();

        // Get a reference to the document you want to retrieve
        DocumentReference docRef = firestore.collection("users").document("john");

        // Retrieve the document and its fields
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            // Print all fields in the document
            Map<String, Object> fields = document.getData();
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
        } else {
            System.out.println("No such document");
        }
    }

}
