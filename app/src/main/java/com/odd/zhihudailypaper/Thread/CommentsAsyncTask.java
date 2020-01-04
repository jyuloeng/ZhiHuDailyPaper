package com.odd.zhihudailypaper.Thread;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;

import com.odd.zhihudailypaper.Adapter.CommentsListAdapter;
import com.odd.zhihudailypaper.Bean.CommentsListBean;
import com.odd.zhihudailypaper.Utils.HttpUtils;
import com.odd.zhihudailypaper.Utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class CommentsAsyncTask extends AsyncTask<String, Void, List<CommentsListBean>> {

    private ListView mListView;
    private List<CommentsListBean> mList;
    private CommentsListAdapter mAdapter;
    private Context mContext;

    public CommentsAsyncTask(Context mContext,ListView mListView,List<CommentsListBean> mList){
        this.mContext = mContext;
        this.mListView = mListView;
        this.mList = mList;
    }

    @Override
    protected List<CommentsListBean> doInBackground(String... strings) {
        return getJsonToList(strings[0]);
    }



    @Override
    protected void onPostExecute(List<CommentsListBean> commentsListBeans) {
        super.onPostExecute(commentsListBeans);
        mAdapter = new CommentsListAdapter(mContext,mList);
        mListView.setAdapter(mAdapter);
    }

    private List<CommentsListBean> getJsonToList(String url) {
        String responseData = HttpUtils.GetOkHttpResponseData(url);

        JsonUtils.ParseCommentsListBeanJsonToList(responseData,mList);
        return mList;
    }
}
