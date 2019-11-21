package com.seg.medex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.antonious.materialdaypicker.MaterialDayPicker;

public class ClinicTimeActivity extends AppCompatActivity {

    private TextView mondayStart;
    private TextView mondayEnd;
    private TextView tuesdayStart;
    private TextView tuesdayEnd;
    private TextView wednesdayStart;
    private TextView wednesdayEnd;
    private TextView thursdayStart;
    private TextView thursdayEnd;
    private TextView fridayStart;
    private TextView fridayEnd;
    private TextView saturdayStart;
    private TextView saturdayEnd;
    private TextView sundayStart;
    private TextView sundayEnd;
    private FirebaseFirestore db;
    private SharedPreferences preferences;

    private Spinner days;
    private Spinner startDay;
    private Spinner endDay;

    private List<String> startTime;
    private List<String> endTime;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_time);

        this.mondayStart = findViewById(R.id.mondayStartTime);
        this.mondayEnd = findViewById(R.id.mondayEndTime);

        this.tuesdayStart = findViewById(R.id.tuesdayStartTime);
        this.tuesdayEnd = findViewById(R.id.tuesdayEndTime);

        this.wednesdayStart = findViewById(R.id.wednesdayStartTime);
        this.wednesdayEnd = findViewById(R.id.wednesdayEndTime);

        this.thursdayStart = findViewById(R.id.thursdayStartTime);
        this.thursdayEnd = findViewById(R.id.thursdayEndTime);

        this.fridayStart = findViewById(R.id.fridayStartTime);
        this.fridayEnd = findViewById(R.id.fridayEndTime);

        this.saturdayStart = findViewById(R.id.saturdayStartTime);
        this.saturdayEnd = findViewById(R.id.saturdayEndTime);

        this.sundayStart = findViewById(R.id.sundayStartTime);
        this.sundayEnd = findViewById(R.id.sundayEndTime);

        this.preferences = getSharedPreferences("ID", 0);
        this.db = FirebaseFirestore.getInstance();

        this.days = findViewById(R.id.day_spinner);
        this.startDay = findViewById(R.id.start_time_spinner);
        this.endDay = findViewById(R.id.end_time_spinner);




        db.collection("users").whereEqualTo("username", preferences.getString("username", " "))
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot query) {
                DocumentSnapshot doc = query.getDocuments().get(0);
                startTime = (ArrayList) doc.get("start_times");
                endTime = (ArrayList) doc.get("end_times");

                mondayStart.setText(startTime.get(0));
                mondayEnd.setText(endTime.get(0));

                tuesdayStart.setText(startTime.get(1));
                tuesdayEnd.setText(endTime.get(1));

                wednesdayStart.setText(startTime.get(2));
                wednesdayEnd.setText(endTime.get(2));
                
                thursdayStart.setText(startTime.get(3));
                thursdayEnd.setText(endTime.get(3));

                fridayStart.setText(startTime.get(4));
                fridayEnd.setText(endTime.get(4));

                saturdayStart.setText(startTime.get(5));
                saturdayEnd.setText(endTime.get(5));

                sundayStart.setText(startTime.get(6));
                sundayEnd.setText(endTime.get(6));
            }

        });
//        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.time_array, android.R.layout.simple_spinner_item);
//
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


    }
    
    public void onBackClick(View view) {
        finish();
    }

    public void onApplyClick(View view) {
        if(days.getSelectedItem().toString().compareTo("Monday") == 0){
            startTime.set(0,startDay.getSelectedItem().toString());
            endTime.set(0,endDay.getSelectedItem().toString());
        }
        if(days.getSelectedItem().toString().compareTo("Tuesday") == 0){
            startTime.set(1,startDay.getSelectedItem().toString());
            endTime.set(1,endDay.getSelectedItem().toString());
        }
        if(days.getSelectedItem().toString().compareTo("Wednesday") == 0){
            startTime.set(2,startDay.getSelectedItem().toString());
            endTime.set(2,endDay.getSelectedItem().toString());
        }
        if(days.getSelectedItem().toString().compareTo("Thursday") == 0){
            startTime.set(3,startDay.getSelectedItem().toString());
            endTime.set(3,endDay.getSelectedItem().toString());
        }
        if(days.getSelectedItem().toString().compareTo("Friday") == 0){
            startTime.set(4,startDay.getSelectedItem().toString());
            endTime.set(4,endDay.getSelectedItem().toString());
        }
        if(days.getSelectedItem().toString().compareTo("Saturday") == 0){
            startTime.set(5,startDay.getSelectedItem().toString());
            endTime.set(5,endDay.getSelectedItem().toString());
        }
        if(days.getSelectedItem().toString().compareTo("Sunday") == 0){
            startTime.set(6,startDay.getSelectedItem().toString());
            endTime.set(6,endDay.getSelectedItem().toString());
        }

        HashMap<String, Object> updated = new HashMap<>();
        updated.put("start_time", startTime);
        updated.put("end_time", endTime);
        

    }
}
