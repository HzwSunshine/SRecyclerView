package com.hzw.srecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 功能：SRV的简易适配器，可用于普通的RecyclerView
 * Created by 何志伟 on 2017/7/13.
 */
public abstract class BaseSRVAdapter<T> extends RecyclerView.Adapter<SRVHolder> {

    private final int itemLayoutId;
    private final List<T> mList;

    public BaseSRVAdapter(List<T> list, int itemLayoutId) {
        this.mList = list;
        this.itemLayoutId = itemLayoutId;
    }

    @Override public SRVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(itemLayoutId, parent, false);
        SRVHolder holder = SRVHolder.getViewHolder(view);
        onCreateView(parent, holder);
        return holder;
    }

    /**
     * 用于初始化view
     */
    public void onCreateView(ViewGroup parent, SRVHolder holder) {
    }

    @Override public void onBindViewHolder(final SRVHolder holder, int position) {
        onBindView(holder, mList.get(position), position);
    }

    public abstract void onBindView(SRVHolder holder, T data, int i);

    @Override public int getItemCount() {
        return mList.size();
    }

}
