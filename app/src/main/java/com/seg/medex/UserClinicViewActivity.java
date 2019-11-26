package com.seg.medex;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserClinicViewActivity extends AppCompatActivity {

    private ListView list;
    private CustomAdapter adapter;
    FirebaseFirestore db;
    final ArrayList<List> elements = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_clinic_view);

        this.list = findViewById(R.id.clinic_list);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        adapter = new CustomAdapter(this, elements);

        db.collection("users")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        Log.d("This", String.valueOf(task.getResult().getDocuments().size()));
                        for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                            Log.d("AAAA", String.valueOf(task.getResult().getDocuments().get(i).get("account_type").getClass().getName()));
                            if (task.getResult().getDocuments().get(i).get("account_type").equals(Long.valueOf(1)) &&
                                task.getResult().getDocuments().get(i).get("clinic_name")!=null &&
                                !task.getResult().getDocuments().get(i).get("clinic_name").equals("") &&
                                !((ArrayList)task.getResult().getDocuments().get(i).get("services")).isEmpty()) {
                                Log.d("AAAA", "AAAA");
                                List currentList = new ArrayList();
                                currentList.add(0,task.getResult().getDocuments().get(i).get("clinic_name").toString());
                                currentList.add(1,(task.getResult().getDocuments().get(i).get("street_address").toString()));
                                currentList.add(2,task.getResult().getDocuments().get(i).get("services"));
                                currentList.add(3,task.getResult().getDocuments().get(i).get("rating").toString());
                                currentList.add(4, task.getResult().getDocuments().get(i).get("username").toString());

                                elements.add(currentList);
                                setAdapter(elements);
                            }
                        }
                        list.setAdapter(adapter);
                    }
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Manage Clinics: ", "Failed. Contact a developer.");
                    }
                });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String username = elements.get(i).get(4).toString();
                openClinic(username);
            }
        });
    }

    private void openClinic(String clinicName) {
        Intent intent = new Intent(this, UserOpenClinicActivity.class);
        intent.putExtra("clinic_username", clinicName);
        startActivity(intent);
    }

    private void setAdapter(ArrayList<List> elements) {
        adapter = new CustomAdapter(this, elements);
        list.setAdapter(adapter);
    }

    private class CustomAdapter extends BaseAdapter implements ListAdapter {

        private Context context;
        private ArrayList<List> list;

        CustomAdapter(@NonNull Context context, ArrayList<List> list) {
            this.context = context;
            this.list = list;
        }

        public void remove(Object item){
            list.remove(item);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if(view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.user_clinic_list_item, null);
            }

            String clinicName = list.get(position).get(0).toString();
            String addressName = list.get(position).get(1).toString();
            ArrayList<String> services = (ArrayList) list.get(position).get(2);
            String rating = list.get(position).get(3).toString();

            TextView nameText = view.findViewById(R.id.name_info);
            nameText.setText("Name: " + clinicName);

            TextView addressNameText = view.findViewById(R.id.address_info);
            addressNameText.setText(addressName);

            TextView serviceText = view.findViewById(R.id.service_info);
            String string = "";
            for(String service : services) {
                string += service + ",";
            }
            serviceText.setText(string);

            TextView ratingText = view.findViewById(R.id.rating_text);
            ratingText.setText(rating);

            return view;
        }
    }

}
