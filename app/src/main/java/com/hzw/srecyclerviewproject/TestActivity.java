package com.hzw.srecyclerviewproject;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hzw.srecyclerview.BaseSRVAdapter;
import com.hzw.srecyclerview.SRVHolder;
import com.hzw.srecyclerview.SRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    private List<TestBean> datas = new ArrayList<>();
    private SRecyclerView srv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        srv = new SRecyclerView(this);
        setContentView(srv);
        srv.setDivider(Color.LTGRAY,2,30,0);
        srv.setLoadListener(new SRecyclerView.LoadListener() {
            @Override
            public void refresh() {

            }

            @Override
            public void loading() {

            }
        });
        for (int i = 0; i < 15; i++) {
            TestBean bean = new TestBean();
            bean.title = String.valueOf(i);
            bean.type = (i == 0 || i == 6 || i == 10) ? 0 : 1;
            datas.add(bean);
        }
        srv.setAdapter(new SRVAdapter(datas));
    }

    private static class TestBean {
        String title;
        int type;
    }

    private static class SRVAdapter extends BaseSRVAdapter<TestBean> {
        SRVAdapter(List<TestBean> list) {
            super(list, R.layout.item_test, R.layout.footer_test);
        }

        @Override
        public void onBindView(SRVHolder holder, TestBean data, int i) {
            holder.setTextView(R.id.tv_item_test, String.format("数据%s", data.title));
        }


        @Override
        public void onBindSectionView(SRVHolder holder, TestBean data, int position) {
            holder.setTextView(R.id.test_section, "分组标题");
        }

        @Override
        public int getItemType(TestBean data, int position) {
            if (data.type == 0) {
                return TYPE_SECTION;
            } else {
                return TYPE_NORMAL;
            }
        }
    }

}
