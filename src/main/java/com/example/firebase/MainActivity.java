package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //Step 1: Create object of Firebase Firestore
    private FirebaseFirestore objectFirebaseFirestore;
    private Dialog objectDialog;

    public String Collection="";

    private static final String TAG = "MainActivity";
    private static final String CollectionName = "NewCities";

    private EditText documentET, cityNameET, cityDetailsET;
    private TextView valuetextbox2;
    public  TextView Loaded;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    DocumentReference objectDocumentReference;
    Task<QuerySnapshot> objectDocumentReference2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Step 2: Initialize Firebase Firestore Object
        objectFirebaseFirestore = FirebaseFirestore.getInstance();
        objectDialog = new Dialog(this);

        documentET = findViewById(R.id.DocumentIDET);
        cityNameET = findViewById(R.id.CityName);

        cityDetailsET = findViewById(R.id.DetailsET);
        valuetextbox2 = findViewById(R.id.GetDataTV1);
        Loaded = findViewById(R.id.LoadData);

        objectDialog.setContentView(R.layout.please_wait_layout);
    }

    public void addValues(View v) {
        try {
            if (!documentET.getText().toString().isEmpty()
                    &&
                    !cityNameET.getText().toString().isEmpty()
                    &&
                    !cityDetailsET.getText().toString().isEmpty()) {
                objectDialog.show();
                objectFirebaseFirestore.collection(CollectionName).document(documentET.getText().toString()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.getResult().exists()) {
                                    objectDialog.dismiss();
                                    documentET.setText("");
                                    cityDetailsET.setText("");
                                    cityNameET.setText("");
                                    documentET.requestFocus();
                                    Toast.makeText(MainActivity.this, "Document Already Exists", Toast.LENGTH_SHORT).show();
                                } else {
                                    Map<String, Object> objectMap = new HashMap<>();
                                    objectMap.put("city_name", cityNameET.getText().toString());

                                    objectMap.put("city_details", cityDetailsET.getText().toString());
                                    objectFirebaseFirestore.collection(CollectionName)
                                            .document(documentET.getText().toString()).set(objectMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    objectDialog.dismiss();
                                                    documentET.setText("");
                                                    cityDetailsET.setText("");
                                                    cityNameET.setText("");
                                                    documentET.requestFocus();
                                                    Toast.makeText(MainActivity.this, "Data Added Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    objectDialog.dismiss();
                                                    Toast.makeText(MainActivity.this, "Fails to add data", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        });
                // Hash Table is known as Map having key and hash vale
                // Parent data type of all data types is Object
            } else {
                objectDialog.dismiss();
                Toast.makeText(this, "Data should not be null", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            objectDialog.dismiss();
            Toast.makeText(this, "addValues" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void getData(View view) {
        try {
            objectDialog.show();
            if (!documentET.getText().toString().isEmpty()) {
                objectDocumentReference = objectFirebaseFirestore.collection("NewCities").
                        document(documentET.getText().toString());
                objectDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        objectDialog.dismiss();
                        if (documentSnapshot.exists()) {
                            valuetextbox2.setText("");
                            documentET.setText("");

                            documentET.requestFocus();
                            String City = documentSnapshot.getString("city_name");
                            String Details = documentSnapshot.getString("city_details");
                            cityNameET.setText(City);
                            cityDetailsET.setText(Details);
//                            valuetextbox.setText("\nCity Name: " + City + "\n\n" + "City Details: " + Details);
                        } else {
                            Toast.makeText(MainActivity.this, "No Documents Retrieved", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed To Get Values Back", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Get Data Error" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void update(View V) {
        try {
            objectDialog.show();
            objectDocumentReference = objectFirebaseFirestore.collection(CollectionName).document(documentET.getText().toString());
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("city_name", cityNameET.getText().toString());
            objectMap.put("city_details", cityDetailsET.getText().toString());

            objectDocumentReference.update(objectMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    objectDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    objectDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Data Failed To Updated" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception ex) {
            Toast.makeText(this, "Update Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void Remove(View V) {
        try {
            objectDialog.show();
            objectDocumentReference = objectFirebaseFirestore.collection(CollectionName).document(documentET.getText().toString());
            Map<String, Object> objectMap = new HashMap<>();
//            objectMap.put("city_name", FieldValue.delete());
//            objectMap.put("city_details", FieldValue.delete());

            objectDocumentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    objectDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Document Deleted", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    objectDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Data Failed To Delete" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception ex) {
            Toast.makeText(this, "Update Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void RemoveCollection(View V) {
        try {
            objectDialog.show();
            db.collection(CollectionName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            objectDialog.dismiss();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    objectDocumentReference = objectFirebaseFirestore.collection(CollectionName).document(document.getId());
                                    objectDocumentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            objectDialog.dismiss();
                                            valuetextbox2.setText("");
                                            Toast.makeText(MainActivity.this, "Collection Deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            objectDialog.dismiss();
                                            Toast.makeText(MainActivity.this, "Data Failed To Delete" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            } else {
                                objectDialog.dismiss();
                                Toast.makeText(MainActivity.this, "Failed To Delete Collection", Toast.LENGTH_SHORT).show();
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        } catch (Exception ex) {
            Toast.makeText(this, "Remove Collection Error", Toast.LENGTH_SHORT).show();
        }
    }

    public void getCollection(View view) {
        try {
            objectDialog.show();
            db.collection(CollectionName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            objectDialog.dismiss();
                            String data = "";
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    Toast.makeText(MainActivity.this, "Loading Collection", Toast.LENGTH_SHORT).show();
                                    data += "Document ID: " + document.getId().toString() + "\n Document Details: " + document.getData().toString() + "\n\n";
                                }
                                Collection=data;
                                Intent intent = new Intent(MainActivity.this, loaded_data.class);
                                startActivity(intent);
                            } else {
                                objectDialog.dismiss();
                                Toast.makeText(MainActivity.this, "Failed To Load Collection", Toast.LENGTH_LONG).show();
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
//            objectDialog.show();
//            objectDocumentReference2=objectFirebaseFirestore.collection(CollectionName)
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                objectDialog.dismiss();
//                                String data="";
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//
//                                    valuetextbox.setText(document.getId().toString() + document.getData().toString());
//                                    valuetextbox2.setText(document.getId().toString() + document.getData().toString());
//                                    String ID[]={ document.getId().toString() } ;
//                                    String Data[]={ document.getData().toString() };
//                                    valuetextbox.setText(String.valueOf(ID) + String.valueOf(Data));
//
//                                }
//                            } else {
//                                objectDialog.dismiss();
//                                Toast.makeText(MainActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
        } catch (Exception ex) {
            Toast.makeText(this, "Load Error", Toast.LENGTH_SHORT).show();
        }
    }
}

