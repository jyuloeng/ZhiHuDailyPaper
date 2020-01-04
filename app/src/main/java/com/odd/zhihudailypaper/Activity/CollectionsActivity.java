package com.odd.zhihudailypaper.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.odd.zhihudailypaper.Bean.NewsListBean;
import com.odd.zhihudailypaper.Bean.UserBean;
import com.odd.zhihudailypaper.R;
import com.odd.zhihudailypaper.Thread.CollectionsAsyncTask;
import com.odd.zhihudailypaper.Utils.BaseUtils;

import java.util.ArrayList;
import java.util.List;

public class CollectionsActivity extends AppCompatActivity {

    private String URL_COLLECTED = "http://test.xkspbz.com/odd.zhihudaily/selectCollected.php/?account=";

    private Window mWindow;
    private Button btn_collections_back;
    private ListView mListView;
    private List<NewsListBean> mList;
    private CollectionsAsyncTask mAsyncTask;

    private boolean isLogin = false;
    private UserBean userBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);

        //  拿用户信息
        userBean = BaseUtils.getUserData(this);
        isLogin = BaseUtils.isLogin(userBean);

        mWindow = getWindow();
        BaseUtils.initWindow(mWindow,this);
        mWindow.setStatusBarColor(0xFFffffff);  //  设置状态栏颜色

        initView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initTask();
    }

    private void initView() {
        btn_collections_back = findViewById(R.id.btn_collections_back);
        btn_collections_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        URL_COLLECTED += userBean.getAccount();
        mListView = findViewById(R.id.lv_collections);

        initTask();
    }

    private void initTask(){
        mList = new ArrayList<>();
        mAsyncTask = new CollectionsAsyncTask(this,mListView,mList);
        mAsyncTask.execute(URL_COLLECTED);
    }
}
