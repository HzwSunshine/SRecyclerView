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

import com.hzw.srecyclerview.BaseSRVAdapter;
import com.hzw.srecyclerview.SRVHolder;
import com.hzw.srecyclerview.SRecyclerView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private List<String> list = new ArrayList<>();
    private SRecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.srv_test);
        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfigDialog();
            }
        });

        //如果设置了加载监听，就是需要刷新加载功能，如果没有设置加载监听，那么就没有下拉与底部加载
        recyclerView.setLoadListener(new SRecyclerView.LoadListener() {
            @Override
            public void refresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (SRVTestConfig.getInstance()
                                .isRefreshEmpty()) {
                            recyclerView.getAdapter()
                                    .notifyDataSetChanged();
                            recyclerView.refreshComplete();
                        } else if (SRVTestConfig.getInstance()
                                .isRefreshError()) {
                            recyclerView.refreshError();
                        } else if (SRVTestConfig.getInstance()
                                .isRefresh()) {
                            refreshData();
                            recyclerView.refreshComplete();
                        }
                    }
                }, 2000);
            }

            @Override
            public void loading() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (SRVTestConfig.getInstance()
                                .isLoadingError()) {
                            recyclerView.loadingError();
                            return;
                        }
                        if (SRVTestConfig.getInstance()
                                .isLoadingNoData()) {
                            recyclerView.loadNoMoreData();
                            return;
                        }
                        loadData();
                        recyclerView.loadingComplete();
                    }
                }, 2000);
            }
        });

        //item的点击事件
        recyclerView.setItemClickListener(new SRecyclerView.ItemClickListener() {
            @Override
            public void click(View v, int position) {
                Toast.makeText(getApplication(), "位置：  " + position, Toast.LENGTH_SHORT)
                        .show();

                list.set(list.size() - 1, "这是通知改变的");
                recyclerView.getAdapter().notifyItemChanged(list.size() - 1);
            }
        });

        //可以设置一个EmptyView
        //recyclerView.setEmptyView(new View(this));

        //可以在xml中配置分割线，也可以在代码中设置分割线
        recyclerView.setDivider(Color.LTGRAY, 3, 30, 0);

        //可以手动设置一个刷新头部，应该在setAdapter方法之前调用，适用于某个列表需要特殊刷新头的场景
        //SRecyclerView的刷新头部和加载尾部的设置有两种种方法：
        // 代码设置，全局配置。如果两种方法都没有设置，则使用自带的默认刷新头和加载尾
        //recyclerView.setRefreshHeader(new TestRefreshHeader(this));
        //recyclerView.setLoadingFooter(new TestLoadFooter(this));


        //SRecyclerView的刷新头部和加载尾部的全局配置需要新建一个类，
        // 并实现SRecyclerViewModule接口，并在AndroidManifest.xml中添加meta-data，
        // 对SRV进行全局配置，name为实现类的路径，value必须为接口名称： "SRecyclerViewModule"
        //示例如下
        // <meta-data
        //          android:name="com.hzw.srecyclerviewproject.TestSRVModule"
        //          android:value="SRecyclerViewModule" />


        //可以添加一个或多个尾部
        View footer = LayoutInflater.from(this)
                .inflate(R.layout.footer_test, recyclerView, false);
        //recyclerView.addFooter(footer);

        //这里的适配器使用的一个简易的SRV适配器：BaseSRVAdapter，可以很大程度上减少适配器的代码量，
        // 这个适配器同样也可以用于普通的RecyclerView，当然这里也可以用原生的适配器
        recyclerView.setAdapter(new SRVAdapter(list));
        //recyclerView.setAdapter(new InitAdapter(list, this));

        //可以添加一个或多个头部
        View header = LayoutInflater.from(this)
                .inflate(R.layout.header_test, recyclerView, false);
        //recyclerView.addHeader(header);

        //SRV的代码刷新，应该在setAdapter方法之后调用，true表示有刷新动画，false无动画
        //recyclerView.startRefresh(true);
        recyclerView.startRefresh(false);


        //测试数据
        //refreshData();

        //混淆
        //-keep public class * implements com.hzw.srecyclerview.SRecyclerViewModule
    }

    private int index;

    private void refreshData() {
        list.clear();
        index = 0;
        for (int i = 0; i < 15; i++) {
            list.add("数据  " + index++);
        }
        recyclerView.getAdapter()
                .notifyDataSetChanged();
    }

    private void loadData() {
        int last = list.size();
        for (int i = 0; i < 15; i++) {
            list.add("数据  " + index++);
        }
        recyclerView.getAdapter()
                .notifyItemInserted(last);
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
                textView = itemView.findViewById(R.id.tv_item_test);
            }
        }
    }

    private void reStart() {
        recreate();
    }


    private TestConfigDialog dialog;
    private List<View> headers = new ArrayList<>();
    private List<View> footers = new ArrayList<>();

    private void showConfigDialog() {
        if (dialog == null) {
            dialog = new TestConfigDialog(this);
            dialog.setTestListener(new TestConfigDialog.TestListener() {
                @Override
                public void addHeader() {
                    View header = LayoutInflater.from(MainActivity.this)
                            .inflate(R.layout.header_test, recyclerView, false);
                    recyclerView.addHeader(header);
                    headers.add(header);
                }

                @Override
                public void removeHeader() {
                    if (headers.size() == 0) return;
                    View header = headers.get(headers.size() - 1);
                    recyclerView.removeHeader(header);
                    headers.remove(header);
                }

                @Override
                public void addFooter() {
                    View footer = LayoutInflater.from(MainActivity.this)
                            .inflate(R.layout.footer_test, recyclerView, false);
                    recyclerView.addFooter(footer);
                    footers.add(footer);
                }

                @Override
                public void removeFooter() {
                    if (footers.size() == 0) return;
                    View footer = footers.get(footers.size() - 1);
                    recyclerView.removeFooter(footer);
                    footers.remove(footer);
                }

                @Override
                public void startRefresh() {
                    recyclerView.startRefresh(true);
                }

                @Override
                public void resetAdapter() {
                    list.clear();
                    index = 0;
                    for (int i = 0; i < 15; i++) {
                        list.add("数据  " + index++);
                    }
                    recyclerView.setAdapter(new SRVAdapter(list));
                }

                @Override
                public void showEmpty() {
                    reStart();
                }

                @Override
                public void showError() {
                    reStart();
                }
            });
        }
        dialog.show();
    }

}
