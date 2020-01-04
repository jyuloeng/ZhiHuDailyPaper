package com.odd.zhihudailypaper.Controls;

import android.content.Context;

import com.lxj.xpopup.core.CenterPopupView;
import com.odd.zhihudailypaper.R;

import androidx.annotation.NonNull;

public class ZhuanlanPopup extends CenterPopupView {

    public ZhuanlanPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_zhuanlan;
    }
}
