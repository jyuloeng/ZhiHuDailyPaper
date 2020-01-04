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

import java.util.List;

public class CollectionsAsyncTask extends AsyncTask<String,Void, List<NewsListBean>> {

    private Context mContext;
    private List<NewsListBean> mList;
    private ListView mListView;
    private NewsListAdapter mAdapter;

    public CollectionsAsyncTask(Context mContext, ListView mListView,List<NewsListBean> mlist){
        this.mContext = mContext;
        this.mList = mlist;
        this.mListView = mListView;
    }

    @Override
    protected List<NewsListBean> doInBackground(String... strings) {
        return getJsonToList(strings[0]);
    }

    @Override
    protected void onPostExecute(final List<NewsListBean> newsListBeans) {
        super.onPostExecute(newsListBeans);
        mAdapter = new NewsListAdapter(mContext,newsListBeans);

        mListView.setAdapter(mAdapter);

        //  listview的item监听事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NewsListBean newsListBean = newsListBeans.get(i);

                Intent intent = new Intent(mContext, NewInfoActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("newsBean",newsListBean);
                intent.putExtras(bundle);

                mContext.getApplicationContext().startActivity(intent);
            }
        });
    }

    private List<NewsListBean> getJsonToList(String url) {
        String responseData =  HttpUtils.GetOkHttpResponseData(url);
        JsonUtils.ParseNewsListBeanPhpDataJsonToList(responseData,mList);
        return mList;
    }
}
