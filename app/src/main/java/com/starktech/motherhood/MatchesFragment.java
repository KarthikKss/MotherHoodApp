package com.starktech.motherhood;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MatchesFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_matches,container,false);

        currentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView=(RecyclerView)rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        mMatchesLayoutManager = new GridLayoutManager(getActivity(),2);
        mRecyclerView.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdapter = new MatchesAdapter(getDataSetMatches(),getActivity());
        mRecyclerView.setAdapter(mMatchesAdapter);

        getUserMatchId();


        return rootView;
    }

    private void getUserMatchId() {

        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("connections").child("matches");
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot match : dataSnapshot.getChildren()){
                        fetchMatchInfo(match.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fetchMatchInfo(String key) {
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String userId = dataSnapshot.getKey();
                    String name="";
                    String profileImageUrl = "";
                    if(dataSnapshot.child("name").getValue() != null){
                        name=dataSnapshot.child("name").getValue().toString();
                        profileImageUrl=dataSnapshot.child("profileImageUrl").getValue().toString();

                    }
                    if(dataSnapshot.child("profileImageUrl").getValue() != null){
                        profileImageUrl=dataSnapshot.child("profileImageUrl").getValue().toString();

                    }
                    MatchesObject object = new MatchesObject(userId,name,profileImageUrl);
                    resultsMatches.add(object);
                    mMatchesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private ArrayList<MatchesObject> resultsMatches = new ArrayList<MatchesObject>();

    private List<MatchesObject> getDataSetMatches() {

        return resultsMatches;
    }
}
