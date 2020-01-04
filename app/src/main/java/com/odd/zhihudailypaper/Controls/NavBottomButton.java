package com.odd.zhihudailypaper.Controls;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.odd.zhihudailypaper.R;

public class NavBottomButton extends FrameLayout {
    
    private ImageView mImageView;
    private TextView mTextView;
    
    public NavBottomButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context,R.layout.view_nav_bottom_btn,this);
        
        initView();
    }

    private void initView() {
        mTextView = findViewById(R.id.tv_nav_bottom_text);
        mImageView = findViewById(R.id.iv_nav_bottom_icon);
    }

    public void setIconAndText(int id,String text){
        mTextView.setText(text);
        mImageView.setImageResource(id);
    }
}
