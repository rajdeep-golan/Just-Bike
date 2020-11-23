package com.just.justbikev2;

import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.just.justbikev2.Fragments.AccountFragment;
import com.just.justbikev2.Fragments.FavoriteFragment;
import com.just.justbikev2.Fragments.HomeFragment;
import com.squareup.picasso.Picasso;

public class Articles extends AppCompatActivity implements View.OnClickListener{

        ImageView btFavorite,btAccount;
        ImageButton btHome;
        String isUpdateProfile;

@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        setContentView(R.layout.articles_screen);
        if(getIntent()!=null){
                isUpdateProfile = getIntent().getStringExtra("updateProfile");
        }
        if(isUpdateProfile!=null && isUpdateProfile.equals("1"))
                loadFragment(new AccountFragment());
        else
                loadFragment(new HomeFragment());

        btHome      = findViewById(R.id.btHome);
        btFavorite  = findViewById(R.id.btFavorite);
        btAccount   = findViewById(R.id.btAccount);
        btHome.setImageResource(R.drawable.ic_home_black);

        btHome.setOnClickListener(this);
        btFavorite.setOnClickListener(this);
        btAccount.setOnClickListener(this);
        }

private boolean loadFragment(Fragment fragment) {
        if (fragment!=null){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container,fragment);
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out);
        fragmentTransaction.commit();
        return true;
        }
        return false;
        }

@Override
public void onClick(View v) {
        switch (v.getId()){
        case R.id.btHome :
                btHome.setImageResource(R.drawable.ic_home_black);
                btAccount.setImageResource(R.drawable.ic_person);
                btFavorite.setImageResource(R.drawable.ic_favorite);
                 loadFragment(new HomeFragment());
        break;
        case R.id.btFavorite:
                btHome.setImageResource(R.drawable.ic_home);
                btAccount.setImageResource(R.drawable.ic_person);
                btFavorite.setImageResource(R.drawable.ic_favorite_black_frag_24dp);
                loadFragment(new FavoriteFragment());
        break;
        case R.id.btAccount :
                btHome.setImageResource(R.drawable.ic_home);
                btAccount.setImageResource(R.drawable.ic_person_black_24dp);
                btFavorite.setImageResource(R.drawable.ic_favorite);
                loadFragment(new AccountFragment());
        break;
        }
        }

        }
