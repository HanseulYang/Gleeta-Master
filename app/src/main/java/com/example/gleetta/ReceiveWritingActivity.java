package com.example.gleetta;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gleetta.fragment.Home;
import com.example.gleetta.fragment.MyPage;
import com.example.gleetta.fragment.Navigation;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReceiveWritingActivity extends AppCompatActivity {
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private MyPage fragmentMypage = new MyPage();
    private Home fragmentHome = new Home();
    private Navigation fragmentNavigation = new Navigation();
    private Toolbar toolbar;
    private TextView toolbar_title;
    private long backKeyPressedTime;
    private Button sendPostButton;

    TextView TextView_get;
    Button bt_sendText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receivewriting);

        TextView_get = findViewById(R.id.TextView_get);

        Intent intent = getIntent(); // intent가 받겠다
        String str = intent.getStringExtra("str"); // string 형태로 날라온 데이터 값을 받는다.

        TextView_get.setText(str);
        // 우선 여기까진 완성된 텍스트 보여주기

        bt_sendText = (Button)findViewById(R.id.sendPostToServer_btn);
        bt_sendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UploadCheckActivity.class);
                startActivity(intent);
            }
        });

    }




}