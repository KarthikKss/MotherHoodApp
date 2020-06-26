package com.starktech.motherhood;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Registration extends AppCompatActivity  {

    private EditText name;
    private EditText birthday;
    private TextView tv;
    private EditText email;
    private EditText password;
    private EditText location;
    private Button regbtn;
    private RadioGroup isMother;
    private RadioGroup isPregnant;
    private Spinner kidsspinner;

    String userAge;

    int y,m,d;
    Calendar mCalendar;

    DatePickerDialog mDatePickerDialog;

    Members mMembers;

    private LocationManager mLocationManager;
    private double latitude;
    private double longitude;
    private String city;
    private String country;
    private String numkids;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(Registration.this, NavPage.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        email = (EditText) findViewById(R.id.email);
        birthday = (EditText) findViewById(R.id.birthday);
        kidsspinner = (Spinner) findViewById(R.id.kidsspinner);
        tv = (TextView) findViewById(R.id.kidstext);
        password = (EditText) findViewById(R.id.password);
        regbtn = (Button) findViewById(R.id.regisbtn);
        name = (EditText) findViewById(R.id.name);
        isMother = (RadioGroup) findViewById(R.id.momornot);
        isPregnant = (RadioGroup) findViewById(R.id.pregornot);
        location = (EditText) findViewById(R.id.location);
        ArrayAdapter<String> kidsadapter = new ArrayAdapter<String>(Registration.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.kids));
        kidsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kidsspinner.setAdapter(kidsadapter);
        mMembers=new Members();

        /*mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mlocation = mLocationManager.getLastKnownLocation(mLocationManager.NETWORK_PROVIDER);
        onLocationChanged(mlocation);

        setCity(mlocation);

        Log.d("Debug",city);


        location.setText(city);*/



        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar = Calendar.getInstance();
                y=mCalendar.get(Calendar.YEAR);
                m=mCalendar.get(Calendar.MONTH);
                d=mCalendar.get(Calendar.DAY_OF_MONTH);

                mDatePickerDialog = new DatePickerDialog(Registration.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                birthday.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                                userAge = getAge( year,  month,  dayOfMonth);
                            }
                        }, y, m, d);
                mDatePickerDialog.show();
            }
        });




        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId1=isMother.getCheckedRadioButtonId();
                int selectedId2=isPregnant.getCheckedRadioButtonId();

                final RadioButton rb1=(RadioButton)findViewById(selectedId1);
                final RadioButton rb2=(RadioButton)findViewById(selectedId2);

                if(rb1.getText()==null||rb2.getText()==null){
                    return;
                }


                final String emailid= email.getText().toString();
                final String pass= password.getText().toString();
                final String nameis=name.getText().toString();
                final String loc=location.getText().toString();
                final String bday= birthday.getText().toString();
                numkids=kidsspinner.getSelectedItem().toString();
                mMembers.setName(nameis);
                mMembers.setCityLoc(loc);
                mMembers.setIsMom(rb1.getText().toString());
                mMembers.setIsPreg(rb2.getText().toString());

                mAuth.createUserWithEmailAndPassword(emailid,pass).addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(Registration.this, "Registration failed",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Map userInfo = new HashMap<>();
                            userInfo.put("name",nameis);
                            userInfo.put("birthday",bday);
                            userInfo.put("cityLoc",loc);
                            userInfo.put("isMom",rb1.getText().toString());
                            userInfo.put("isPreg",rb2.getText().toString());
                            userInfo.put("profileImageUrl","default");
                            userInfo.put("about","");
                            userInfo.put("age",userAge);
                            userInfo.put("kids",numkids);

                            String userid=mAuth.getCurrentUser().getUid();
                            mMembers.setUserid(userid);
                            userInfo.put("userid",userid);
                            DatabaseReference currentUserDb= FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                            currentUserDb.updateChildren(userInfo);
                            Toast.makeText(Registration.this, "Data Inserted",Toast.LENGTH_SHORT).show();
                            currentUserDb.child("connections").child("nope").child(userid).setValue(true);
                            currentUserDb.child("filters").child("distance").setValue("50");
                            currentUserDb.child("filters").child("showme").setValue("All");
                            currentUserDb.child("filters").child("maxage").setValue(userAge);
                            currentUserDb.child("filters").child("maxkids").setValue(">=3");


                        }
                    }
                });
            }
        });
    }

    private String getAge(int year, int month, int dayOfMonth) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, dayOfMonth);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }
/*
    @Override
    public void onLocationChanged(Location location) {
        longitude=location.getLongitude();
        latitude=location.getLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }*/

    /*public void setCity(Location location){
        try {
            Geocoder geocoder=new Geocoder(this);
            List<Address> addresses=null;
            addresses=geocoder.getFromLocation(latitude,longitude,1);

            city=addresses.get(0).getLocality();
            country=addresses.get(0).getCountryName();
            Log.d("Debug",city);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Error fetching city, enter manually!",Toast.LENGTH_SHORT).show();
        }
    }*/
}
