package com.example.dotaskillstimer.data;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;

public class CloudFireStore {
    private final String TAG = "CloudFireStore";
    private final String heroCollections = "Heroes";
    private final String abilityCollections = "Ability";

    private FirebaseFirestore cloudStore;

    public CloudFireStore(FirebaseFirestore firestore) {
        this.cloudStore = firestore;
    }

    public FirebaseFirestore getCloudStore() {
        return cloudStore;
    }

    public void addHero(Hero hero) {
        cloudStore.collection(heroCollections)
                .document(hero.getName())
                .set(hero)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    public void addAbility(Ability ability) {
        cloudStore.collection(abilityCollections)
                .document(ability.getName())
                .set(ability)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    public void addHero(HeroWithAbility heroWithAbility) {
        cloudStore.collection(heroCollections)
                .add(heroWithAbility)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    public void addHeroWithAbility(HeroWithAbility hero) {
        cloudStore.collection(heroCollections)
                .document(hero.getName())
                .set(getHeroMap(hero));

        CollectionReference abilityCollection = cloudStore
                .collection(heroCollections)
                .document(hero.getName())
                .collection(abilityCollections);
        for (Ability ability : hero.getAbilities()) {
            abilityCollection.document(ability.getName()).set(ability);
        }
    }

    public List<Hero> getHeroesWithAbility() {
        CollectionReference collection = cloudStore.collection(heroCollections);

        Task<QuerySnapshot> snapshotTask = collection.get();
        try {
            Tasks.await(snapshotTask);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, snapshotTask.toString() + " => " + snapshotTask.isSuccessful());
        if (snapshotTask.isSuccessful()) {
            QuerySnapshot snapshot = snapshotTask.getResult();
            for (QueryDocumentSnapshot document : snapshot) {
                Log.d(TAG, document.getId() + " => " + document.getData());
                Hero hero=document.toObject(Hero.class);
                Log.d(TAG, hero.toString());
            }
        } else {
            Log.d(TAG, "Error getting documents: ", snapshotTask.getException());
        }
        return new ArrayList<>();
    }
    public List<Ability> getAbilityList() {
        CollectionReference collection = cloudStore.collection(abilityCollections);

        Task<QuerySnapshot> snapshotTask = collection.get();
        try {
            Tasks.await(snapshotTask);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, snapshotTask.toString() + " => " + snapshotTask.isSuccessful());
        if (snapshotTask.isSuccessful()) {
            QuerySnapshot snapshot = snapshotTask.getResult();
            for (QueryDocumentSnapshot document : snapshot) {
                Log.d(TAG, document.getId() + " => " + document.getData());
                Ability ability =document.toObject(Ability.class);
                Log.d(TAG, ability.toString());
            }
        } else {
            Log.d(TAG, "Error getting documents: ", snapshotTask.getException());
        }
        return new ArrayList<>();
    }


    private HashMap<String, Object> getHeroMap(HeroWithAbility hero) {
        HashMap<String, Object> heroMap = new HashMap<>();
        heroMap.put("avatar", hero.getAvatar());
        heroMap.put("name", hero.getName());
        heroMap.put("PrimaryAttributes", hero.getPrimaryAttributes());
        return heroMap;
    }
//    private HeroWithAbility getHeroFromMap(Map map){
//        HeroWithAbility hero = new HeroWithAbility();
//        try {
//        hero.setName((String) map.get("name"));
//        long aLong = (long)map.get("avatar");
//        hero.setAvatar((int)aLong);
//
//        String primaryAttributes = (String) map.get("PrimaryAttributes");
//
//        if (primaryAttributes != null) {
//            switch (primaryAttributes) {
//                case "Strength":
//                    hero.setPrimaryAttributes(PrimaryAttributes.Strength);
//                    break;
//                case "Agility":
//                    hero.setPrimaryAttributes(PrimaryAttributes.Agility);
//                    break;
//                case "Intellects":
//                    hero.setPrimaryAttributes(PrimaryAttributes.Intellects);
//                    break;
//                default:
//                    hero.setPrimaryAttributes(null);
//                    break;
//            }
//        }
//        Log.d(TAG, hero.toString());
//        return hero;
//        }catch (ClassCastException e){
//            Log.d(TAG, e.toString());
//            return hero;
//        }
//    }
    public List<Ability> getHeroAbilityList(String hero){
        return null;
    }


}
