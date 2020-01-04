package com.odd.zhihudailypaper.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.odd.zhihudailypaper.Bean.UserBean;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;


public class BaseUtils {

    /**
     * 初始化窗口的样式
     * @param window window对象
     * @param activity activity对象
     */
    public static void initWindow(Window window, Activity activity){
        //  qmui实现沉浸式
        QMUIStatusBarHelper.translucent(activity);

        //  将状态栏字体变为深色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //window.setStatusBarColor(0xFFffffff);  //  设置状态栏颜色
        window.setNavigationBarColor(0xFFFAFAFA);  //  设置导航栏颜色
    }

    /**
     * 弹出空的输入框填写提示
     * @param activity activity对象
     * @param title 空提醒标题
     * @param editText 空的输入框
     */
    public static void showEmptyDialog(Activity activity, String title, EditText editText){
        new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("请输入"+title+"哦~")
                .setContentText("  ")
                .setConfirmText("好吧")
                .show();
        editText.requestFocus();
    }

    /**
     * 判断是否为手机号码的正确格式
     * @param telephone
     * @return
     */
    public static boolean isTelephone(String telephone){
        String pattern = "^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";
        return Pattern.matches(pattern,telephone);
    }

    /**
     * 判断是否为邮箱的正确格式
     * @param email
     * @return
     */
    public static boolean isEmail(String email){
        String pattern = "^[\\\\w-]+@[\\\\w-]+\\\\.(com|net|org|edu|mil|tv|biz|info)$";
        return Pattern.matches(pattern,email);
    }

    /**
     * 根据天数获得当前日期的前几天
     * @param day 几天前
     * @return
     */
    public static String getOldDate(int day){
        Date begin = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar date = Calendar.getInstance();

        date.setTime(begin);
        date.set(Calendar.DATE,date.get(Calendar.DATE) - day);
        Date endDate = null;

        try {
            endDate = sdf.parse(sdf.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sdf.format(endDate);
    }

    /**
     * 根据天数获得当前日期的前几天的格式
     * @param day 几天前
     * @return
     */
    public static String getOldDateMd(int day){
        Date begin = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
        Calendar date = Calendar.getInstance();

        date.setTime(begin);
        date.set(Calendar.DATE,date.get(Calendar.DATE) - day);
        Date endDate = null;

        try {
            endDate = sdf.parse(sdf.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sdf.format(endDate);
    }

    /**
     * 弹出SweetAlertDialog
     * @param context 上下文对象
     * @param type 弹出类型
     * @param title 弹出题目
     * @param contentText 弹出内容
     * @param confirmtText 弹出回应
     */
    public static void showAlertDialog(Context context,int type,String title,String contentText,String confirmtText){
        new SweetAlertDialog(context, type)
                .setTitleText(title)
                .setContentText(contentText)
                .setConfirmText(confirmtText)
                .show();
    }

    /**
     * 从本地缓存获得用户信息
     * @param context
     * @return
     */
    public static UserBean getUserData(Context context){
        SharedPreferences preferences = context.getSharedPreferences(UserBean.USER_DATA,Context.MODE_PRIVATE);

        UserBean userBean = new UserBean();

        userBean.setId(preferences.getInt(UserBean.ID,-1));
        userBean.setAccount(preferences.getString(UserBean.ACCOUNT,""));
        userBean.setPassword(preferences.getString(UserBean.PASSWORD,""));
        userBean.setName(preferences.getString(UserBean.NAME,""));
        userBean.setTelephone(preferences.getString(UserBean.TELEPHONE,""));
        userBean.setAvatar(preferences.getString(UserBean.AVATAR,""));

        return userBean;
    }

    /**
     * 修改本地缓存信息
     * @param context 上下文对象
     * @param dataType 数据类型
     * @param newData 新的数据
     */
    public static void editUserData(Context context,String dataType,String newData){
        SharedPreferences.Editor editor = context.getSharedPreferences(UserBean.USER_DATA,context.MODE_PRIVATE).edit();
        editor.putString(dataType,newData);
        editor.apply();
    }

    /**
     * 判断是否登录
     * @param userBean
     * @return
     */
    public static boolean isLogin(UserBean userBean){
        if(userBean.getId() != -1){
            return true;
        }
        return false;
    }

    /**
     * 退出登录
     * @param context
     */
    public static void quit(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(UserBean.USER_DATA,context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    /**
     * 上传图片到服务器
     * @param uploadUrl
     * @param srcPath
     * @return
     */
    public static String uploadFile(String uploadUrl,String srcPath){
        String result="err";
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        try
        {
            URL url = new URL(uploadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();
            // 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
            // 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。
            httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
            // 允许输入输出流
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            // 使用POST方法
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(
                    httpURLConnection.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""
                    + srcPath.substring(srcPath.lastIndexOf("/") + 1)
                    + "\""
                    + end);
            dos.writeBytes(end);

            FileInputStream fis = new FileInputStream(srcPath);
            byte[] buffer = new byte[8192]; // 8k
            int count = 0;
            // 读取文件
            while ((count = fis.read(buffer)) != -1)
            {
                dos.write(buffer, 0, count);
            }
            fis.close();

            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();

            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            StringBuffer sb1 = new StringBuffer();
            int ss;
            while ((ss = is.read()) != -1) {
                sb1.append((char) ss);
            }
            result = sb1.toString();
            result = result.replaceAll("(\\r\\n|\\n|\\n\\r)","");
            dos.close();
            is.close();


        } catch (Exception e)
        {
            e.printStackTrace();

        }
        finally {

        }
        return result;
    }

}
