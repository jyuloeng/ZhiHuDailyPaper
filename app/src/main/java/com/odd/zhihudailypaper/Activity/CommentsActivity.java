package com.odd.zhihudailypaper.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ListView;

import com.odd.zhihudailypaper.Bean.CommentsListBean;
import com.odd.zhihudailypaper.R;
import com.odd.zhihudailypaper.Thread.CommentsAsyncTask;
import com.odd.zhihudailypaper.Utils.BaseUtils;
import com.odd.zhihudailypaper.Utils.NetUtils;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    private Window mWindow;
    private Intent mIntent;
    private String new_id;
    private ListView lv_comments;

    private String URL_DATAS = "https://news-at.zhihu.com/api/4/story/";
    private String URL_lONG_COMMENTS = "";
    private String URL_SHORT_COMMENTS = "";
    private String lONG_COMMENTS = "/long-comments";
    private String SHORT_COMMENTS = "/short-comments";

    private CommentsAsyncTask comments_task;

    private List<CommentsListBean> mList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        mWindow = getWindow();
        BaseUtils.initWindow(mWindow,this);

        initView();
        initData();
    }

    /**
     * 初始化要加载的数据
     */
    private void initData() {
        mList = new ArrayList<>();

        mIntent = getIntent();
        new_id = mIntent.getStringExtra("new_id");
        URL_lONG_COMMENTS = URL_DATAS + new_id + lONG_COMMENTS;
        URL_SHORT_COMMENTS = URL_DATAS + new_id + SHORT_COMMENTS;

        //  开线程加载数据
        if(NetUtils.isNetworkAvailable(this)){
            comments_task = new CommentsAsyncTask(this,lv_comments,mList);
            Log.d("TestUrl", URL_lONG_COMMENTS);
            comments_task.execute(URL_lONG_COMMENTS);

            comments_task = new CommentsAsyncTask(this,lv_comments,mList);
            Log.d("TestUrl", URL_SHORT_COMMENTS);
            comments_task.execute(URL_SHORT_COMMENTS);
        }else{
            NetUtils.isNetworkAvailable(this);
        }
    }
    private void initView() {
        lv_comments = findViewById(R.id.lv_comments);
    }
}
