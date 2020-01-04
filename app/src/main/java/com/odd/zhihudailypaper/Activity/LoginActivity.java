package com.odd.zhihudailypaper.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.odd.zhihudailypaper.Bean.UserBean;
import com.odd.zhihudailypaper.R;
import com.odd.zhihudailypaper.Utils.BaseUtils;
import com.odd.zhihudailypaper.Utils.HttpUtils;
import com.odd.zhihudailypaper.Utils.JsonUtils;
import com.odd.zhihudailypaper.Utils.NetUtils;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static String URL_LOGIN = "http://test.xkspbz.com/odd.zhihudaily/login.php?account=";

    private Window mWindow;
    private Button btn_back,btn_login;
    private EditText et_username,et_password;
    private TextView tv_register,tv_forget_password;

    private String account,password;

    private UserBean userBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //  初始化窗口
        mWindow = getWindow();
        BaseUtils.initWindow(mWindow,this);

        initView();
    }

    private void initView() {
        btn_back = findViewById(R.id.btn_login_back);
        btn_login = findViewById(R.id.btn_login);

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);

        tv_register = findViewById(R.id.tv_register);
        tv_forget_password = findViewById(R.id.tv_forget_password);

        btn_back.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        tv_forget_password.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_login_back:
                finish();
                break;
            case R.id.btn_login:
                getData();
                login();
                break;
            case R.id.tv_register:
                Intent login_intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(login_intent);
                break;
            case R.id.tv_forget_password:
                Intent fg_password_intent = new Intent(LoginActivity.this,ForgetPasswordActivity.class);
                startActivity(fg_password_intent);
                break;
        }
    }

    /**
     * 获得登录需要的数据
     */
    private void getData() {
        account = et_username.getText().toString().trim();
        password = et_password.getText().toString().trim();

        if(account.isEmpty()){
            BaseUtils.showEmptyDialog(this,"用户账号",et_username);
            return;
        }else if(password.isEmpty()){
            BaseUtils.showEmptyDialog(this,"用户密码",et_password);
            return;
        }

        //  判断是否为正确的用户账号格式
        if(!BaseUtils.isTelephone(account) && !BaseUtils.isEmail(account)){
            BaseUtils.showAlertDialog(this,SweetAlertDialog.WARNING_TYPE,
                    "用户账号格式输入错误哦~", "  ","是吗");
            et_username.findFocus();
            return;
        }
    }

    /**
     * 发送登录请求
     */
    private void login() {
        //  开一个线程进行发送请求操作
        if(NetUtils.isNetworkAvailable(this)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String responseData = HttpUtils.GetOkHttpResponseData(URL_LOGIN+account+"&password="+password);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parsePhpData(responseData);
                        }
                    });
                }
            }).start();
        }else{
            NetUtils.isNetworkAvailable(this);
        }

    }

    /**
     * 解析php返回的数据，1表示登录成功，跳转页面，0表示登录失败
     * @param responseData php返回数据
     */
    private void parsePhpData(String responseData){
        userBean = new UserBean();

        JsonUtils.ParsePhpDataJsonToUserBean(responseData,userBean);

        try{
            userBean.getAccount().isEmpty();
            cacheDataInLocal();

            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);

            finish();
        }catch (Exception e){
            BaseUtils.showAlertDialog(this,SweetAlertDialog.WARNING_TYPE,
                    "用户名或密码错误哦~", "  ","我再试试");
        }
    }

    /**
     * 使用缓存将内容缓存到本地xml文件
     */
    private void cacheDataInLocal(){
        SharedPreferences.Editor editor = getSharedPreferences(UserBean.USER_DATA,MODE_PRIVATE).edit();
        editor.putInt(UserBean.ID,userBean.getId());
        editor.putString(UserBean.ACCOUNT,userBean.getAccount());
        editor.putString(UserBean.PASSWORD,userBean.getPassword());
        editor.putString(UserBean.NAME,userBean.getName());
        editor.putString(UserBean.TELEPHONE,userBean.getTelephone());
        editor.putString(UserBean.AVATAR,userBean.getAvatar());
        editor.apply();
    }
}
