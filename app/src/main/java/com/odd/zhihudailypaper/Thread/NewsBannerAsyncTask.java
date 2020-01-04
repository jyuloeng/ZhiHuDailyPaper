package com.odd.zhihudailypaper.Thread;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.odd.zhihudailypaper.Activity.MainActivity;
import com.odd.zhihudailypaper.Activity.NewInfoActivity;
import com.odd.zhihudailypaper.Bean.NewsListBean;
import com.odd.zhihudailypaper.R;
import com.odd.zhihudailypaper.Utils.HttpUtils;
import com.odd.zhihudailypaper.Utils.JsonUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

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

public class NewsBannerAsyncTask extends AsyncTask<String, Void, List<NewsListBean>> implements OnBannerListener {

    private Context mContext;
    private List<String> list_title;
    private List<String> list_banner_url;
    private List<NewsListBean> mList;
    private Banner mBanner;

    public NewsBannerAsyncTask(Context mContext, Banner mBanner){
        this.mContext = mContext;
        this.mBanner = mBanner;
    }

    @Override
    protected List<NewsListBean> doInBackground(String... strings) {
        return getJsonToList(strings[0]);
    }

    @Override
    protected void onPostExecute(List<NewsListBean> newsListBeans) {
        super.onPostExecute(newsListBeans);
        list_title = new ArrayList<>();
        list_banner_url = new ArrayList<>();

        for(int i = 0;i<newsListBeans.size();i++){
            list_title.add(newsListBeans.get(i).getTitle());
        }
        for(int i = 0;i<newsListBeans.size();i++){
            list_banner_url.add(newsListBeans.get(i).getImgId());
        }

        initBanner();
    }

    /**
     * 初始化轮播图
     */
    private void initBanner() {
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        mBanner.setImageLoader(new MyLoader());
        mBanner.setBannerAnimation(Transformer.Default);
        mBanner.setBannerTitles(list_title);
        mBanner.setDelayTime(4000);
        mBanner.isAutoPlay(true);
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        mBanner.setImages(list_banner_url);
        mBanner.setOnBannerListener(this);
        mBanner.start();
    }

    /**
     * 根据json数据返回一个list
     * @param url json的url地址
     * @return
     */
    private List<NewsListBean> getJsonToList(String url) {
        mList = new ArrayList<>();

        String responseData = HttpUtils.GetOkHttpResponseData(url);

        JsonUtils.ParseNewsListBeanJsonToList(responseData,mList,"top_stories");
        return mList;
    }

    /**
     * 轮播图监听
     * @param position
     */
    @Override
    public void OnBannerClick(int position) {

        NewsListBean newsListBean = mList.get(position);
        Intent intent = new Intent(mContext, NewInfoActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("newsBean",newsListBean);
        intent.putExtras(bundle);

        mContext.getApplicationContext().startActivity(intent);
    }

    /**
     * 网络加载图片
     * 使用了Glide图片加载框架
     */
    private class MyLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context.getApplicationContext())
                    .load((String) path)
                    .into(imageView);
        }
    }
}
