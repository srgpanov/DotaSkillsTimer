package com.srgpanov.dotaskillstimer.data;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.srgpanov.dotaskillstimer.App;
import com.srgpanov.dotaskillstimer.data.entity.Ability;
import com.srgpanov.dotaskillstimer.data.entity.Hero;
import com.srgpanov.dotaskillstimer.data.entity.HeroWithAbility;
import com.srgpanov.dotaskillstimer.data.entity.Item;
import com.srgpanov.dotaskillstimer.utils.Constants;
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
    private final String versionCollections = "Version";
    private String itemCollections = "Items";

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
                        Log.d(TAG, "Hero " + hero.getName() + " successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, hero.getName() + "Error adding document", e);
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
                        Log.d(TAG, "Ability " + ability.getName() + " successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error adding document", e);
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
                        Log.d(TAG, "Error adding document", e);
                    }
                });

    }

    public void addItem(Item item) {
        cloudStore.collection(itemCollections)
                .document(item.getName())
                .set(item)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Item " + item.getName() + " successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error adding document", e);
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

    public List<Hero> getHeroesList() {
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
            List<Hero> heroList = new ArrayList<>();
            for (QueryDocumentSnapshot document : snapshot) {
                Log.d(TAG, document.getId() + " => " + document.getData());
                Hero hero = document.toObject(Hero.class);
                heroList.add(hero);
                Log.d(TAG, hero.toString());
            }
            return heroList;
        } else {
            Log.d(TAG, "Error getting documents: ", snapshotTask.getException());
            throw new IllegalStateException("Heroes not loaded");
        }
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
            List<Ability> abilityList = new ArrayList<>();
            for (QueryDocumentSnapshot document : snapshot) {
                Log.d(TAG, document.getId() + " => " + document.getData());
                Ability ability = document.toObject(Ability.class);
                abilityList.add(ability);
                Log.d(TAG, ability.toString());
            }
            return abilityList;
        } else {
            Log.d(TAG, "Error getting documents: ", snapshotTask.getException());
            throw new IllegalStateException("Heroes not loaded");
        }
    }

    public List<Item> getItemsList() {
        CollectionReference collection = cloudStore.collection(itemCollections);
        Task<QuerySnapshot> snapshotTask = collection.get();
        try {
            Tasks.await(snapshotTask);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, snapshotTask.toString() + " => " + snapshotTask.isSuccessful());
        if (snapshotTask.isSuccessful()) {
            QuerySnapshot snapshot = snapshotTask.getResult();
            List<Item> itemList = new ArrayList<>();
            for (QueryDocumentSnapshot document : snapshot) {
                Log.d(TAG, document.getId() + " => " + document.getData());
                Item item = document.toObject(Item.class);
                itemList.add(item);
                Log.d(TAG, item.toString());
            }
            return itemList;
        } else {
            Log.d(TAG, "Error getting documents: ", snapshotTask.getException());
            throw new IllegalStateException("Items not loaded");
        }
    }

    private HashMap<String, Object> getHeroMap(HeroWithAbility hero) {
        HashMap<String, Object> heroMap = new HashMap<>();
        heroMap.put("avatar", hero.getAvatar());
        heroMap.put("name", hero.getName());
        heroMap.put("PrimaryAttributes", hero.getPrimaryAttributes());
        return heroMap;
    }


    public boolean checkUpdate(long version) {
        CollectionReference collection = cloudStore.collection(versionCollections);
        Task<QuerySnapshot> snapshotTask = collection.get();
        try {
            Tasks.await(snapshotTask);
        } catch (ExecutionException | InterruptedException e) {
            Log.d(TAG, "error on checkUpdate ");
            e.printStackTrace();
        }
        Log.d(TAG, snapshotTask.toString() + " => " + snapshotTask.isSuccessful());
        if (snapshotTask.isSuccessful()) {
            QuerySnapshot snapshot = snapshotTask.getResult();
            List<VersionRemoteDB> dbList = new ArrayList<>();
            for (QueryDocumentSnapshot document : snapshot) {
                Log.d(TAG, document.getId() + " => " + document.getData());
                VersionRemoteDB remoteDB = new VersionRemoteDB((long) document.getData().get("LastUpdate"));
                dbList.add(remoteDB);
                Log.d(TAG, remoteDB.toString());
            }
            if (dbList.size() == 1){
                VersionRemoteDB remoteDB = dbList.get(0);
                Log.d(TAG, ""+remoteDB.getVersion());
                long remoteVersion=remoteDB.getVersion();
                saveInSetting(remoteVersion);
                return version < remoteVersion;
            }
            return false;
        } else {
            Log.d(TAG, "Error getting documents: ", snapshotTask.getException());
            throw new IllegalStateException("Version not loaded");
        }
    }

    private void saveInSetting(long remoteVersion) {
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        preferences.edit().putLong(Constants.versionDB,remoteVersion).commit();
    }

    public List<Ability> getHeroAbilityList(String hero) {
        return null;
    }

}
