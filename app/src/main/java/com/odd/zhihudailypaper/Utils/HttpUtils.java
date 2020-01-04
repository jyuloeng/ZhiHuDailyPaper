package com.odd.zhihudailypaper.Utils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtils {


    /**
     * 得到相应url的数据
     * @param url 地址
     * @return
     */
    public static String GetOkHttpResponseData(String url){
        Response response = null;
        String responseData = null;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        Call call =okHttpClient.newCall(request);
        try {
            response = call.execute();
            responseData = response.body().string();
            response.close();
            return responseData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
