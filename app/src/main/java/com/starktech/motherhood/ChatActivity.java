package com.starktech.motherhood;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private NestedScrollView mNestedScrollView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    private String currentUserId,matchId,chatId;
    private TextView chatname;
    private ImageView chatprofpic;
    String chatpic;

    private EditText mSendEditText;
    private Button mSendButton;

    DatabaseReference mDatabaseUser,mDatabaseChat;

    StorageReference chatstoragereference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        matchId=getIntent().getExtras().getString("matchId");

        currentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("connections").child("matches").child(matchId).child("ChatId");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");

        getChatId();

        mNestedScrollView = (NestedScrollView)findViewById(R.id.scrollid);
        mNestedScrollView.fullScroll(View.FOCUS_DOWN);


        mRecyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);

        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(getDataSetChat(),ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);

        mSendEditText=(EditText)findViewById(R.id.msg);
        mSendButton = (Button)findViewById(R.id.sendbtn);
        chatname = (TextView)findViewById(R.id.chatprofname);
        chatprofpic = (ImageView)findViewById(R.id.chatprofpic);

        chatstoragereference = FirebaseStorage.getInstance().getReference().child("profileimages").child(matchId);

        setChatData();
        /*if(chatpic!="default"){
            Glide.with(this).load(chatstoragereference).into(chatprofpic);
        }*/
        if(getIntent().hasExtra("matchimg")){

            Bitmap bm = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("matchimg"),0,getIntent().getByteArrayExtra("matchimg").length);
            chatprofpic.setImageBitmap(bm);
        }


        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void setChatData() {
        DatabaseReference chatdb= FirebaseDatabase.getInstance().getReference().child("Users").child(matchId);
        chatdb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String chatnam = dataSnapshot.child("name").getValue().toString();
                chatpic = dataSnapshot.child("profileImageUrl").getValue().toString();
                chatname.setText(chatnam);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {
        String sendMessageText = mSendEditText.getText().toString();
        if(!sendMessageText.isEmpty()){
            DatabaseReference newMessageDb = mDatabaseChat.push();
            Map newMessage = new HashMap();
            newMessage.put("createdByUser",currentUserId);
            newMessage.put("text",sendMessageText);

            newMessageDb.setValue(newMessage);

        }
        mSendEditText.setText(null);

        mNestedScrollView.fullScroll(View.FOCUS_DOWN);

    }

    private void getChatId(){
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    chatId = dataSnapshot.getValue().toString();
                    mDatabaseChat = mDatabaseChat.child(chatId);
                    getChatMessages();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getChatMessages() {
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    String message = null;
                    String createdByUser = null;
                    if(dataSnapshot.child("text").getValue()!=null){
                        message= dataSnapshot.child("text").getValue().toString();
                    }
                    if(dataSnapshot.child("createdByUser").getValue()!=null){
                        createdByUser= dataSnapshot.child("createdByUser").getValue().toString();
                    }

                    if(message!=null && createdByUser!=null){
                        Boolean currentUserBoolean = false;
                        if(createdByUser.equals(currentUserId)){
                            currentUserBoolean = true;
                        }

                        ChatObject newMessage = new ChatObject(message,currentUserBoolean);
                        resultsChat.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();
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

    private ArrayList<ChatObject> resultsChat = new ArrayList<ChatObject>();

    private List<ChatObject> getDataSetChat() {

        return resultsChat;
    }
}
