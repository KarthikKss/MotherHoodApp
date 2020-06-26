package com.starktech.motherhood;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class NavPage extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_page);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        loadFragment(new ExploreFragment());
    }

    private boolean loadFragment(Fragment fragment){
        if(fragment!=null){

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer,fragment).commit();

            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        switch (menuItem.getItemId()){
            case R.id.navigation_explore :
                fragment = new ExploreFragment();
                break;

            case R.id.navigation_matches :
                fragment = new MatchesFragment();
                break;

            case R.id.navigation_profile :
                fragment = new SettingFragment();
                break;
        }

        return loadFragment(fragment);
    }
}
