package com.common.changeskin;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import androidx.core.content.res.ResourcesCompat;

import com.common.changeskin.utils.L;

public class ResourceManager {
    private static final String DEF_TYPE_DRAWABLE = "drawable";
    private static final String DEF_TYPE_COLOR = "color";
    private Resources mResources;
    private String mPluginPackageName;
    private String mSuffix;


    public ResourceManager(Resources res, String pluginPackageName, String suffix) {
        mResources = res;
        mPluginPackageName = pluginPackageName;
        mSuffix = suffix;
    }

    public Drawable getDrawableByName(String name) {
        try {
            name = appendSuffix(name);
            L.e("name = " + name + " , " + mPluginPackageName);
            return ResourcesCompat.getDrawable(mResources,
                    mResources.getIdentifier(name, DEF_TYPE_DRAWABLE, mPluginPackageName), null);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getColor(String name) throws Resources.NotFoundException {
        name = appendSuffix(name);
        L.e("name = " + name);
        return ResourcesCompat.getColor(mResources,
                mResources.getIdentifier(name, DEF_TYPE_COLOR, mPluginPackageName), null);
    }

    public ColorStateList getColorStateList(String name) {
        try {
            name = appendSuffix(name);
            L.e("name = " + name);
            return ResourcesCompat.getColorStateList(mResources,
                    mResources.getIdentifier(name, DEF_TYPE_COLOR, mPluginPackageName), null);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String appendSuffix(String name) {
        if (!TextUtils.isEmpty(mSuffix))
            return name + "_" + mSuffix;
        return name;
    }
}
