package com.starktech.motherhood;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FiltersActivity extends AppCompatActivity {

    private TextView distance,maxage;
    private SeekBar distancebar,agebar;
    private RadioGroup filtersgrp;
    private Button updatefiltersbtn,backfiltersbtn;
    private RadioButton mother,all;
    private Spinner kidsfilterspinner;

    private int setdistance;
    private int filteroption;

    private String currentUid;
    private FirebaseAuth mAuth;
    private DatabaseReference userDb,initialUserDb;
    private String initalDistance;
    private String initialShowme;
    private String initialMaxAge;
    private String initialMaxKids;
    String numfilterkids;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        mAuth=FirebaseAuth.getInstance();
        currentUid=mAuth.getCurrentUser().getUid();

        initialUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUid).child("filters");





        distance=(TextView)findViewById(R.id.filterdistance);
        kidsfilterspinner = (Spinner)findViewById(R.id.filterskidsspinner);
        ArrayAdapter<String> kidsfilteradapter = new ArrayAdapter<String>(FiltersActivity.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.filterkids));
        kidsfilteradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kidsfilterspinner.setAdapter(kidsfilteradapter);
        numfilterkids=kidsfilterspinner.getSelectedItem().toString();


        maxage=(TextView)findViewById(R.id.filtermaxage);
        distancebar=(SeekBar)findViewById(R.id.distancebar);
        agebar=(SeekBar)findViewById(R.id.agebar);
        filtersgrp=(RadioGroup)findViewById(R.id.filtermom);
        updatefiltersbtn=(Button)findViewById(R.id.confirmfiltersbtn);
        backfiltersbtn=(Button)findViewById(R.id.filtersbackbtn);
        mother=(RadioButton)findViewById(R.id.mothersid);
        all=(RadioButton)findViewById(R.id.allid);

        getInitailValues();




        updatefiltersbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId1=filtersgrp.getCheckedRadioButtonId();
                final RadioButton rb1=(RadioButton)findViewById(selectedId1);
                userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUid).child("filters");
                Map filterInfo = new HashMap<>();
                filterInfo.put("distance",distance.getText().toString());
                filterInfo.put("showme",rb1.getText().toString());
                filterInfo.put("maxage",maxage.getText().toString());
                filterInfo.put("maxkids",kidsfilterspinner.getSelectedItem().toString());
                userDb.updateChildren(filterInfo);
                Intent i = new Intent(FiltersActivity.this,NavPage.class);
                finish();
                startActivity(i);
                return;
            }
        });
        backfiltersbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        distancebar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distance.setText(""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int temp=distancebar.getProgress();
                distancebar.setProgress(temp);
            }
        });
        agebar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                maxage.setText(""+(progress/2));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int temp=agebar.getProgress();
                agebar.setProgress(temp);
            }
        });

    }

    private void getInitailValues() {
        initialUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                initalDistance=dataSnapshot.child("distance").getValue().toString();
                initialShowme=dataSnapshot.child("showme").getValue().toString();
                initialMaxAge=dataSnapshot.child("maxage").getValue().toString();
                initialMaxKids=dataSnapshot.child("maxkids").getValue().toString();

                Log.d("debug","Distance = "+initalDistance+" Showme = "+initialShowme+" Maxage = "+initialMaxAge+" MaxKids = "+initialMaxKids);
                if(initialMaxKids.equals("1")){
                    kidsfilterspinner.setSelection(0);
                }
                else if(initialMaxKids.equals("2")){
                    kidsfilterspinner.setSelection(1);
                }
                else {
                    kidsfilterspinner.setSelection(2);
                }

                distance.setText(initalDistance);
                maxage.setText(initialMaxAge);

                if(initialShowme.equals(mother.getText().toString())){
                    mother.setChecked(true);
                }
                else{
                    all.setChecked(true);
                }
                int disprogress=Integer.parseInt(initalDistance);
                int ageprogress=Integer.parseInt(initialMaxAge);
                ageprogress=ageprogress*2;

                distancebar.setProgress(disprogress);
                agebar.setProgress(ageprogress);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
