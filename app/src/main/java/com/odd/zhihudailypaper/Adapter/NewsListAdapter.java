package com.odd.zhihudailypaper.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.odd.zhihudailypaper.Bean.NewsListBean;
import com.odd.zhihudailypaper.R;
import com.odd.zhihudailypaper.Utils.ImageLoader;
import com.odd.zhihudailypaper.Utils.JsonUtils;

import java.util.List;

public class NewsListAdapter extends BaseAdapter {

    private ImageLoader mImageLoader;
    private List<NewsListBean> mList;
    private Context mContext;
    private LayoutInflater mInflater;

    public NewsListAdapter(Context mContext,List<NewsListBean> mList){
        this.mContext = mContext;
        this.mList = mList;
        mInflater = LayoutInflater.from(mContext);
        mImageLoader = new ImageLoader();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.layout_items,parent,false);

            viewHolder.tv_title = convertView.findViewById(R.id.tv_title);
            viewHolder.tv_hint = convertView.findViewById(R.id.tv_hint);
            viewHolder.img_photo = convertView.findViewById(R.id.iv_photo);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv_title.setText(mList.get(position).getTitle());
        viewHolder.tv_hint.setText(mList.get(position).getHint());
        viewHolder.img_photo.setImageResource(R.mipmap.ic_launcher);

        // 设置图像
        ImageView mImageView = viewHolder.img_photo;
        String imgUrl = JsonUtils.JsonTransform(mList.get(position).getImgId());

        //  用Glide加载图像
        Glide.with(mContext.getApplicationContext())
                .load(imgUrl)
                .into(mImageView);
        return convertView;
    }

    class ViewHolder{
        public TextView tv_title;
        public TextView tv_hint;
        public ImageView img_photo;
    }
}
