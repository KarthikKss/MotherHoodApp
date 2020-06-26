package com.starktech.motherhood;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class arrayAdapter extends ArrayAdapter<Cards> {


    Context context;


    public arrayAdapter(Context context, int resourceId, List<Cards> items) {
        super(context, resourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Cards cardItem=getItem(position);
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item,parent,false);
        }

        TextView name = (TextView)convertView.findViewById(R.id.name);
        TextView about = (TextView)convertView.findViewById(R.id.about);
        ImageView image=(ImageView)convertView.findViewById(R.id.image);

        Log.d("debug","Name "+cardItem.getCardname()+" About "+cardItem.getAbout());

        String temp=cardItem.getAbout();

        name.setText(cardItem.getCardname());
        about.setText(temp);

        //image.setImageResource(R.mipmap.ic_launcher);

        switch (cardItem.getProfileImageUrl()){
            case "default":
                Glide.with(convertView.getContext()).load(R.drawable.defimg).into(image);
                break;

            default:
                Glide.clear(image);
                Glide.with(convertView.getContext()).load(cardItem.getProfileImageUrl()).into(image);
                break;
        }


        return convertView;

    }


}
