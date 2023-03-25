package com.javawhizz.App.Auction;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.javawhizz.App.AppApplication;
import com.javawhizz.App.Constants;
import com.javawhizz.App.pojo.AssignToOwnerPojo;
import com.javawhizz.App.pojo.Auction;
import com.javawhizz.App.pojo.AuctionPlayer;
import com.javawhizz.App.pojo.OwnersPojo;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
@Component
public class AuctionService {

    public String addAuctionPlayer(String basePrice,String category,AuctionPlayer auctionPlayer) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference collectionRef = dbFirestore.collection(Constants.BASE_PRICE_CONST+basePrice+"_"+category);
        ApiFuture<QuerySnapshot> querySnapshot = collectionRef.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        String docId = String.valueOf(documents.size()+1);
        DocumentReference docRef = collectionRef.document(docId);
        WriteResult collectionsApiFuture = docRef.set(auctionPlayer, SetOptions.merge()).get();
        return String.valueOf(collectionsApiFuture.getUpdateTime());
    }

    public AuctionPlayer getAuctionPlayer(String documentId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("baseprice3").document(documentId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        AuctionPlayer auction;
        if (document.exists()){
            auction = document.toObject(AuctionPlayer.class);
            return auction;
        }
        return null;
    }

    public AuctionPlayer getRandomPlayer(String basePrice, String category) throws Exception {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference collectionRef = dbFirestore.collection(Constants.BASE_PRICE_CONST+basePrice+"_"+category);

        // Retrieve the documents in the collection
        ApiFuture<QuerySnapshot> querySnapshot = collectionRef.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        // Generate a random number to select a random document
        Random random = new Random();
        int randomIndex = random.nextInt(documents.size());
        // Get the random document
        DocumentSnapshot randomDocument = documents.get(randomIndex);
        AuctionPlayer auction;
        if (randomDocument.exists()){
            auction = randomDocument.toObject(AuctionPlayer.class);
            return auction;
        }
        return null;
    }

    public String assignToOwner(AssignToOwnerPojo assignToOwnerPojo) throws Exception {
        String collectionName = Constants.BASE_PRICE_CONST
                +assignToOwnerPojo.getBasePrice()+"_"+assignToOwnerPojo.getCategory();
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference collectionRef = dbFirestore.collection(collectionName);
        // Create a query to get all documents in the collection
        Query query = collectionRef;
        // Retrieve the documents in the query
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        String docId = null;
        AuctionPlayer player;
        for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
            player = document.toObject(AuctionPlayer.class);
//            System.out.println(document.getId() + " => " + document.getData());
            if (player.getName().equals(assignToOwnerPojo.getPlayerName())){
                docId = document.getId();
                break;
            }
        }
        if (docId != null){
            dbFirestore.collection(collectionName).document(docId).delete();
            Map<String,Object> outerValue = new HashMap<>();
            Map<String,Object> updateValue = new HashMap<>();
            updateValue.put(assignToOwnerPojo.getPlayerName() ,assignToOwnerPojo.getBuyPrice());
            outerValue.put(assignToOwnerPojo.getCategory(),updateValue);
            dbFirestore.collection(Constants.OWNER_CONST)
                    .document(assignToOwnerPojo.getOwnerName())
                        .set(outerValue,SetOptions.merge());
            DocumentReference docRef = dbFirestore.collection(Constants.OWNER_CONST)
                    .document(assignToOwnerPojo.getOwnerName());
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            OwnersPojo op = null;
            if (document.exists()) {
                op = document.toObject(OwnersPojo.class);
                System.out.println("Purse Value : "+op.getAPurse());
            } else {
                System.out.println("No such document");
            }
            ApiFuture<WriteResult> futurePurse = docRef.update(Constants.PURSE_CONST,
                    op.getAPurse() - assignToOwnerPojo.getBuyPrice());
            WriteResult result = futurePurse.get();
            System.out.println("Update time : " + result.getUpdateTime());
            return String.valueOf(result.getUpdateTime());
        }
        return null;
    }

    public String updateAuctionPlayer(Auction auction) {
        return "";
    }

    public Map<String, OwnersPojo> getOwersTeam() throws ExecutionException, InterruptedException {
        Map<String, OwnersPojo> ownersTeam = new HashMap();
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference collectionRef = dbFirestore.collection(Constants.OWNER_CONST);
        ApiFuture<QuerySnapshot> querySnapshot = collectionRef.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        OwnersPojo ownersPojo;
        for (QueryDocumentSnapshot document : documents) {
            ownersPojo = document.toObject(OwnersPojo.class);
            ownersTeam.put(
                    document.getId(), ownersPojo);
            System.out.println(document.getId() + " => " + document.getData());
        }
        return ownersTeam;
    }

    public String deleteAuctitonPlayer(String documentId) {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("baseprice3").document(documentId).delete();
        return "Successfully deleted!";
    }

    public static void main(String[] args) throws IOException {
        AuctionService as = new AuctionService();
        ClassLoader classLoader = AppApplication.class.getClassLoader();
        File file =  new File(Objects.requireNonNull(classLoader.getResource("serviceAccountKey1.json")).getFile());
        FileInputStream serviceAccount = new FileInputStream(file.getAbsoluteFile());

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://auction-c9885-default-rtdb.europe-west1.firebasedatabase.app")
                .build();

        FirebaseApp.initializeApp(options);
        try {
//            as.assignToOwner("3","batsman","Rohit","Chaitu",5);
            System.out.println(as.getOwersTeam());;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
