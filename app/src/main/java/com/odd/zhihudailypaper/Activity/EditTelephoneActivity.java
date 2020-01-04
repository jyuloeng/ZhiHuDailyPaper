package com.odd.zhihudailypaper.Activity;

import androidx.appcompat.app.AppCompatActivity;

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

public class EditTelephoneActivity extends AppCompatActivity implements View.OnClickListener {

    private static String URL_EDIT_TELEPHONE = "http://test.xkspbz.com/odd.zhihudaily/editTelephone.php?account=";

    private Window mWindow;

    private EditText et_old_telephone,et_ed_new_telephone,et_ed_aff_new_telephone;
    private Button btn_ed_telephone_submit,btn_ed_telephone_back;

    private String old_telephone,new_telephone,aff_telephone;

    private boolean isLogin = false;

    private UserBean userBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_telephone);

        //  拿用户信息
        userBean = BaseUtils.getUserData(this);
        isLogin = BaseUtils.isLogin(userBean);

        mWindow = getWindow();
        BaseUtils.initWindow(mWindow,this);
        mWindow.setStatusBarColor(0xFFffffff);  //  设置状态栏颜色

        initView();
    }

    private void initView() {
        et_old_telephone = findViewById(R.id.et_old_telephone);
        et_ed_new_telephone = findViewById(R.id.et_ed_new_telephone);
        et_ed_aff_new_telephone = findViewById(R.id.et_ed_aff_new_telephone);
        btn_ed_telephone_submit = findViewById(R.id.btn_ed_telephone_submit);
        btn_ed_telephone_back = findViewById(R.id.btn_ed_telephone_back);

        btn_ed_telephone_submit.setOnClickListener(this);
        btn_ed_telephone_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_ed_telephone_submit:
                getData();
                submit();
                break;
            case R.id.btn_ed_telephone_back:
                finish();
                break;
        }
    }

    /**
     * 获得数据
     */
    private void getData() {
        old_telephone = et_old_telephone.getText().toString().trim();
        new_telephone = et_ed_new_telephone.getText().toString().trim();
        aff_telephone = et_ed_aff_new_telephone.getText().toString().trim();

        //  判空操作
        if(old_telephone.isEmpty()){
            BaseUtils.showEmptyDialog(this,"旧手机号码",et_old_telephone);
            return;
        }else if(new_telephone.isEmpty()){
            BaseUtils.showEmptyDialog(this,"新手机号码",et_ed_new_telephone);
            return;
        }else if(aff_telephone.isEmpty()){
            BaseUtils.showEmptyDialog(this,"确认新手机号码",et_ed_aff_new_telephone);
            return;
        }

        //  判断是否为正确的预留手机号格式
        if(!BaseUtils.isTelephone(old_telephone)){
            BaseUtils.showAlertDialog(this, SweetAlertDialog.WARNING_TYPE,
                    "旧手机号格式输入错误哦~", "  ", "是吗");
            et_old_telephone.requestFocus();
        }else if(!BaseUtils.isTelephone(new_telephone)){
            BaseUtils.showAlertDialog(this, SweetAlertDialog.WARNING_TYPE,
                    "新手机号格式输入错误哦~", "  ", "是吗");
            et_ed_new_telephone.requestFocus();
        }

        //  判断是否一致
        if(!aff_telephone.equals(new_telephone)){
            BaseUtils.showAlertDialog(this, SweetAlertDialog.WARNING_TYPE,
                    "两次输入的手机号不一致哦~", "  ", "是吗");
            et_ed_aff_new_telephone.requestFocus();
        }

        //  判断是否与旧的手机号一致
        if(!old_telephone.equals(userBean.getTelephone())){
            BaseUtils.showAlertDialog(this, SweetAlertDialog.WARNING_TYPE,
                    "旧手机号码输入入错误哦~", "  ", "是吗");
            et_old_telephone.requestFocus();
        }
    }

    /**
     * 发送提交请求
     */
    private void submit() {
        URL_EDIT_TELEPHONE += userBean.getAccount()+
                "&oldtelephone="+old_telephone+
                "&newtelephone="+new_telephone;
        if(NetUtils.isNetworkAvailable(this)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String responseData =  HttpUtils.GetOkHttpResponseData(URL_EDIT_TELEPHONE);
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
            BaseUtils.editUserData(this,UserBean.TELEPHONE,new_telephone);

            et_old_telephone.setText("");
            et_ed_new_telephone.setText("");
            et_ed_aff_new_telephone.setText("");
        }else{
            BaseUtils.showAlertDialog(this,SweetAlertDialog.WARNING_TYPE,
                    "修改失败~","  ","好吧");
            et_old_telephone.requestFocus();
        }
    }
}
