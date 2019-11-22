package com.example.gleetta;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gleetta.data.MediaScanner;
import com.example.gleetta.data.SharedPreferenceMangaer;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.NetworkInterface;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_WIFI;

public class PostingActivity extends AppCompatActivity {
    private final int MY_PERMISSIONS_REQUEST_STORAGE = 1002;
    // activity, context 변수
    private Context mContext;
    private Activity mActivity = this;
    // 파일 서버에 전송 관련
    private Button sendPostButton;
    private String userName;
    private String userName_full;
    private String file_path_for_update_db;
    private Bitmap bitmap;
    private File bmpFp;
    // http 통신
    private HttpConnection httpConn = HttpConnection.getInstance();
    // 이미지 업로드 관련
    private ImageButton attachImageButton;
    private final String IMG_FILE_PATH = "imgfilepath";
    private final String IMG_TITLE = "imgtitle";
    private final String IMG_ORIENTATION = "imgorientation";
    private final int REQ_CODE_SELECT_IMAGE = 1001;
    private String imagePath = null;
    private String imageTitle = null;
    private String imageOrientation = null;
    // 그림판 관련
    private ImageButton drawingModeButton;
    //글 관련
    private ImageButton writingModeButton;

    // 뷰가 추가될 linearLayout
    LinearLayout layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        mContext = this;
        userName = SharedPreferenceMangaer.getString(this, "user_name");
        userName_full = userName;
        userName = userName.substring(0, userName.lastIndexOf("@"));
        attachImageButton = findViewById(R.id.attachImage_btn);
        layout = (LinearLayout)findViewById(R.id.container_posting);
        sendPostButton = findViewById(R.id.sendPostToServer_btn);
        drawingModeButton = findViewById(R.id.drawingMode_btn);
        writingModeButton = findViewById(R.id.writingMode_btn);

        // 파일 첨부 버튼 클릭
        attachImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getGallery();
            }
        });

        // 그림 버튼 클릭
        drawingModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*그림판 툴이 container_posting에 나와야 함.*/
                Intent intent = new Intent(getApplicationContext(), DrawingActivity.class);
                startActivity(intent);

            }
        });

        writingModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WritingActivity.class);
                startActivity(intent);
            }
        });

        // 확인 버튼 클릭
        sendPostButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /* 레이아웃을 이미지 캡쳐해서 서버에 전송 */
                // 일단은 갤러리에 저장하는 걸로 이미지 캡쳐 테스트
                saveLayoutCapture();
                // 업로드 완료 화면으로 넘어가기
                Intent intent = new Intent(getApplicationContext(), UploadCheckActivity.class);
                startActivity(intent);
            }
        });


        /*사용자 권한 얻기*/
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 승인이 필요합니다", Toast.LENGTH_LONG).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(this, "저장장치 권한이 필요합니다.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_STORAGE);
                Toast.makeText(this, "저장장치 권한이 필요합니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){
                    Toast.makeText(mContext, "승인이 허가되어 있습니다.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, "아직 승인받지 않았습니다.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    // 사진 선택을 위해 갤러리를 호출
    private void getGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        if (Build.VERSION.SDK_INT >= 19) {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        }
        intent.setType("image/*");
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
    }

    // 갤러리에서 선택된 사진을 받아서 레이아웃에 이미지뷰 동적 생성
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    /*이미지뷰 동적으로 추가하고 사진 붙이기*/
                    addImageView(img);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED){
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    // 이미지 뷰 동적으로 추가
    private void addImageView(Bitmap image){
        // layout param 생성
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        // 이미지뷰 생성 및 추가
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(image);
        imageView.setLayoutParams(layoutParams);
        layout.addView(imageView);
    }

    // linearlayout 이미지 캡쳐
    public void saveLayoutCapture(){
        // 년 월 일 시간 포맷 설정 + 사용자이름까지
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date time = new Date();
        String currentTime_and_userName = sdf.format(time);
        String namePostFix = currentTime_and_userName + "_" + userName;
        Log.d("응답 ", namePostFix);
        // 지정한 layout영역 사진첩 저장 요청
        Request_Capture(layout, namePostFix + "_capture.png");
        file_path_for_update_db = namePostFix + "_capture.png";
    }

    public void Request_Capture(View view, String title) {
        if (view == null) {
            Log.d("응답", "view가 null값임");
            return;
        }
        int layout_width = view.getWidth();
        int layout_height = view.getHeight();
        view.clearFocus();
        view.setPressed(false);
        view.invalidate();
        bitmap = Bitmap.createBitmap(layout_width, layout_height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        // 레이아웃 캡쳐한 거 자동으로 갤러리에 저장됨.
        saveInLocal(title);
        /* 레이아웃 캡쳐한 거 서버에 전송 */
        if (!TextUtils.isEmpty(imagePath)) {
            if(getInternetStatus(mContext) != 0) {
                String ImageUploadURL = "http://13.209.159.14/gleetta/upload/upload.php";
                new ImageUploadTask().execute(ImageUploadURL, imagePath);
            } else {
                Toast.makeText(mContext, "인터넷 연결을 확인하세요", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(mContext, "먼저 업로드할 파일을 선택하세요", Toast.LENGTH_SHORT).show();
        }
    }

    private class ImageUploadTask extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("이미지 업로드중...");
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                JSONObject jsonObject = HttpConnection.sendToServer(params[0], params[1]);
                if (jsonObject != null)
                    return jsonObject.getString("result").equals("success ");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("응답", "json 오류 " + e.getLocalizedMessage());
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (progressDialog != null)
                progressDialog.dismiss();
            if (aBoolean) {
                // 위치는 db에서 빔 위치 데이터 가지고 와야 되고, 단어도 db에서 가져와야 됨
                httpConn.insert_data_for_uploaded_file("http://13.209.159.14/gleetta/upload/insertDataInfo.php", userName_full, "서울특별시 용산구 청파동2가 청파로47길 100 눈꽃광장", "행복", file_path_for_update_db, callback);

            }
            else
                Toast.makeText(getApplicationContext(), "파일 업로드 실패", Toast.LENGTH_LONG).show();

            imagePath = "";
        }
    }

    private final Callback callback = new Callback(){
        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            Log.d("응답", response.body().string());
            //Toast.makeText(getApplicationContext(), "파일 업로드 성공", Toast.LENGTH_LONG).show();
            // posting 액티비티 종료
             finish();
        }
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            Log.d("응답 ", e.getMessage());
        }
    };

    public static int getInternetStatus(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null){
            if (activeNetwork.getSubtype() == TYPE_WIFI) {
                return 1;
            } else if (activeNetwork.getSubtype() == TYPE_MOBILE){
                return 2;
            }
        }
        return 0;
    }

    public void saveInLocal(String title){
        // 로컬 sd카드 절대경로 구하기
        String savePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String basePath = savePath + "/gleetta/";

        // 파일 저장
        File fp = new File(basePath);
        if (!fp.exists()){
            try{
                fp.mkdirs();
                Log.d("응답", "폴더 생성 완료");
            } catch (Exception e){
                Log.d("응답", "폴더 생성 실패" + e.toString());
            }
        }
        try{
            fp.exists();
        } catch(Exception e) {
            Log.d("응답", e.getMessage());
        }
        bmpFp = new File(basePath, title);
        imagePath = basePath + title;
        try{
            OutputStream os = new FileOutputStream(bmpFp);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, os);
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("응답", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("응답", e.getMessage());
        } finally {
            bitmap.recycle();
        }
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + bmpFp)));
    }

    // 해당 장소 사용 가능한지
    public static boolean checkAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    // 해당 장소에 쓰기 작업 가능한지
    public static boolean checkWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){
            if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

}
