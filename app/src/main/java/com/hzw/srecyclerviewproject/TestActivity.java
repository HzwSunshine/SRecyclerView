package com.hzw.srecyclerviewproject;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hzw.srecyclerview.SRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：
 * Created by 何志伟 on 2017/8/18.
 */

public class TestActivity extends AppCompatActivity {

    private List<String> list = new ArrayList<>();
    private SRecyclerView srv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        srv = new SRecyclerView(this);
        srv.setDivider(Color.LTGRAY, 2, 0, 0);
        setContentView(srv);

        srv.setLoadingFooter(new TestLoadFooter(this));

        srv.setRefreshEnable(true);



        srv.setLoadListener(new SRecyclerView.LoadListener() {
            @Override
            public void refresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srv.refreshComplete();
                    }
                },2500);
            }

            @Override
            public void loading() {

            }
        });

        InitAdapter adapter = new InitAdapter(list, this);
        srv.setAdapter(adapter);

        srv.setRefreshHeader(new TestRefreshHeader(this));

        srv.setLoadingEnable(false);


        srv.setItemClickListener(new SRecyclerView.ItemClickListener() {
            @Override
            public void click(View v, int position) {
                if (position == 0) {
                    srv.setLoadingEnable(true);
                    Toast.makeText(getApplication(),"加载尾部可用",Toast.LENGTH_SHORT).show();
                }else if (position == 1) {
                    srv.setLoadingEnable(false);
                    Toast.makeText(getApplication(),"加载尾部不可用！",Toast.LENGTH_SHORT).show();
                }else if (position == 2) {
                    srv.setRefreshEnable(true);
                    Toast.makeText(getApplication(),"刷新头可用！",Toast.LENGTH_SHORT).show();
                }else if (position == 3) {
                    srv.setRefreshEnable(false);
                    Toast.makeText(getApplication(),"刷新头不可用！",Toast.LENGTH_SHORT).show();
                }else if (position == 4) {
                    srv.startRefresh(true);
                    Toast.makeText(getApplication(),"刷新",Toast.LENGTH_SHORT).show();
                }
            }
        });

        refreshData();

        srv.startRefresh(true);
    }

    private void refreshData() {
        list.clear();
        for (int i = 0; i < 15; i++) {
            list.add("数据  " + i);
        }
        srv.getAdapter().notifyDataSetChanged();
    }

    private void loadData() {
        for (int i = 15; i < 30; i++) {
            list.add("数据  " + i);
        }
        srv.getAdapter().notifyDataSetChanged();
    }


    //
    //
    //
    //
    //


    private static class InitAdapter extends RecyclerView.Adapter<InitAdapter.Holder> {
        private LayoutInflater inflater;
        private List<String> list;

        InitAdapter(List<String> list, Context context) {
            this.list = list;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = inflater.inflate(R.layout.item_test, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.textView.setText(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class Holder extends RecyclerView.ViewHolder {
            private TextView textView;

            Holder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.tv_item_test);
            }
        }
    }

//    @Override
//    public void onBackPressed() {
//    }


}
