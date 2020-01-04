package com.odd.zhihudailypaper.Controls;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.odd.zhihudailypaper.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavBar extends FrameLayout {

    private CircleImageView mImageView;
    private TextView mTextView;

    public NavBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_nav_bar,this);

        initView();
    }

    private void initView() {
        mImageView = findViewById(R.id.iv_user_avatar);
        mTextView = findViewById(R.id.tv_user_name);
    }

    public void setImageAndText(Bitmap userAvator, String userName){
        mTextView.setText(userName);
        mImageView.setImageBitmap(userAvator);
    }

    public void setText(String userName){
        mTextView.setText(userName);
    }

}
