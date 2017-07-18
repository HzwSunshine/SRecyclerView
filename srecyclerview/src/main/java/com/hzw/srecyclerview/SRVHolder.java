package com.hzw.srecyclerview;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 功能：BaseSRVAdapter的ViewHolder
 * Created by 何志伟 on 2017/7/17.
 */
public class SRVHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> views;
    private View itemView;

    private SRVHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        views = new SparseArray<>();
    }

    public static SRVHolder getViewHolder(View itemView) {
        SRVHolder holder = (SRVHolder) itemView.getTag();
        if (holder == null) {
            holder = new SRVHolder(itemView);
            itemView.setTag(holder);
        }
        return holder;
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int id) {
        View childView = views.get(id);
        if (childView == null) {
            childView = itemView.findViewById(id);
            views.put(id, childView);
        }
        return (T) childView;
    }

    //返回ItemView
    public View getItemView() {
        return itemView;
    }

    //封装返回常用的控件
    public TextView getTextView(int id) {
        return getView(id);
    }

    public Button getButton(int id) {
        return getView(id);
    }

    public ImageView getImageView(int id) {
        return getView(id);
    }

    //封装设置常用的控件
    public SRVHolder setTextView(int id, CharSequence text) {
        getTextView(id).setText(text);
        return this;
    }

    public SRVHolder setButton(int id, CharSequence title) {
        getButton(id).setText(title);
        return this;
    }

    public SRVHolder setImageResource(int id, int resource) {
        getImageView(id).setImageResource(resource);
        return this;
    }

}
