package com.hzw.srecyclerviewproject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hzw.srecyclerview.BaseSRVAdapter;
import com.hzw.srecyclerview.SRVHolder;
import com.hzw.srecyclerview.SRecyclerView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private List<String> list = new ArrayList<>();
    private SRecyclerView recyclerView;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (SRecyclerView) findViewById(R.id.srv_test);
        emptyView = findViewById(R.id.emptyView);


        //如果设置了加载监听，就是需要刷新加载功能，如果没有设置加载监听，那么就没有下拉与底部加载
        recyclerView.setLoadListener(new SRecyclerView.LoadListener() {
            @Override
            public void refresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshData();
                        recyclerView.refreshComplete();
                    }
                }, 2000);
            }

            @Override
            public void loading() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (list.size() != 30) {
                            loadData();
                            recyclerView.loadingComplete();
                        } else {
                            recyclerView.loadNoMoreData();
                        }
                    }
                }, 2000);
            }
        });

        //item的点击事件
        recyclerView.setItemClickListener(new SRecyclerView.ItemClickListener() {
            @Override
            public void click(View v, int position) {
                Toast.makeText(getApplication(), "位置：  " + position, Toast.LENGTH_SHORT).show();
                if (position == 0) {
                    recyclerView.startRefresh(true);
                }
            }
        });

        //可以设置一个EmptyView
        recyclerView.setEmptyView(emptyView);

        //可以手动设置一个刷新头部，应该在setAdapter方法之前调用，适用于某个列表需要特殊刷新头的场景
        //SRecyclerView的头部设置有两种种方法：代码设置，全局配置。如果两种方法都没有设置，则适用默认自带的默认刷新头和加载尾
        //recyclerView.setRefreshHeader(new TestRefreshHeader(this));
        //recyclerView.setLoadingFooter(new TestLoadFooter(this));


        //也可以新建一个类，并实现SRecyclerViewModule接口，并在AndroidManifest.xml中添加meta-data，
        // 对SRV进行全局配置，name为实现类的路径，value必须为SRecyclerViewModule接口名称
        //示例如下
        // <meta-data
        //          android:name="com.hzw.srecyclerviewproject.TestSRVModule"
        //          android:value="SRecyclerViewModule" />


        //这里的适配器使用的一个简易的SRV适配器，同样也可以用于普通的RecyclerView，当然这里也可以用原生的适配器
        recyclerView.setAdapter(new SRVAdapter(list));
        //recyclerView.setAdapter(new InitAdapter(list, this));


        //测试添加头部，添加头部的方法应该在setAdapter方法之后调用
        View header = LayoutInflater.from(this).inflate(R.layout.header_test, recyclerView, false);
        recyclerView.addHeader(header);

        //测试添加尾部，添加头部的方法应该在setAdapter方法之后调用
        View footer = LayoutInflater.from(this).inflate(R.layout.footer_test, recyclerView, false);
        recyclerView.addFooter(footer);


        //SRV的代码刷新，应该在setAdapter方法之后调用，true表示会有刷新动画，false无任何动画
        //recyclerView.startRefresh(true);


        //测试数据
        refreshData();


        //混淆
        //-keep public class * implements com.hzw.srecyclerview.SRecyclerViewModule
    }

    private void refreshData() {
        list.clear();
        for (int i = 0; i < 15; i++) {
            list.add("数据  " + i);
        }
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void loadData() {
        for (int i = 15; i < 30; i++) {
            list.add("数据  " + i);
        }
        recyclerView.getAdapter().notifyDataSetChanged();
    }


    /**
     * 基于简易适配器的写法
     */
    private static class SRVAdapter extends BaseSRVAdapter<String> {
        SRVAdapter(List<String> list) {
            super(list, R.layout.item_test);
        }

        @Override
        public void onBindView(SRVHolder holder, String data, int i) {
            holder.setTextView(R.id.tv_item_test, data);
//            holder.setTextView(0, "123")
//                    .setTextView(1, "234");
        }
    }


    /**
     * 原生的适配器
     */
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


}
