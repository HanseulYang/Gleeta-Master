package com.example.gleetta;

import android.widget.Button;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gleetta.fragment.Home;
import com.example.gleetta.fragment.MyPage;
import com.example.gleetta.fragment.Navigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;

public class UploadCheckActivity extends AppCompatActivity {
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private MyPage fragmentMypage = new MyPage();
    private Home fragmentHome = new Home();
    private Navigation fragmentNavigation = new Navigation();
    private Toolbar toolbar;
    private TextView toolbar_title;
    private long backKeyPressedTime;


    Button home, mypage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadcheck);

        home = (Button)findViewById(R.id.goto_home_btn);
        mypage = (Button)findViewById(R.id.goto_mypage_btn);

        //물론이죠 버튼(HOME)
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Home home = new Home(); // 이게 필요한가?
                //transaction.replace(R.id.action_home, home);

                transaction.replace(R.id.content, fragmentHome);
                transaction.replace(R.id.action_home, home);

                transaction.addToBackStack(null);
                transaction.commit();
                finish();
            }
        });




        mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                MyPage mypage = new MyPage(); // 이게 필요한가?
                transaction.replace(R.id.content, mypage);
                transaction.addToBackStack(null);
                transaction.commit();
                finish();

            }
        });

    }
}