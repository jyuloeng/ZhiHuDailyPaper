package com.odd.zhihudailypaper.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ImageLoader {

    private LruCache<String,Bitmap> mCache;

    public ImageLoader(){
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory/5;

        mCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    /**
     * 将bitmap对象存入cache缓存
     * @param url
     * @param bitmap
     */
    public void addBitmapToCache(String url, Bitmap bitmap) {
        if(getBitmap(url) == null){
            mCache.put(url,bitmap);
        }
    }

    public void ShowImageByAsyncTask(ImageView mImageView,String url){
        Bitmap bitmap = getBitmap(url);
        if(bitmap == null){
            new ImageAsyncTask(mImageView,url).execute(url);
        }else {
            mImageView.setImageBitmap(bitmap);
        }
    }

    /**
     * 根据url从cache中那出bitmap对象
     * @param url
     * @return
     */
    private Bitmap getBitmap(String url) {
        return mCache.get(url);
    }

    class ImageAsyncTask extends AsyncTask<String,Void, Bitmap>{

        private String url;
        private ImageView mImageView;

        public ImageAsyncTask(ImageView mImageView,String url){
            this.mImageView = mImageView;
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap  = getBitmapFromUrl(strings[0]);
            if(bitmap != null){
                addBitmapToCache(strings[0],bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(mImageView.getTag().equals(url)){
                mImageView.setImageBitmap(bitmap);
            }
        }

        /**
         * 根据url地址返回bitmap对象
         * @param url
         * @return
         */
        private Bitmap getBitmapFromUrl(String url) {
            Bitmap bitmap = null;
            try{
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bitmap = BitmapFactory.decodeStream(bis);

                if(conn!=null){
                    conn.disconnect();
                }
                if(is != null){
                    is.close();
                }
                return bitmap;
            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
