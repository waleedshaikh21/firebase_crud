package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class loaded_data extends AppCompatActivity {
    private FirebaseFirestore objectFirebaseFirestore;
    private Dialog objectDialog;

    private static final String TAG = "loaded_data";
    private static final String CollectionName = "NewCities";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference objectDocumentReference;
    Task<QuerySnapshot> objectDocumentReference2;

    private TextView load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loaded_data);
        objectFirebaseFirestore = FirebaseFirestore.getInstance();
        objectDialog = new Dialog(this);
        load = findViewById(R.id.LoadData);
        getCollection(load);
    }

    public void Change(View v) {
        Intent intent = new Intent(loaded_data.this, MainActivity.class);
        startActivity(intent);
    }

    public void getCollection(View v) {
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
                                    data += "Document ID: " + document.getId().toString() + "\n Document Details: " + document.getData().toString() + "\n\n";
                                }
                                Toast.makeText(loaded_data.this, "Loading Collection", Toast.LENGTH_SHORT).show();
                                load.setText(data);
                            } else {
                                objectDialog.dismiss();
                                Toast.makeText(loaded_data.this, "Collection Failed To Load", Toast.LENGTH_SHORT).show();
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        } catch (Exception ex) {
            Toast.makeText(this, "Load Error" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
