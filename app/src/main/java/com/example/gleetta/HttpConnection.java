package com.example.gleetta;

import android.content.Context;
import android.util.Log;

import com.example.gleetta.data.SharedPreferenceMangaer;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*okhttp를 이용한 http통신을 위한 클래스를 따로 만든다.*/
public class HttpConnection {
    private OkHttpClient client;
    private static HttpConnection instance = new HttpConnection();

    public static HttpConnection getInstance() {
        return instance;
    }

    private HttpConnection(){
        this.client = new OkHttpClient();
    }

    // 사용자 정보를 통해 웹서버로부터 데이터 가져올 때
    public void request_to_get_something_by_userInfo(String url, String username, String password, Callback callback) {
        RequestBody body = new FormBody.Builder()
                .add("user_name", username)
                .add("user_password", password)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static JSONObject sendToServer(String imageUploadURL, String sourceImageFile){
        try {
            File sourceFile = new File(sourceImageFile);
            Log.d("응답", "File...::::" + sourceFile + ":" + sourceFile.exists());
            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
            String filename = sourceImageFile.substring(sourceImageFile.lastIndexOf("/")+1);
            Log.d("응답", "filename : " + filename);
            // okhttp3
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("uploaded_file", filename, RequestBody.create(MEDIA_TYPE_PNG, sourceFile))
                    .addFormDataPart("result", "photo_image")
                    .build();
            Request request = new Request.Builder()
                    .url(imageUploadURL)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            Log.d("응답", "전송 결과: " + res);
            return new JSONObject(res);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("응답", "오류 : " + e.getLocalizedMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("응답", "오류 : " + e.getLocalizedMessage());
        }
        return null;
    }

    // 서버에 업로드된 파일 데이터 db에 업데이트
    public void insert_data_for_uploaded_file(String url, String username, String beamLocation, String material, String filename, Callback callback) {
        RequestBody body = new FormBody.Builder()
                .add("user_id", username)
                .add("material_content", material)
                .add("beam_location", beamLocation)
                .add("file_path", filename)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

}
