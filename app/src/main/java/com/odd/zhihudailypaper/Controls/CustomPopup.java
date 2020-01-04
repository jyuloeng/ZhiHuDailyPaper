package com.odd.zhihudailypaper.Controls;

import android.content.Context;

import com.lxj.xpopup.core.CenterPopupView;
import com.odd.zhihudailypaper.R;

import androidx.annotation.NonNull;

public class CustomPopup extends CenterPopupView {
    //注意：自定义弹窗本质是一个自定义View，但是只需重写一个参数的构造，其他的不要重写，所有的自定义弹窗都是这样。
    public CustomPopup(@NonNull Context context) {
        super(context);
    }
    // 返回自定义弹窗的布局
    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_about;
    }

}
