package com.starktech.motherhood;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mMessage;



    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMessage = itemView.findViewById(R.id.showmsg);

    }

    @Override
    public void onClick(View v) {

    }
}
