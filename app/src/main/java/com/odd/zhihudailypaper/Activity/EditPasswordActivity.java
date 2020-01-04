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
import com.odd.zhihudailypaper.Thread.CommentsAsyncTask;
import com.odd.zhihudailypaper.Utils.BaseUtils;
import com.odd.zhihudailypaper.Utils.HttpUtils;
import com.odd.zhihudailypaper.Utils.NetUtils;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private static String URL_EDIT_PASSWORD = "http://test.xkspbz.com/odd.zhihudaily/editPassword.php?account=";

    private Window mWindow;
    private EditText et_old_password,et_ed_new_password,et_ed_aff_new_password;
    private Button btn_ed_submit,btn_ed_password_back;

    private String old_password,new_password,aff_password;

    private boolean isLogin = false;

    private UserBean userBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        //  拿用户信息
        userBean = BaseUtils.getUserData(this);
        isLogin = BaseUtils.isLogin(userBean);

        mWindow = getWindow();
        BaseUtils.initWindow(mWindow,this);
        mWindow.setStatusBarColor(0xFFffffff);  //  设置状态栏颜色

        initView();
    }

    private void initView() {
        et_old_password = findViewById(R.id.et_old_password);
        et_ed_new_password = findViewById(R.id.et_ed_new_password);
        et_ed_aff_new_password = findViewById(R.id.et_ed_aff_new_password);
        btn_ed_submit = findViewById(R.id.btn_ed_submit);
        btn_ed_password_back = findViewById(R.id.btn_ed_password_back);

        btn_ed_submit.setOnClickListener(this);
        btn_ed_password_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_ed_submit:
                getData();
                submit();
                break;
            case R.id.btn_ed_password_back:
                finish();
                break;
        }
    }

    /**
     * 发送提交请求
     */
    private void submit() {

        URL_EDIT_PASSWORD += userBean.getAccount()+
                "&oldpassword="+old_password+
                "&newpassword="+new_password;
        //  开线程加载数据
        if(NetUtils.isNetworkAvailable(this)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String responseData =  HttpUtils.GetOkHttpResponseData(URL_EDIT_PASSWORD);
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
     * 处理Php服务器端返回的数据,返回1代表修改成功，返回0代表修改失败
     * @param responseData
     */
    private void parsePhpData(String responseData) {
        if(responseData.equals("1")){
            BaseUtils.showAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE,
                    "修改成功~","  ","嘻嘻");

            //  修改本地缓存
            BaseUtils.editUserData(this,UserBean.PASSWORD,new_password);

            et_old_password.setText("");
            et_ed_new_password.setText("");
            et_ed_aff_new_password.setText("");
        }else if(responseData.equals("0")){
            BaseUtils.showAlertDialog(this,SweetAlertDialog.WARNING_TYPE,
                    "旧密码错误噢~","  ","好吧");
            et_old_password.requestFocus();
        }
    }

    /**
     * 获得数据
     */
    private void getData() {
        old_password = et_old_password.getText().toString().trim();
        new_password = et_ed_new_password.getText().toString().trim();
        aff_password = et_ed_aff_new_password.getText().toString().trim();

        //  判空操作
        if(old_password.isEmpty()){
            BaseUtils.showEmptyDialog(this,"旧用户密码",et_old_password);
        }else if(new_password.isEmpty()){
            BaseUtils.showEmptyDialog(this,"新用户密码",et_ed_new_password);
        }else if(aff_password.isEmpty()){
            BaseUtils.showEmptyDialog(this,"确认新用户密码",et_ed_aff_new_password);
        }

        //  判断两次密码是否输入一致
        if(!new_password.equals(aff_password)){
            BaseUtils.showAlertDialog(this, SweetAlertDialog.WARNING_TYPE,
                    "两次输入的密码不一致哦~", "  ", "是吗");
            et_ed_aff_new_password.requestFocus();
        }

        //  判断是否与旧的密码一致
        if(!old_password.equals(userBean.getPassword())){
            BaseUtils.showAlertDialog(this, SweetAlertDialog.WARNING_TYPE,
                    "旧用户密码输入错误哦~", "  ", "是吗");
            et_old_password.requestFocus();
        }
    }

}
