package com.starktech.motherhood;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private List<ChatObject> chatList;
    private Context context;

    private FirebaseAuth auth;
    String fuser;

    public ChatAdapter(List<ChatObject> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
    }


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == MSG_TYPE_RIGHT){
            View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_item_right, viewGroup,false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutView.setLayoutParams(lp);
            ChatViewHolder rcv = new ChatViewHolder((layoutView));
            return rcv;

        }
        else {
            View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_item_left, viewGroup,false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutView.setLayoutParams(lp);
            ChatViewHolder rcv = new ChatViewHolder((layoutView));
            return rcv;

        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder chatViewHolder, int i) {
        chatViewHolder.mMessage.setText(chatList.get(i).getMessage());
       /* if(chatList.get(i).getCurrentUser()){
            chatViewHolder.mMessage.setGravity(Gravity.END);
            chatViewHolder.mMessage.setTextColor(Color.parseColor("#AFAFAF"));
            chatViewHolder.mContainer.setBackgroundResource(R.drawable.chat_send);
            chatViewHolder.mContainer.setGravity(Gravity.RIGHT);



           *//* LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.RIGHT;
            chatViewHolder.mOuterContainer.setLayoutParams(params);*//*

        }
        else{
            chatViewHolder.mMessage.setGravity(Gravity.START);
            chatViewHolder.mMessage.setTextColor(Color.parseColor("#FFFFFF"));
            chatViewHolder.mContainer.setBackgroundResource(R.drawable.chat_receive);

            *//*LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.LEFT;
            chatViewHolder.mOuterContainer.setLayoutParams(params);*//*

        }*/
    }

    @Override
    public int getItemViewType(int position) {
        auth=FirebaseAuth.getInstance();
        fuser=auth.getCurrentUser().getUid();
        if (chatList.get(position).getCurrentUser().equals(true)){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }

    @Override
    public int getItemCount() {

        return this.chatList.size();
    }
}
