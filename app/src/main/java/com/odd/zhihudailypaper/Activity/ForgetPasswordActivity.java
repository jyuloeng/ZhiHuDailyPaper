package com.odd.zhihudailypaper.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.odd.zhihudailypaper.Bean.UserBean;
import com.odd.zhihudailypaper.R;
import com.odd.zhihudailypaper.Utils.BaseUtils;
import com.odd.zhihudailypaper.Utils.HttpUtils;
import com.odd.zhihudailypaper.Utils.NetUtils;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private static String URL_FG_PASSWORD = "http://test.xkspbz.com/odd.zhihudaily/forgetPassword.php?account=";

    private Window mWindow;
    private EditText et_fg_username,et_fg_telephone,et_new_password,et_aff_new_password;
    private Button btn_fg_submit,btn_fg_password_back;

    private String account,telephone,new_password,aff_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mWindow = getWindow();
        BaseUtils.initWindow(mWindow,this);
        mWindow.setStatusBarColor(0xFFffffff);  //  设置状态栏颜色

        initView();
    }

    private void initView() {
        et_fg_username = findViewById(R.id.et_fg_username);
        et_new_password = findViewById(R.id.et_new_password);
        et_fg_telephone = findViewById(R.id.et_fg_telephone);
        et_aff_new_password = findViewById(R.id.et_aff_new_password);
        btn_fg_submit = findViewById(R.id.btn_fg_submit);
        btn_fg_password_back = findViewById(R.id.btn_fg_password_back);

        btn_fg_submit.setOnClickListener(this);
        btn_fg_password_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_ed_submit:

                //  TODO 点击事件有问题
                getData();
                submit();
                break;
            case R.id.btn_fg_password_back:
                finish();
                break;
        }
    }

    /**
     * 发送提交请求
     */
    private void submit() {
        URL_FG_PASSWORD += et_fg_username+
                "&newpassword="+et_new_password+
                "&telephone="+et_fg_telephone;

        Log.d("URL_FG_PASSWORD", URL_FG_PASSWORD);
        if(NetUtils.isNetworkAvailable(this)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String responseData =  HttpUtils.GetOkHttpResponseData(URL_FG_PASSWORD);
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
     * 获得数据
     */
    private void getData() {
        account = et_fg_username.getText().toString().trim();
        telephone = et_fg_telephone.getText().toString().trim();
        new_password = et_new_password.getText().toString().trim();
        aff_password = et_aff_new_password.getText().toString().trim();

        //  判空操作
        if(account.isEmpty()){
            BaseUtils.showEmptyDialog(this,"用户账号",et_fg_username);
            return;
        }else if(telephone.isEmpty()){
            BaseUtils.showEmptyDialog(this,"用户预留手机号码",et_fg_telephone);
            return;
        }else if(new_password.isEmpty()){
            BaseUtils.showEmptyDialog(this,"新用户密码",et_new_password);
            return;
        }else if(aff_password.isEmpty()){
            BaseUtils.showEmptyDialog(this,"确认新用户密码",et_aff_new_password);
            return;
        }

        //判断是否为正确的用户账号格式
        if(!BaseUtils.isTelephone(account) && !BaseUtils.isEmail(account)){
            BaseUtils.showAlertDialog(this, SweetAlertDialog.WARNING_TYPE,
                    "用户账号格式输入错误哦~","  ","是吗");
            et_fg_username.requestFocus();
            return;
        }

        //  判断两次账号密码是否一致
        if(!aff_password.equals(new_password)){
            BaseUtils.showAlertDialog(this,SweetAlertDialog.WARNING_TYPE,
                    "两次输入的密码不一致哦~","  ","是吗");
            et_aff_new_password.requestFocus();
            return;
        }

        //  判断是否为正确的预留手机号格式
        if(!BaseUtils.isTelephone(telephone)){
            BaseUtils.showAlertDialog(this,SweetAlertDialog.WARNING_TYPE,
                    "预留手机号格式输入错误哦~","  ","是吗");
            et_fg_telephone.requestFocus();
        }
    }

    /**
     * 处理Php服务器端返回的数据,返回1代表修改成功，返回0代表修改失败
     * @param responseData
     */
    private void parsePhpData(String responseData) {
        if(responseData.equals("1")){
            BaseUtils.showAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE,
                    "修改成功~","  ","嘻嘻");

            et_fg_username.setText("");
            et_fg_telephone.setText("");
            et_new_password.setText("");
            et_aff_new_password.setText("");
        }else{
            BaseUtils.showAlertDialog(this,SweetAlertDialog.WARNING_TYPE,
                    "修改失败~","  ","好吧");
            et_fg_username.requestFocus();
        }
    }
}
