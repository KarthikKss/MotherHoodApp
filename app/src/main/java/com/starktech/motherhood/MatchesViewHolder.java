package com.starktech.motherhood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class MatchesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mMatchId,mMatchName;
    public ImageView mMatchImage;
    public Bitmap bmp;



    public MatchesViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mMatchId=(TextView)itemView.findViewById(R.id.matchid);
        mMatchName=(TextView)itemView.findViewById(R.id.matchname);
        mMatchImage=(ImageView)itemView.findViewById(R.id.matchimage);



    }

    @Override
    public void onClick(View v) {

        mMatchImage.invalidate();
        BitmapDrawable drawable = (BitmapDrawable)mMatchImage.getDrawable();
        bmp = drawable.getBitmap();
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG,50,bs);
        Intent intent=new Intent(v.getContext(),ChatActivity.class);
        Bundle b = new Bundle();
        b.putString("matchId",mMatchId.getText().toString());
        b.putByteArray("matchimg",bs.toByteArray());
        intent.putExtras(b);
        v.getContext().startActivity(intent);


    }
}
