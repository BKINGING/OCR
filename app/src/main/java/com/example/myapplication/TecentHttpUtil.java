package com.example.myapplication;

//import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

//import org.xutils.common.Callback;
//import org.xutils.http.RequestParams;
//import org.xutils.x;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TecentHttpUtil {

    public interface SimpleCallBack {
        void Succ(String result);

        void error();
    }
    //获取身份证信息
    public static void getIdCardDetails(String bitmap, final SimpleCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("ImageBase64", ImageToBase64(bitmap));
        Gson gson = new Gson();
        String param = gson.toJson(params);
        //发送请求 本地封装的https 请求获取返回数据 repsonse
        String response = SignUtil.getAuthTC3("IDCardOCR", param, "2018-11-19");
        if("".equals(response)){
            Log.d("onError", "信息识别错误");
        }else{
            Log.d("Success", "信息识别正确");
            callBack.Succ(response);
        }
    }

    //图片转化成base64字符串
    public static String ImageToBase64(String img) {
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        String imgFile = img;//待处理的图片
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //对字节数组Base64编码
        Base64Util encoder = new Base64Util();
        //返回Base64编码过的字节数组字符串
        return encoder.encode(data);
    }
}
