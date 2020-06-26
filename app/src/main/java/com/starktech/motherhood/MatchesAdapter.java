package com.starktech.motherhood;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.List;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesViewHolder> {

    private List<MatchesObject> matchesList;
    private Context context;

    public MatchesAdapter(List<MatchesObject> matchesList,Context context){
        this.matchesList=matchesList;
        this.context=context;
    }

    @NonNull
    @Override
    public MatchesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_matches,null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MatchesViewHolder rcv = new MatchesViewHolder((layoutView));


        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchesViewHolder matchesViewHolder, int i) {

        matchesViewHolder.mMatchId.setText(matchesList.get(i).getUserId());
        matchesViewHolder.mMatchName.setText(matchesList.get(i).getName());
        if(!matchesList.get(i).getProfileImageUrl().equals("default")){
            String blah = matchesList.get(i).getProfileImageUrl();
            Glide.with(context).load(matchesList.get(i).getProfileImageUrl()).asBitmap().into(matchesViewHolder.mMatchImage);

        }

    }

    @Override
    public int getItemCount() {
        Log.d("Debug","no. of matches is "+Integer.toString(matchesList.size()));
        return this.matchesList.size();
    }
}
