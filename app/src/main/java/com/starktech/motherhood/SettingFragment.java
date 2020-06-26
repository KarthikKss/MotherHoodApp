package com.starktech.motherhood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class SettingFragment extends Fragment {


    private EditText nameField,locationField,aboutField;
    private Spinner kidsField;
    private Button confirmbtn;
    private ImageView profilepic;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    private String userId,name,location,profileImageUrl,about,numberkids,gotnumberkids;

    private Uri resultUri;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.activity_settings,container,false);

        nameField=(EditText)rootView.findViewById(R.id.profilename);
        aboutField=(EditText)rootView.findViewById(R.id.profileabout);
        locationField=(EditText)rootView.findViewById(R.id.profileLoc);
        profilepic=(ImageView)rootView.findViewById(R.id.profilepic);
        confirmbtn=(Button)rootView.findViewById(R.id.confirmbtn);
        kidsField=(Spinner)rootView.findViewById(R.id.settingskidsspinner);

        ArrayAdapter<String> kidsadaptersettings = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.kids));
        kidsadaptersettings.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kidsField.setAdapter(kidsadaptersettings);
        numberkids=kidsField.getSelectedItem().toString();

        mAuth=FirebaseAuth.getInstance();
        userId=mAuth.getCurrentUser().getUid();
        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(userId);


        getUserInfo();

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i,1);
            }
        });
        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
            }
        });


        return rootView;
    }

    private void getUserInfo() {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String,Object> map = (Map<String, Object>)dataSnapshot.getValue();
                    if(map.get("name")!=null){
                        name = map.get("name").toString();
                        nameField.setText(name);
                    }
                    if(map.get("cityLoc")!=null){
                        location = map.get("cityLoc").toString();
                        locationField.setText(location);
                    }
                    if(map.get("about")!=null){
                        about = map.get("about").toString();
                        aboutField.setText(about);
                    }
                    if(map.get("kids")!=null){
                         gotnumberkids= map.get("kids").toString();
                         if(gotnumberkids.equals(">5")){
                             kidsField.setSelection(6);
                         }
                         else {
                             kidsField.setSelection(Integer.parseInt(gotnumberkids));
                         }
                    }
                    Glide.clear(profilepic);
                    if(map.get("profileImageUrl")!=null){
                        profileImageUrl = map.get("profileImageUrl").toString();
                        switch (profileImageUrl){
                            case "default":
                                Glide.with(getActivity().getApplicationContext()).load(R.drawable.defimgsetting).into(profilepic);

                                break;

                            default:
                                Glide.with(getActivity().getApplicationContext()).load(profileImageUrl).into(profilepic);
                                break;
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void saveUserInfo() {
        name=nameField.getText().toString();
        location=locationField.getText().toString();
        about=aboutField.getText().toString();
        numberkids=kidsField.getSelectedItem().toString();

        Map userInfo = new HashMap();
        userInfo.put("name",name);
        userInfo.put("cityLoc",location);
        userInfo.put("about",about);
        userInfo.put("kids",numberkids);
        mUserDatabase.updateChildren(userInfo);



        if(resultUri != null){
            final StorageReference filePath= FirebaseStorage.getInstance().getReference().child("profileimages").child(userId);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplication().getContentResolver(),resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,20,baos);
            byte[] data=baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(),"Failed to upload",Toast.LENGTH_LONG).show();

                    getActivity().finish();
                }
            });

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("profileImageUrl", uri.toString());
                            mUserDatabase.updateChildren(newImage);
                            Toast.makeText(getActivity(),"Upload Successful",Toast.LENGTH_SHORT).show();
                            Log.d("debug","Uploaded");
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(),"Failed to upload",Toast.LENGTH_LONG).show();
                            Log.d("debug","Failed to Upload");
                            return;
                        }
                    });
                }
            });
        }

        /*lse{
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String cuid= auth.getCurrentUser().getUid();

            DatabaseReference dr;
            dr = FirebaseDatabase.getInstance().getReference().child("Users").child(cuid);
            dr.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String piurl = dataSnapshot.child("profileImgUrl").getValue().toString();
                    if(piurl.equals("default")){
                        Toast.makeText(getActivity(),"Please chose a profile image and then click on Update",Toast.LENGTH_LONG).show();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }*/

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 &&  resultCode == RESULT_OK && data != null && data.getData() != null){
            Log.d("debug","Image Found");
            resultUri=data.getData();
            Picasso.with(getActivity()).load(resultUri).into(profilepic);

        }
    }

}
