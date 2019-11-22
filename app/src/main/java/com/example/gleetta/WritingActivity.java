package com.example.gleetta;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gleetta.fragment.Home;
import com.example.gleetta.fragment.MyPage;
import com.example.gleetta.fragment.Navigation;

public class WritingActivity extends AppCompatActivity {
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private MyPage fragmentMypage = new MyPage();
    private Home fragmentHome = new Home();
    private Navigation fragmentNavigation = new Navigation();
    private Toolbar toolbar;
    private TextView toolbar_title;
    private long backKeyPressedTime;


    private Button send_text;
    private EditText write_text;
    private String str; // 빈 문자열 생성_사용자 입력 받아 저장하는 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        write_text = findViewById(R.id.write_text);
        // 위치 바뀜 str = et_test.getText().toString(); // 사용자가 입력한 것을 받음(string으로 변환 필요)

        // send_text = findViewById(R.id.write_text); // 해당 아이디를 찾아와라
        write_text.setOnClickListener(new View.OnClickListener(){
            // btn_move 라는 버튼을 클릭했을 때 안쪽을 실행하라(onClick)

            @Override
            public void onClick(View v) {
                str = write_text.getText().toString();
                Intent intent = new Intent(WritingActivity.this, ReceiveWritingActivity.class); //intent 라는 객체 생성/ (현재 액티비티, 이동하고 싶은 액티비티)
                intent.putExtra("str", str); // 별명으로 받아옴 & 실제로 들어가는 데이터 공간
                startActivity(intent);  // 액티비티 이동해주는 구문

                // SUbActivity로 쏘았으니 이제 sub에서 받는 것을 해보자
            }
        });

    }
}