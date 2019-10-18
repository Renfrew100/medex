package com.seg.medex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Query;



public class MainActivity extends AppCompatActivity {

    /**
     * The Firebase Firestore database object.
     */
    private FirebaseFirestore db;
    private boolean auto_login = false;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("ID", 0);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        if(!preferences.contains("username")) {
            startActivity(new Intent(this, SignupActivity.class));
            finish();
            return;
        }

        if(!preferences.getBoolean("logged_in", false)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }


        if(preferences.contains("username") && preferences.getBoolean("logged_in", false)) {

            if(!preferences.getBoolean("completed_profile", false)) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return;

            } else {

                this.db = FirebaseFirestore.getInstance();
                db.collection("users").whereEqualTo("username", preferences.getString("username", ""))
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()) {
                            QuerySnapshot query = task.getResult();
                            if(!(query.isEmpty())) {
                                if(preferences.getString("username", "").equals(query.getDocuments().get(0).get("username"))
                                && preferences.getString("password", "").equals(query.getDocuments().get(0).get("password"))
                                && preferences.getString("email", "").equals(query.getDocuments().get(0).get("email"))) {
                                    auto_login = true;
                                }

                            }
                        } else {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.clear();
                            editor.apply();
                            Log.d("LOGIN", "AutoLogin failed", task.getException());
                        }

                    }
                });
                if(auto_login) {
                    System.out.println("outer");
                    startActivity(new Intent(this, LandingActivity.class));
                    finish();
                    return;
                }

            }

            System.out.println("outer");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Toast.makeText(this, "You're not supposed to be here. Report this bug to a developer.", Toast.LENGTH_SHORT);
        finish();
    }
}
