package com.odd.zhihudailypaper.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.odd.zhihudailypaper.Bean.UserBean;
import com.odd.zhihudailypaper.R;
import com.odd.zhihudailypaper.Utils.BaseUtils;
import com.odd.zhihudailypaper.Utils.HttpUtils;
import com.odd.zhihudailypaper.Utils.NetUtils;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static String URL = "http://test.xkspbz.com/odd.zhihudaily/register.php?account=";

    private Window mWindow;
    private TextView tv_login;
    private Button btn_register_back,btn_register;
    private EditText et_register_username,et_register_password,et_aff_password,et_telephone;

    private String account,password,affPassword,telephone;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //  初始化窗口
        mWindow = getWindow();
        BaseUtils.initWindow(mWindow,this);

        initView();
    }

    private void initView() {
        tv_login = findViewById(R.id.tv_login);
        btn_register = findViewById(R.id.btn_register);
        btn_register_back = findViewById(R.id.btn_register_back);
        et_register_username = findViewById(R.id.et_register_username);
        et_register_password = findViewById(R.id.et_register_password);
        et_aff_password = findViewById(R.id.et_aff_password);
        et_telephone = findViewById(R.id.et_telephone);

        tv_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        btn_register_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.tv_login:
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_register:
                getData();
                register();
                break;
            case R.id.btn_register_back:
                finish();
                break;
        }
    }

    /**
     * 获得注册需要的数据
     */
    private void getData() {
        account = et_register_username.getText().toString().trim();
        password = et_register_password.getText().toString().trim();
        affPassword = et_aff_password.getText().toString().trim();
        telephone = et_telephone.getText().toString().trim();

        if(account.isEmpty()){
            BaseUtils.showEmptyDialog(this,"用户账号",et_register_username);
            return;
        }else if(password.isEmpty()){
            BaseUtils.showEmptyDialog(this,"用户密码",et_register_password);
            return;
        }else if(affPassword.isEmpty()){
            BaseUtils.showEmptyDialog(this,"确认密码",et_aff_password);
            return;
        }else if(telephone.isEmpty()){
            BaseUtils.showEmptyDialog(this,"预留手机号",et_telephone);
            return;
        }

        //  判断是否为正确的用户账号格式
        if(!BaseUtils.isTelephone(account) && !BaseUtils.isEmail(account)){
            BaseUtils.showAlertDialog(this,SweetAlertDialog.WARNING_TYPE,
                    "用户账号格式输入错误哦~","  ","是吗");
            et_register_username.requestFocus();
            return;
        }

        //  判断两次账号密码是否一致
        if(!password.equals(affPassword)){
            BaseUtils.showAlertDialog(this,SweetAlertDialog.WARNING_TYPE,
                    "两次输入的密码不一致哦~","  ","是吗");
            et_aff_password.requestFocus();
            return;
        }

        //  判断是否为正确的预留手机号格式
        if(!BaseUtils.isTelephone(telephone)){
            BaseUtils.showAlertDialog(this,SweetAlertDialog.WARNING_TYPE,
                    "预留手机号格式输入错误哦~","  ","是吗");
            et_telephone.requestFocus();
        }
    }

    /**
     * 连接数据库发送注册请求
     */
    private void register() {
        UserBean user = new UserBean();
        user.setAccount(account);
        user.setPassword(password);
        user.setTelephone(telephone);
        if(NetUtils.isNetworkAvailable(this)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String responseData = HttpUtils.GetOkHttpResponseData(URL+account
                            +"&password="+password
                            +"&telephone="+telephone);
                    Log.d("phpdata", responseData+"");
                    Log.d("php", URL+account +"&password="+password +"&telephone="+telephone);
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
     * 后端php代码会返回三个值，1表示注册成功，2表示用户名重复，0表示注册失败
     * @param responseData
     */
    private void parsePhpData(String responseData){
        if(responseData.equals("1")){
            BaseUtils.showAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE,
                    "恭喜注册成功噢~","  ","嘻嘻");
            et_register_username.setText("");
            et_register_password.setText("");
            et_aff_password.setText("");
            et_telephone.setText("");
        }else if(responseData.equals("2")){
            BaseUtils.showAlertDialog(this,SweetAlertDialog.WARNING_TYPE,
                    "账号已被注册了噢~","  ","好吧");
            et_register_username.requestFocus();
        }else if(responseData.equals("0")){
            BaseUtils.showAlertDialog(this,SweetAlertDialog.WARNING_TYPE,
                    "注册失败~","  ","啊偶");
        }
    }
}
