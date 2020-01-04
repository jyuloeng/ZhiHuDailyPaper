package com.odd.zhihudailypaper.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

public class EditUserNameActivity extends AppCompatActivity implements View.OnClickListener {

    private static String URL_EDIT_NAME = "http://test.xkspbz.com/odd.zhihudaily/editUsername.php?account=";

    private Window mWindow;

    private Button btn_ed_user_name_back,btn_ed_user_name_submit;
    private EditText et_ed_user_name;
    private String user_name;
    private boolean isLogin = false;

    private UserBean userBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_name);

        //  拿用户信息
        userBean = BaseUtils.getUserData(this);
        isLogin = BaseUtils.isLogin(userBean);

        mWindow = getWindow();
        BaseUtils.initWindow(mWindow,this);
        mWindow.setStatusBarColor(0xFFffffff);  //  设置状态栏颜色

        initView();
    }

    private void initView() {
        btn_ed_user_name_back = findViewById(R.id.btn_ed_user_name_back);
        btn_ed_user_name_submit = findViewById(R.id.btn_ed_user_name_submit);
        et_ed_user_name = findViewById(R.id.et_ed_user_name);

        btn_ed_user_name_back.setOnClickListener(this);
        btn_ed_user_name_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_ed_user_name_back:
                finish();
                break;
            case R.id.btn_ed_user_name_submit:
                getData();
                submit();
                break;
        }
    }

    /**
     * 获得数据
     */
    private void getData() {
        user_name = et_ed_user_name.getText().toString().trim();
        if(user_name.isEmpty()){
            BaseUtils.showEmptyDialog(this,"新用户名",et_ed_user_name);
            return;
        }
    }

    /**
     * 发送提交请求
     */
    private void submit() {
        URL_EDIT_NAME += userBean.getAccount()+
                "&name="+user_name;
        if(NetUtils.isNetworkAvailable(this)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String responseData =  HttpUtils.GetOkHttpResponseData(URL_EDIT_NAME);
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
            BaseUtils.showAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE,
                    "修改成功~","  ","嘻嘻");

            //  修改本地缓存
            BaseUtils.editUserData(this,UserBean.NAME,user_name);
            et_ed_user_name.setText("");
        }else{
            BaseUtils.showAlertDialog(this,SweetAlertDialog.WARNING_TYPE,
                    "修改失败~","  ","好吧");
            et_ed_user_name.requestFocus();
        }
    }
}
