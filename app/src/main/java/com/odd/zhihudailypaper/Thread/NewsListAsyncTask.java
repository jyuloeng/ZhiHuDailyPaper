package com.odd.zhihudailypaper.Thread;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.odd.zhihudailypaper.Activity.NewInfoActivity;
import com.odd.zhihudailypaper.Adapter.NewsListAdapter;
import com.odd.zhihudailypaper.Bean.NewsListBean;
import com.odd.zhihudailypaper.Utils.HttpUtils;
import com.odd.zhihudailypaper.Utils.JsonUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsListAsyncTask extends AsyncTask<String,Void, List<NewsListBean>> {

    private Context mContext;
    private ListView mListView;
    private NewsListAdapter mAdapter;
    private List<NewsListBean> mList;

    public NewsListAsyncTask(Context mContext,ListView mListView,List<NewsListBean> mList){
        this.mContext = mContext;
        this.mListView = mListView;
        this.mList = mList;
    }

    @Override
    protected List<NewsListBean> doInBackground(String... strings) {
        return getJsonToList(strings[0]);
    }

    @Override
    protected void onPostExecute(final List<NewsListBean> mList) {
        super.onPostExecute(mList);
        mAdapter = new NewsListAdapter(mContext,mList);

        mAdapter.notifyDataSetChanged();

        mListView.setAdapter(mAdapter);

        mListView.setStackFromBottom(true); //  从底部排列，不会回滚到顶部

        //  listview的item监听事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NewsListBean newsListBean = mList.get(i-1);

                Intent intent = new Intent(mContext, NewInfoActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("newsBean",newsListBean);
                intent.putExtras(bundle);

                mContext.getApplicationContext().startActivity(intent);
            }
        });
    }

    /**
     * 根据json数据返回一个list
     * @param url json的url地址
     * @return
     */
    private List<NewsListBean> getJsonToList(String url) {

        String responseData = HttpUtils.GetOkHttpResponseData(url);

        JsonUtils.ParseNewsListBeanJsonToList(responseData,mList,"stories");
        return mList;

    }

}
