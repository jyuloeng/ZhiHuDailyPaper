package com.odd.zhihudailypaper.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guoxiaoxing.phoenix.core.PhoenixOption;
import com.guoxiaoxing.phoenix.core.listener.ImageLoader;
import com.guoxiaoxing.phoenix.core.model.MediaEntity;
import com.guoxiaoxing.phoenix.core.model.MimeType;
import com.guoxiaoxing.phoenix.picker.Phoenix;
import com.lxj.xpopup.XPopup;
import com.odd.zhihudailypaper.Bean.UserBean;
import com.odd.zhihudailypaper.Controls.CustomPopup;
import com.odd.zhihudailypaper.R;
import com.odd.zhihudailypaper.Utils.BaseUtils;
import com.odd.zhihudailypaper.Utils.HttpUtils;
import com.odd.zhihudailypaper.Utils.NetUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private static String URL_UPLOAD = "http://test.xkspbz.com/image/up.php";
    private static String EDIT_AVATAR = "http://test.xkspbz.com/odd.zhihudaily/editAvator.php?account=";

    private Window mWindow;
    private Button btn_settings_back,btn_unlogin;
    private ImageView iv_st_avatar;
    private TextView tv_st_user_name;
    private RelativeLayout layout_username,layout_password,layout_telephone,layout_myself;

    private boolean isLogin = false;

    private UserBean userBean;

    private String imgUrl = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mWindow = getWindow();
        BaseUtils.initWindow(mWindow,this);
        mWindow.setStatusBarColor(0xFFffffff);  //  设置状态栏颜色

        //  拿用户信息
        userBean = BaseUtils.getUserData(this);
        isLogin = BaseUtils.isLogin(userBean);

        initView();

        Phoenix.config()
                .imageLoader(new ImageLoader() {
                    @Override
                    public void loadImage(Context mContext, ImageView imageView
                            , String imagePath, int type) {
                        Glide.with(mContext)
                                .load(imagePath)
                                .into(imageView);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_st_user_name.setText(userBean.getName());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        tv_st_user_name.setText(userBean.getName());
        //  TODO 修改完名字回到界面不会立刻刷新名字BUG
    }

    private void initView() {
        btn_settings_back = findViewById(R.id.btn_settings_back);
        btn_unlogin = findViewById(R.id.btn_unlogin);
        iv_st_avatar = findViewById(R.id.iv_st_avatar);
        tv_st_user_name = findViewById(R.id.tv_st_user_name);
        layout_username = findViewById(R.id.layout_username);
        layout_password = findViewById(R.id.layout_password);
        layout_telephone = findViewById(R.id.layout_telephone);
        layout_myself = findViewById(R.id.layout_myself);

        btn_settings_back.setOnClickListener(this);
        btn_unlogin.setOnClickListener(this);
        iv_st_avatar.setOnClickListener(this);
        layout_username.setOnClickListener(this);
        layout_password.setOnClickListener(this);
        layout_telephone.setOnClickListener(this);
        layout_myself.setOnClickListener(this);

        if(isLogin == true){
            tv_st_user_name.setText(userBean.getName());
            Glide.with(this)
                    .load(userBean.getAvatar())
                    .into(iv_st_avatar);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id){
            case R.id.btn_settings_back:
                finish();
                break;
            case R.id.btn_unlogin:
                quit();
                break;
            case R.id.iv_st_avatar:
                editAvatar();
                break;
            case R.id.layout_username:
                editUserName();
                break;
            case R.id.layout_password:
                editPassword();
                break;
            case R.id.layout_telephone:
                editTelephone();
                break;
            case R.id.layout_myself:
                aboutMe();
                break;
        }
    }

    /**
     * 关于我的弹窗
     */
    private void aboutMe() {
        new XPopup.Builder(this)
                .asCustom(new CustomPopup(this))
                .show();
    }

    /**
     * 修改预留手机号
     */
    private void editTelephone() {
        Intent intent = new Intent(SettingsActivity.this,EditTelephoneActivity.class);
        startActivity(intent);
    }

    /**
     * 修改密码
     */
    private void editPassword() {
        Intent intent = new Intent(SettingsActivity.this,EditPasswordActivity.class);
        startActivity(intent);
    }

    /**
     * 更改用户名
     */
    private void editUserName() {
        Intent intent = new Intent(SettingsActivity.this,EditUserNameActivity.class);
        startActivity(intent);
    }

    /**
     * 更改头像
     */
    private void editAvatar() {
        List<MediaEntity> mList = new ArrayList<>();

        Phoenix.with().theme(PhoenixOption.THEME_BLUE)// 主题
                .fileType(MimeType.ofAll())
                .maxPickNumber(1)// 最大选择数量
                .minPickNumber(1)// 最小选择数量
                .spanCount(4)// 每行显示个数
                .enablePreview(true)
                .thumbnailHeight(160)// 选择界面图片高度
                .thumbnailWidth(160)// 选择界面图片宽度
                .enableClickSound(false)// 是否开启点击声音
                .pickedMediaList(mList)
                .start(SettingsActivity.this, PhoenixOption.TYPE_PICK_MEDIA,0);

    }

    /**
     * 选择图片后的回调
     * @param requestCode 回调码
     * @param resultCode    是否OK
     * @param data  图片数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            //返回的数据
            final List<MediaEntity> result = Phoenix.result(data);
            Log.d("photoccc", result.get(0).getLocalPath());

            Bitmap bitmap = getLoacalBitmap(result.get(0).getLocalPath());
            iv_st_avatar.setImageBitmap(bitmap);
            final Activity activity = this;
            //  发送给服务器更改头像

            if(NetUtils.isNetworkAvailable(this)){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        imgUrl =  BaseUtils.uploadFile(URL_UPLOAD,result.get(0).getLocalPath());
                        //Log.d("imgUrl", imgUrl);
                        BaseUtils.editUserData(activity,UserBean.AVATAR,imgUrl);

                        //  提交到数据库修改资料
                        EDIT_AVATAR += userBean.getAccount()+ "&avatar=" + imgUrl;
                        Log.d("imgUrl", EDIT_AVATAR);
                        final String responseData =  HttpUtils.GetOkHttpResponseData(EDIT_AVATAR);
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
    }

    /**
     *
     * @param responseData
     */
    private void parsePhpData(String responseData) {
        if(responseData.equals("1")){
            //BaseUtils.showAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE,
                   // "修改头像成功~","  ","嘻嘻");
        }else if(responseData.equals("0")){
            BaseUtils.showAlertDialog(this,SweetAlertDialog.WARNING_TYPE,
                    "修改头像失败~","  ","好吧");
            return;
        }
    }

    /**
     * 退出登录
     */
    private void quit() {
        isLogin = false;
        BaseUtils.quit(this);
        Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
        startActivity(intent);
    }

    /**
     * 加载本地图片
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
