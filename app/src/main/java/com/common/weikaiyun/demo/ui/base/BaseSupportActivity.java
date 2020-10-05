package com.common.weikaiyun.demo.ui.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.common.changeskin.SkinManager;
import com.weikaiyun.fragmentation.SupportActivity;

public abstract class BaseSupportActivity extends SupportActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewID());
        SkinManager.INSTANCE.register(this);
    }

    abstract public int getContentViewID();

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SkinManager.INSTANCE.apply(this);
        initView(savedInstanceState);
        initData(savedInstanceState);
    }

    public void initView(@Nullable Bundle savedInstanceState) {

    }

    public void initData(@Nullable Bundle savedInstanceState) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.INSTANCE.unregister(this);
    }
}
