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

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_SECTION = 1;
    private int itemLayoutId, sectionLayoutId;
    private List<T> mList;


    /*-------------------------------常用构造器---------------------------------begin-------------*/
    public BaseSRVAdapter(List<T> list, int itemLayoutId) {
        this.mList = list;
        this.itemLayoutId = itemLayoutId;
    }

    /**
     * 分组构造器
     *
     * @param itemLayoutId    正常ItemId
     * @param sectionLayoutId 分组ItemId
     */
    public BaseSRVAdapter(List<T> list, int itemLayoutId, int sectionLayoutId) {
        this.mList = list;
        this.itemLayoutId = itemLayoutId;
        this.sectionLayoutId = sectionLayoutId;
    }
    /*-------------------------------常用构造器----------------------------------end--------------*/


    @Override
    public int getItemViewType(int position) {
        return getItemType(mList.get(position), position);
    }

    /**
     * 当有分组时，用于判断当前Item的类型
     *
     * @return 返回Item类型是正常类型还是分组类型
     */
    public int getItemType(T data, int position) {
        return TYPE_NORMAL;
    }

    @Override
    public SRVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SRVHolder holder;
        if (viewType == TYPE_NORMAL) {
            View view = LayoutInflater.from(parent.getContext()).inflate(itemLayoutId, parent, false);
            holder = SRVHolder.getViewHolder(view);
            onCreateView(parent, holder);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(sectionLayoutId, parent, false);
            holder = SRVHolder.getViewHolder(view);
            onCreateSectionView(parent, holder);
        }
        return holder;
    }

    /**
     * 用于初始化view
     */
    public void onCreateView(ViewGroup parent, SRVHolder holder) {
    }

    /**
     * 用于初始化分组的View
     */
    public void onCreateSectionView(ViewGroup parent, SRVHolder holder) {
    }

    @Override
    public void onBindViewHolder(final SRVHolder holder, int position) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            onBindView(holder, mList.get(position), position);
        } else if (getItemViewType(position) == TYPE_SECTION) {
            onBindSectionView(holder, mList.get(position), position);
        }
    }

    public abstract void onBindView(SRVHolder holder, T data, int i);

    public void onBindSectionView(SRVHolder holder, T data, int position) {
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

}
