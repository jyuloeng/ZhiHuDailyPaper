package com.odd.zhihudailypaper.Controls;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.odd.zhihudailypaper.R;

public class NavButton extends FrameLayout {

    private ImageView mImageView;
    private TextView mTextView;

    public NavButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_nav_btn,this);

        initView();
    }

    private void initView() {
        mImageView = findViewById(R.id.iv_navigation_icon);
        mTextView = findViewById(R.id.tv_navigation_name);
    }

    public void setImageAndText(int imgId,String text){
        mImageView.setImageResource(imgId);
        mTextView.setText(text);
    }
}
