package com.hzw.srecyclerviewproject;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

/**
 * author: hzw
 * time: 2018/8/12 下午4:51
 * description:
 */
public class TestConfigDialog extends Dialog implements View.OnClickListener {

    public TestConfigDialog(@NonNull Context context) {
        super(context, R.style.dialogStyle);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window == null) return;
        window.setContentView(R.layout.dialog_config);
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        initView();
    }

    private void initView() {
        findViewById(R.id.add_header).setOnClickListener(this);
        findViewById(R.id.remove_header).setOnClickListener(this);
        findViewById(R.id.refresh).setOnClickListener(this);
        findViewById(R.id.add_footer).setOnClickListener(this);
        findViewById(R.id.remove_footer).setOnClickListener(this);
        findViewById(R.id.reset_adapter).setOnClickListener(this);
        findViewById(R.id.refresh_loading).setOnClickListener(this);
        findViewById(R.id.footer_loading).setOnClickListener(this);
        findViewById(R.id.loading_error).setOnClickListener(this);
        findViewById(R.id.empty).setOnClickListener(this);
        findViewById(R.id.error).setOnClickListener(this);
        findViewById(R.id.loading_nodata).setOnClickListener(this);
    }

    @Override public void onClick(View v) {
        if (listener == null) return;
        switch (v.getId()) {
            case R.id.add_header:
                listener.addHeader();
                break;
            case R.id.remove_header:
                listener.removeHeader();
                break;
            case R.id.refresh:
                listener.startRefresh();
                break;
            case R.id.add_footer:
                listener.addFooter();
                break;
            case R.id.remove_footer:
                listener.removeFooter();
                break;
            case R.id.reset_adapter:
                listener.resetAdapter();
                break;
            case R.id.refresh_loading:
                SRVTestConfig.getInstance()
                        .setRefresh(true);
                Toast.makeText(getContext(), "再刷新试试看！", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.footer_loading:
                SRVTestConfig.getInstance()
                        .setLoadingNormal();
                Toast.makeText(getContext(), "加载更多正常", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.loading_error:
                SRVTestConfig.getInstance()
                        .setLoadingError(true);
                Toast.makeText(getContext(), "加载更多会错误", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.loading_nodata:
                SRVTestConfig.getInstance()
                        .setLoadingNoData(true);
                Toast.makeText(getContext(), "加载更多无数据", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.empty:
                SRVTestConfig.getInstance()
                        .setRefreshEmpty(true);
                listener.showEmpty();
                break;
            case R.id.error:
                SRVTestConfig.getInstance()
                        .setRefreshError(true);
                listener.showError();
                break;
        }
        dismiss();
    }


    public interface TestListener {
        void addHeader();

        void removeHeader();

        void addFooter();

        void removeFooter();

        void startRefresh();

        void resetAdapter();

        void showEmpty();

        void showError();
    }

    private TestListener listener;

    public void setTestListener(TestListener listener) {
        this.listener = listener;
    }
}
