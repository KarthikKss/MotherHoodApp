package com.starktech.motherhood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {

    private ArrayList<String> al;
    private Cards cards_data[];
    private arrayAdapter arrayAdapter;
    private int i;
    private Button sgnotbtn,filtersbtn,refreshbtn;
    private ImageView likebtn,dislikebtn;
    private String currentUid;

    private String maxDis,maxAge,showMe,maxkids;
    String userKids;

    private FirebaseAuth mAuth;
    private DatabaseReference userDb;

    ListView listView;
    List<Cards> rowItems;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_explore, container,false);
        sgnotbtn=(Button)rootView.findViewById(R.id.sgnoutbtn);
        filtersbtn=(Button)rootView.findViewById(R.id.filterssbtn);
        refreshbtn=(Button)rootView.findViewById(R.id.refreshbtn);
        likebtn=(ImageView) rootView.findViewById(R.id.likebutton);
        dislikebtn=(ImageView) rootView.findViewById(R.id.dislikebutton);

        mAuth=FirebaseAuth.getInstance();
        currentUid=mAuth.getCurrentUser().getUid();

        userDb = FirebaseDatabase.getInstance().getReference().child("Users");

        rowItems =new ArrayList<>();
        arrayAdapter = new arrayAdapter(rootView.getContext(), R.layout.item, rowItems );

        getFilters();

        // displayInCards();
        displayRelevantCards();

        final SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView)rootView.findViewById(R.id.frame);

        flingContainer.setAdapter(arrayAdapter);

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject

                Cards obj = (Cards) dataObject;
                String userId = obj.getUserId();
                userDb.child(userId).child("connections").child("nope").child(currentUid).setValue(true);

                Toast.makeText(getActivity(),"Dislike",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;
                String userId = obj.getUserId();
                userDb.child(userId).child("connections").child("yeps").child(currentUid).setValue(true);
                isConnectionMatch(userId);
                Toast.makeText(getActivity(),"Like",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
               /* al.add("XML ".concat(String.valueOf(i)));
                arrayAdapter.notifyDataSetChanged();
                Log.d("LIST", "notified");
                i++;*/
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                /*View view = flingContainer.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);*/
            }
        });

        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(getActivity(),"Click",Toast.LENGTH_SHORT).show();
            }
        });

        sgnotbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent i= new Intent(getActivity(),LoginOrRegistration.class);
                startActivity(i);
                return;
            }
        });
        filtersbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getActivity(),FiltersActivity.class);
                startActivity(i);
                return;
            }
        });
        refreshbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getActivity(),NavPage.class);
                getActivity().finish();
                startActivity(i);
                return;
            }
        });

        likebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flingContainer.getTopCardListener().selectRight();
            }
        });
        dislikebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flingContainer.getTopCardListener().selectLeft();
            }
        });


        return rootView;
    }

    private void getFilters() {
        DatabaseReference filtersDb=userDb.child(currentUid).child("filters");
        filtersDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                maxDis = dataSnapshot.child("distance").getValue().toString();
                maxAge = dataSnapshot.child("maxage").getValue().toString();
                showMe = dataSnapshot.child("showme").getValue().toString();
                maxkids = dataSnapshot.child("maxkids").getValue().toString();
                maxkids = maxkids.replaceAll("[^\\d.]", "");

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayRelevantCards() {
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUid) && !dataSnapshot.child("connections").child("yeps").hasChild(currentUid) && Integer.parseInt(dataSnapshot.child("age").getValue().toString())<=Integer.parseInt(maxAge)){
                    userKids = dataSnapshot.child("kids").getValue().toString();
                    userKids = userKids.replaceAll("[^\\d.]", "");
                    if(showMe.equals("All")){
                        if(Integer.parseInt(userKids)<=Integer.parseInt(maxkids)){
                            String profileImageUrl = "default";

                            if(!dataSnapshot.child("profileImageUrl").getValue().equals("default")){
                                profileImageUrl=dataSnapshot.child("profileImageUrl").getValue().toString();
                            }
                            Cards item = new Cards(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), profileImageUrl, dataSnapshot.child("about").getValue().toString());
                            rowItems.add(item);
                            arrayAdapter.notifyDataSetChanged();
                        }

                    }
                    else{
                        if(dataSnapshot.child("isMom").getValue().toString().equals("Yes")){
                            if(Integer.parseInt(userKids)<=Integer.parseInt(maxkids)){
                                String profileImageUrl = "default";

                                if(!dataSnapshot.child("profileImageUrl").getValue().equals("default")){
                                    profileImageUrl=dataSnapshot.child("profileImageUrl").getValue().toString();
                                }
                                Cards item = new Cards(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), profileImageUrl, dataSnapshot.child("about").getValue().toString());
                                rowItems.add(item);
                                arrayAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isConnectionMatch(String userId) {
        DatabaseReference currentUserConnectionDb=userDb.child(currentUid).child("connections").child("yeps").child(userId);
        currentUserConnectionDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Toast.makeText(getActivity(),"New Connection",Toast.LENGTH_LONG).show();

                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    userDb.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUid).child("ChatId").setValue(key);
                    userDb.child(currentUid).child("connections").child("matches").child(dataSnapshot.getKey()).child("ChatId").setValue(key);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void displayInCards(){
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String thisUserId=dataSnapshot.child("userid").getValue().toString();
                if(dataSnapshot.exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUid) && !dataSnapshot.child("connections").child("yeps").hasChild(currentUid) ){
                    String profileImageUrl = "default";

                    if(!dataSnapshot.child("profileImageUrl").getValue().equals("default")){
                        profileImageUrl=dataSnapshot.child("profileImageUrl").getValue().toString();
                    }
                    Cards item = new Cards(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), profileImageUrl, dataSnapshot.child("about").getValue().toString());
                    rowItems.add(item);
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
