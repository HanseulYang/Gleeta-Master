package com.example.gleetta.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gleetta.MainActivity;
import com.example.gleetta.PostingActivity;
import com.example.gleetta.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MyPage extends Fragment {
    private FloatingActionButton uploadPostBtn;
    private Context mContext;

    public MyPage(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);
        uploadPostBtn = view.findViewById(R.id.upload_post_btn);

        // + 버튼 눌렀을 때
        uploadPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // postingActivity 띄우기
                Intent intent = new Intent(getActivity(), PostingActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }





}
