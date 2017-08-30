package com.hzw.srecyclerview;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：获取用户的全局SRV配置
 * Created by 何志伟 on 2017/7/17.
 */

class GetSRVModule {

    private static final String SRV_CONFIG_VALUE = "SRecyclerViewModule";
    private SRecyclerViewModule config;

    SRecyclerViewModule getConfig(Context context) {
        if (config == null) {
            List<SRecyclerViewModule> configs = parse(context);
            config = configs.size() > 0 ? configs.get(0) : null;
        }
        return config;
    }

    private List<SRecyclerViewModule> parse(Context context) {
        List<SRecyclerViewModule> modules = new ArrayList<>();
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                for (String key : appInfo.metaData.keySet()) {
                    if (SRV_CONFIG_VALUE.equals(appInfo.metaData.get(key))) {
                        modules.add(parseConfig(key));
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return modules;
    }

    private static SRecyclerViewModule parseConfig(String className) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (Exception e) {
            return null;
        }
        Object module;
        try {
            module = clazz.newInstance();
        } catch (Exception e) {
            return null;
        }
        if (!(module instanceof SRecyclerViewModule)) {
            return null;
        }
        return (SRecyclerViewModule) module;
    }


}
