package com.odd.zhihudailypaper.Adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.odd.zhihudailypaper.Bean.CommentsListBean;
import com.odd.zhihudailypaper.R;
import com.odd.zhihudailypaper.Utils.ImageLoader;
import com.odd.zhihudailypaper.Utils.JsonUtils;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsListAdapter extends BaseAdapter implements View.OnClickListener {

    private ImageLoader mImageLoader;
    private List<CommentsListBean> mList;
    private Context mContext;
    private LayoutInflater mInflater;

    public CommentsListAdapter(Context mContext,List<CommentsListBean> mList){
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.layout_commentst,parent,false);

            viewHolder.tv_author = convertView.findViewById(R.id.tv_author);
            viewHolder.tv_content = convertView.findViewById(R.id.tv_content);
            viewHolder.tv_reply = convertView.findViewById(R.id.tv_reply);
            viewHolder.tv_time = convertView.findViewById(R.id.tv_time);
            viewHolder.tv_likes = convertView.findViewById(R.id.tv_likes);

            viewHolder.iv_author = convertView.findViewById(R.id.iv_avatar);
            viewHolder.btn_copy = convertView.findViewById(R.id.btn_copy);
            viewHolder.btn_likes = convertView.findViewById(R.id.btn_likes);
            viewHolder.btn_comment = convertView.findViewById(R.id.btn_comment);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.tv_author.setText(mList.get(position).getAuthor());
        viewHolder.tv_content.setText(mList.get(position).getContent());

        if(mList.get(position).getApply_to() != null){
            viewHolder.tv_reply.setText(mList.get(position).getApply_to());
            viewHolder.tv_reply.setVisibility(View.VISIBLE);
        }else {
            viewHolder.tv_reply.setVisibility(View.GONE);
        }

        viewHolder.tv_time.setText(JsonUtils.StampToTime(mList.get(position).getTime()));
        viewHolder.tv_likes.setText(mList.get(position).getLikes());

        //  设置评论头像
        viewHolder.iv_author.setImageResource(R.mipmap.ic_launcher);

        CircleImageView imageView = viewHolder.iv_author;
        String authorUrl = JsonUtils.JsonTransform(mList.get(position).getAvatarId());

        Glide.with(mContext.getApplicationContext())
                .load(authorUrl)
                .into(imageView);

        // copy按钮事件
        viewHolder.btn_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CopyComment(v,position);
            }
        });

        viewHolder.btn_likes.setOnClickListener(this);
        viewHolder.btn_comment.setOnClickListener(this);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_likes:
                new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("sorry，您没点赞权限~")
                        .setContentText("我也想给他点赞啊我也没办法嘛")
                        .setConfirmText("好吧")
                        .show();
                break;
            case R.id.btn_comment:
                new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("sorry，您没评论权限~")
                        .setContentText("点赞都不行你还想评论的咩")
                        .setConfirmText("好吧")
                        .show();
                break;
        }
    }

    /**
     * 复制评论到系统剪切板
     * @param v
     */
    private void CopyComment(View v, final int i) {
        new XPopup.Builder(mContext)
                .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .asAttachList(new String[]{"复制评论"},
                        null,
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                //  复制到系统剪切板中
                                ClipboardManager cm =  (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                String comment = mList.get(i).getContent();
                                ClipData mData = ClipData.newPlainText("Label",comment);
                                cm.setPrimaryClip(mData);

                                Toast.makeText(mContext,"内容已复制成功",Toast.LENGTH_SHORT).show();
                            }
                        })
                .show();
    }

    class ViewHolder{
        TextView tv_author,tv_content,tv_reply,tv_time,tv_likes;
        CircleImageView iv_author;
        Button btn_copy,btn_likes,btn_comment;
    }
}
