package com.hzw.srecyclerviewproject;

/**
 * author: hzw
 * time: 2018/8/12 下午2:58
 * description:
 */
public class SRVTestConfig {

    private volatile static SRVTestConfig instance;

    private SRVTestConfig() {
    }

    public static SRVTestConfig getInstance() {
        if (instance == null) {
            synchronized (SRVTestConfig.class) {
                if (instance == null) {
                    instance = new SRVTestConfig();
                }
            }
        }
        return instance;
    }

    private boolean refreshError;
    private boolean refreshEmpty;
    private boolean refresh = true;

    public boolean isRefreshError() {
        return refreshError;
    }

    public void setRefreshError(boolean refreshError) {
        this.refreshError = refreshError;
        if (refreshError) {
            refreshEmpty = false;
            refresh = false;
        }
    }

    public boolean isRefreshEmpty() {
        return refreshEmpty;
    }

    public void setRefreshEmpty(boolean refreshEmpty) {
        this.refreshEmpty = refreshEmpty;
        if (refreshEmpty) {
            refreshError = false;
            refresh = false;
        }
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
        if (refresh) {
            refreshError = false;
            refreshEmpty = false;
        }
    }


    private boolean loadingNoData;
    private boolean loadingError;

    public boolean isLoadingNoData() {
        return loadingNoData;
    }

    public void setLoadingNoData(boolean loadingNoData) {
        this.loadingNoData = loadingNoData;
        if (loadingNoData) {
            loadingError = false;
        }
    }

    public boolean isLoadingError() {
        return loadingError;
    }

    public void setLoadingError(boolean loadingError) {
        this.loadingError = loadingError;
        if (loadingError) {
            loadingNoData = false;
        }
    }

    public void setLoadingNormal() {
        loadingNoData = false;
        loadingError = false;
    }

}
