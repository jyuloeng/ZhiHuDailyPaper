package com.odd.zhihudailypaper.Controls;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.odd.zhihudailypaper.R;

public class BottomButton extends FrameLayout {

    private ImageView mImageView;
    private TextView mTextView;

    public BottomButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_bottom_btn,this);

        initView();
    }

    private void initView() {
        mImageView = findViewById(R.id.iv_bottom_bar);
        mTextView = findViewById(R.id.tv_num);
    }

    public void setText(String num){
        mTextView.setText(num);
    }

    public void setImage(int imgId){
        mImageView.setImageResource(imgId);
    }
}
