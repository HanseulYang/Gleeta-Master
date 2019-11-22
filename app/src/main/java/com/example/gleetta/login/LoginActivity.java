package com.example.gleetta.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gleetta.HttpConnection;
import com.example.gleetta.MainActivity;
import com.example.gleetta.R;
import com.example.gleetta.data.SharedPreferenceMangaer;
import com.example.gleetta.signup.SignUpActivity;
import com.jakewharton.rxbinding2.widget.RxTextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    public Button signupBtn;
    public Intent signup_intent;
    private EditText username;
    private EditText password;
    public Button login_btn;
    public ProgressBar loading;
    public String username_input_string;
    public String password_input_string;
    private Context mContext;
    // http 통신
    private HttpConnection httpConn = HttpConnection.getInstance();

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;

        signup_intent = new Intent(this, SignUpActivity.class);

        signupBtn = findViewById(R.id.signup);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.login);
        loading = findViewById(R.id.loading);

        // 회원가입 버튼 누르면 처리되는 이벤트
        signupBtn.setOnClickListener(view -> startActivity(signup_intent));

        // 로그인 데이터 저장되어있으면 자동로그인 되도록
        if (SharedPreferenceMangaer.getString(mContext, "user_name").length() > 0){
            Log.d("응답", "로그인 데이터 저장되어있음.");
            username_input_string = SharedPreferenceMangaer.getString(mContext, "user_name");
            password_input_string = SharedPreferenceMangaer.getString(mContext, "user_password");
            username.setText(SharedPreferenceMangaer.getString(mContext, "user_name"));
            password.setText(SharedPreferenceMangaer.getString(mContext, "user_password"));
            authenticateLoginData_and_returnResult();
        }

        // rxjava로 edittext에 입력되는 것들 유효성 검사
        RxTextView.textChanges(username)
                .subscribe(value -> {
                    username_input_string = value.toString();
                    isUsernameValid(username_input_string);
                    if (!isUsernameValid(username_input_string)){
                        username.setError("올바른 이메일 형식이 아닙니다.");
                    } else {
                        if (isAllDataVaild()){
                            login_btn.setEnabled(true);
                        } else {
                            login_btn.setEnabled(false);
                        }
                    }
                });
        RxTextView.textChanges(password)
                .subscribe(value -> {
                    password_input_string = value.toString();
                    isPassWordValid(password_input_string);
                    if (!isPassWordValid(password_input_string)){
                        password.setError("비밀번호는 알파벳과 숫자를 포함하고 7글자 이상이어야 합니다.");
                    } else {
                        if (isAllDataVaild()){
                            login_btn.setEnabled(true);
                        } else {
                            login_btn.setEnabled(false);
                        }
                    }
                });

        login_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                authenticateLoginData_and_returnResult();
            }
        });
    }

    private void authenticateLoginData_and_returnResult(){
        // 네트워크 통신하는 작업은 무조건 작업스레드를 생성해서 호출해줄 것!
        new Thread() {
            public void run(){
                httpConn.request_to_get_something_by_userInfo("http://13.209.159.14/gleetta/loginAuthenticate.php",username_input_string, password_input_string, callback);
            }
        }.start();
    }

    private final Callback callback = new Callback() {
        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            String data = response.body().string();
            try {
                JSONObject result_array = new JSONObject(data);
                // 사용자 인증 성공 시
                if (result_array.getString("user_name").equals(username_input_string)){
                    Log.d("응답", "인증성공함");
                    // 로그인 로컬 데이터 저장 안 되어 있을 때 사용자 데이터 로컬에 파일로 저장
                    if (SharedPreferenceMangaer.getString(mContext, "user_name").length() < 1){
                        SharedPreferenceMangaer.setString(mContext, "user_name", username_input_string);
                        SharedPreferenceMangaer.setString(mContext, "user_password", password_input_string);
                    }
                    // 홈 화면으로 넘어가기
                    Intent mainIntent = new Intent(mContext, MainActivity.class);
                    startActivity(mainIntent);
                }
                // 사용자 인증 실패 시
                else {
                    Log.d("응답", "인증실패함");
                    Toast.makeText(mContext, "로그인에 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Log.d("응답", e.getMessage());
            }
        }
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            Log.d("응답", e.getMessage());
        }
    };


    private Boolean isUsernameValid(String username) {
        if (Patterns.EMAIL_ADDRESS.matcher(username).matches() && !(username.isEmpty())) {
            return true;
        } else {
            return false;
        }
    }

    private Boolean isPassWordValid(String password){
        if ( (password.length() > 6) && (password.matches(".*[0-9].*")) && (password.matches(".*[A-Za-z].*"))){
            return true;
        } else {
            return false;
        }
    }

    private Boolean isAllDataVaild(){
        if (isUsernameValid(username_input_string) && isPassWordValid(password_input_string)){
            return true;
        } else {
            return false;
        }
    }

}

