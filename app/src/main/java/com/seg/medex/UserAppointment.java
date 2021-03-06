package com.seg.medex;
 
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
 
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
 
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
 
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

public class UserAppointment extends AppCompatActivity {
    private TextView clinicName;
    private TextView address;
    private TextView service;
    private TextView dateAndTime;
    FirebaseFirestore db;
    SharedPreferences preferences;
    private Button cancelButton;
    private Button checkInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_appointment);
        this.clinicName = findViewById(R.id.showClinicName);
        clinicName.setText((String) getIntent().getSerializableExtra("clinic_username"));
        this.address = findViewById(R.id.showAddress);
        address.setText((String) getIntent().getSerializableExtra("addy"));
        this.service = findViewById(R.id.showService);
        service.setText((String) getIntent().getSerializableExtra("service"));
        this.dateAndTime = findViewById(R.id.showDateandTime);
        String da = getIntent().getSerializableExtra("date")+"  "+getIntent().getSerializableExtra("time");
        dateAndTime.setText(da);
        this.preferences = getSharedPreferences("ID",0);

        this.cancelButton = findViewById(R.id.cancel_app);
        this.checkInButton = findViewById(R.id.check_in_button);

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

        this.cancelButton.setOnTouchListener(touchListener);
        this.checkInButton.setOnTouchListener(touchListener);
    }
 
    public void onClickCancelApp(View view){
        db = FirebaseFirestore.getInstance();
        db.collection("users").whereEqualTo("clinic_name", getIntent().getSerializableExtra("clinic_username"))
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
                        if (eachApp.get("username").equals(preferences.getString("username",""))){
                            apps.remove(i);
                            appointments.put((String)entry.getKey(),(ArrayList<Map<String, String>>) apps);
                            Map<String, Map<String, ArrayList<Map<String, String>>>> service = new HashMap<>();
                            service.put("appointments", appointments);
                            db.collection("users").document("/" + id).set(service, SetOptions.merge());
                            backToAppointmentsList();
                            Toast.makeText(UserAppointment.this, "Canceled appointment", Toast.LENGTH_SHORT).show();
                        }
 
                    }
 
                }
 
            }});
    }
 
    public void backToAppointmentsList(){
        finish();
    }
 
    public void onCheckInClick(View view){
        String currentTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE);
 
 
        db = FirebaseFirestore.getInstance();
        db.collection("users").whereEqualTo("clinic_name", getIntent().getSerializableExtra("clinic_username"))
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
                        if (eachApp.get("username").equals(preferences.getString("username",""))){
                            int aaa = Calendar.getInstance().get(Calendar.MONTH)+1;
                            String date;
                            if(Calendar.getInstance().get(Calendar.DAY_OF_MONTH )-10 < 0){
                                date = Calendar.getInstance().get(Calendar.YEAR)+"/"+aaa+"/0"+Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                            }else{
                                date = Calendar.getInstance().get(Calendar.YEAR)+"/"+aaa+"/"+Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                            }
                            if(date.compareTo((String)entry.getKey()) == 0){
                                apps.remove(i);
                                appointments.put((String)entry.getKey(),(ArrayList<Map<String, String>>) apps);
                                Map<String, Map<String, ArrayList<Map<String, String>>>> service = new HashMap<>();
                                service.put("appointments", appointments);
                                db.collection("users").document("/" + id).set(service, SetOptions.merge());
                                backToAppointmentsList();
                                Toast.makeText(UserAppointment.this, "User Checked in!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
 
                    }
 
                }
 
            }});
 
    }
}
