package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.app.Activity;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Button btn_scan;
    private TextView textView;

    private Uri imageUri;
    private File outputImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_scan = findViewById(R.id.btn1);
        textView = findViewById(R.id.tv1);

        btn_scan.setOnClickListener(v -> takePhoto());
    }

    private void takePhoto() {
        outputImage = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "output_image.jpg");
        if (outputImage.exists()) {
            outputImage.delete();
        }
        try {
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageUri = FileProvider.getUriForFile(this, "com.example.myapplication.fileprovider", outputImage);
//        Uri imageUri = FileProvider.getUriForFile(MainActivity.this, getApplicationContext().getPackageName() + ".provider", outputImage);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        launcher.launch(intent);
    }

    public ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // 使用身份证识别接口获取
                        Log.d("bky", "onActivityResult: ");
                        IdCardDetails(getHandler_IdCard);
                    }
                }
            });

    Handler getHandler_IdCard = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Log.d("bky", "handleMessage: ");
            // 处理识别结果，更新UI等操作
            return true;
        }
    });

    /**
     * 身份证扫描结果
     */
    public void IdCardDetails(final Handler handler) {
        String imagePath = outputImage.getAbsolutePath();
        TecentHttpUtil.getIdCardDetails(imagePath, new TecentHttpUtil.SimpleCallBack() {
            @Override
            public void Succ(String result) {
                Log.d("bky", "Succ: ");
                // 解析身份证信息，更新UI等操作
                handleIdCardResult(result);
            }
            @Override
            public void error() {
                // 处理识别错误，更新UI等操作
                handleIdCardError();
            }
        });
    }

    private void handleIdCardResult(String result) {
        // 解析腾讯云返回的身份证信息，更新UI等操作
        Gson gson = new Gson();
        IdentifyResult identifyResult = gson.fromJson(result, IdentifyResult.class);
        Log.d("bky", "handleIdCardResult: " + result);
        // 将身份证信息显示在 TextView 中
        updateTextView(identifyResult);
        Log.d("bky", "handleIdCardResult: "+identifyResult.getResponse().getName());
    }

    private void updateTextView(IdentifyResult identifyResult) {
        runOnUiThread(() -> {
            String info = "姓名：" + identifyResult.getResponse().getName() + "\n"
                    + "性别：" + identifyResult.getResponse().getSex() + "\n"
                    + "民族：" + identifyResult.getResponse().getNation() + "\n"
                    + "出生日期：" + identifyResult.getResponse().getBirth() + "\n"
                    + "地址：" + identifyResult.getResponse().getAddress() + "\n"
                    + "身份证号：" + identifyResult.getResponse().getId();

            Log.d("bky", "updateTextView: ");
            textView.setText(info);
        });
    }

    private void handleIdCardError() {
        // 处理身份证识别错误，更新UI等操作
        runOnUiThread(() -> textView.setText("身份证识别失败"));
    }

    public void toast(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }
}
