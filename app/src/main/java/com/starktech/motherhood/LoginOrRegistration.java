package com.starktech.motherhood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class LoginOrRegistration extends AppCompatActivity {

    private Button loginBtn;
    private Button regBtn;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_registration);

        loginBtn=(Button)findViewById(R.id.loginbtn);
        regBtn=(Button)findViewById(R.id.regbtn);
        logo=(ImageView)findViewById(R.id.logo);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(LoginOrRegistration.this,Login.class);
                startActivity(intent1);
                //finish();
                return;
            }
        });
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2=new Intent(LoginOrRegistration.this,Registration.class);
                startActivity(intent2);
                //finish();
                return;
            }
        });
    }
}
