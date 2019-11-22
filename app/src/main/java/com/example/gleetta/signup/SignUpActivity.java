package com.example.gleetta.signup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gleetta.R;
import com.example.gleetta.login.LoginActivity;

import io.reactivex.Flowable;

public class SignUpActivity extends AppCompatActivity {
    private String RegisterURL = "http://10.0.0.2.2/gleetta_register.php";
    public String id_sign_string = "";
    public String password_sign_string = "";
    public String name_sign_string = "";
    public ProgressBar loading = null;
    public Boolean IsUserIdDuplicate = false;
    public Boolean isUserNameDuplicate = false;
    public View signUp_view = null;
    public AlertDialog.Builder signUp_dialogBuilder = null;
    public View signUp_authentication_dialogView = null;
    public View login_view = null;
    public Intent login_intent = null;

    private Context context;
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        EditText id_sign = findViewById(R.id.id_sign);
        EditText password_sign = findViewById(R.id.password_sign);
        EditText name_sign = findViewById(R.id.name_sign);
        Button signupBtn = findViewById(R.id.btn_signup);
        signUp_view = inflater.inflate(R.layout.activity_sign_up, null, false);
        signUp_dialogBuilder = new AlertDialog.Builder(signUp_view.getContext());
        signUp_authentication_dialogView = inflater.inflate(R.layout.signup_popup_for_authentication, null, false);
        login_view = inflater.inflate(R.layout.activity_login, null, false);
        login_intent = new Intent(this, LoginActivity.class);

        /*회원가입은 나중에 시간남으면 다시하기*/

    }
}
