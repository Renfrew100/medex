package com.seg.medex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

public class ClinicEditAppoinments extends AppCompatActivity {
    private TextView patientName;
    private TextView dateTime;
    private TextView service;
    private String username;
    private String patient;
    private Button removeButton;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_edit_appoinments);
        this.patientName = findViewById(R.id.show_patient_name);
        this.dateTime = findViewById(R.id.show_date_time);
        this.service = findViewById(R.id.show_service);
        this.removeButton = findViewById(R.id.remove_appointment_button);
        this.username = (String) getIntent().getSerializableExtra("username");
        this.patient =(String) getIntent().getSerializableExtra("patient");

        patientName.setText( this.patient);
        String date = (String) getIntent().getSerializableExtra("date");
        String time = (String) getIntent().getSerializableExtra("time");
        dateTime.setText( date  + " @ " + time);
        service.setText((String) getIntent().getSerializableExtra("service"));

        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case ACTION_DOWN:
                        v.setBackground(getResources().getDrawable(R.drawable.clicked_rectangle));
                        return true; // if you want to handle the touch event
                    case ACTION_UP:
                        v.setBackground(getResources().getDrawable(R.drawable.rectangle));
                        v.performClick();
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        };

        this.removeButton.setOnTouchListener(touchListener);
    }

    public void OnRemoveAppointmentClick(View view){
        db = FirebaseFirestore.getInstance();
        db.collection("users").whereEqualTo("username", username)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot query) {
                DocumentSnapshot doc = query.getDocuments().get(0);
                String id = query.getDocuments().get(0).getId();
                //gets appointments for specific clinic
                Map<String, ArrayList<Map<String, String>>> appointments = (Map<String, ArrayList<Map<String, String>>>) doc.get("appointments");
                //for each day of appointmets
                for(Map.Entry entry : appointments.entrySet()){
                    //retrieve the appoints in the day
                    List apps = (ArrayList<Map<String, String>>) entry.getValue();
                    // for each appointments
                    for(int i = 0; i<apps.size(); i++){
                        Map<String, String> eachApp = (Map<String, String>) apps.get(i);
                        if (eachApp.get("username").equals(patient)){
                            apps.remove(i);
                            appointments.put((String)entry.getKey(),(ArrayList<Map<String, String>>) apps);

                            Map<String, Map<String, ArrayList<Map<String, String>>>> service = new HashMap<>();
                            service.put("appointments", appointments);
                            db.collection("users").document("/" + id).set(service, SetOptions.merge());
                            //backToAppointmentsList();
                            Toast.makeText(ClinicEditAppoinments.this, "Deleted appointments!", Toast.LENGTH_SHORT).show();
                            //return;
                            finish();
                        }

                    }

                }

            }});
    }

    public void backToAppointmentsList(){
        startActivity(new Intent(this, ClinicViewAppointments.class));
    }
}
