package com.example.gleetta;

import android.os.Bundle;

import com.example.gleetta.fragment.Home;
import com.example.gleetta.fragment.MyPage;
import com.example.gleetta.fragment.Navigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private MyPage fragmentMypage = new MyPage();
    private Home fragmentHome = new Home();
    private Navigation fragmentNavigation = new Navigation();
    private Toolbar toolbar;
    private TextView toolbar_title;
    private long backKeyPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 하단바
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavi);

        // 상단바(툴바)
        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        toolbar_title = findViewById(R.id.app_toolbar_title);
        setSupportActionBar(toolbar);
        // 툴바 커스터마이징
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); // 커스터마이징하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // 첫 화면 지정
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, fragmentMypage).commitAllowingStateLoss();
        toolbar_title.setText("나의 발자취");

        //

        // bottomView의 아이템이 선택될 때 호출될 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());
    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (menuItem.getItemId()) {
                case R.id.action_mypage:
                    transaction.replace(R.id.content, fragmentMypage).commitAllowingStateLoss();
                    toolbar_title.setText("나의 발자취");
                    break;
                case R.id.action_home:
                    transaction.replace(R.id.content, fragmentHome).commitAllowingStateLoss();
                    toolbar_title.setText("모아보기");
                    break;
                case R.id.action_navigation:
                    transaction.replace(R.id.content, fragmentNavigation).commitAllowingStateLoss();
                    toolbar_title.setText("빔 위치 찾기");
                    break;
            }
            return true;
        }
    }

    // 툴바에 메뉴 리소스 파일 적용
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar, menu);

        return true;
    }

    // 메뉴 선택 이벤트
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            // 뒤로 가기 버튼 눌렀을 때
            case android.R.id.home: {
                onBackPressed();
            }
            // 검색 버튼 눌렀을 때
            case R.id.app_toolbar_search_button: {

            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "뒤로 가기 버튼을 한번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else {
            moveTaskToBack(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}