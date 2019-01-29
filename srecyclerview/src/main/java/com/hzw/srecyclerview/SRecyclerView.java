package com.hzw.srecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.LayoutRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.List;

/**
 * 功能：刷新与加载更多
 * Created by 何志伟 on 2017/7/6.
 */
public class SRecyclerView extends RecyclerView implements AppBarLayout.OnOffsetChangedListener {

    private AbsRefreshHeader refreshHeader;
    private WrapperAdapter wrapperAdapter;
    private AbsLoadFooter loadingFooter;
    private SRecyclerViewModule config;
    private LoadListener loadListener;
    private AppBarLayout appBarLayout;
    private SRVDivider divider;
    private View emptyView;
    private View errorView;
    private View LoadView;

    private final SparseArray<View> headers = new SparseArray<>();
    private final SparseArray<View> footers = new SparseArray<>();
    private int HEADER_TYPE = 1314522;
    private int FOOTER_TYPE = HEADER_TYPE * 10;

    private boolean isLoadingEnable = true;
    private boolean isRefreshEnable = true;
    private boolean isAppBarExpand = true;
    private boolean isFirstMove = true;
    private boolean isLoading = false;
    private boolean isPullUp;

    private float dividerHeight;
    private float dividerRight;
    private float dividerLeft;
    private float firstY;
    private float lastY;

    private int currentScrollMode;
    private int currentState;
    private int dividerColor;


    public SRecyclerView(Context context) {
        super(context);
        init(null, 0);
    }

    public SRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int def) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SRecyclerView, def, 0);
        dividerHeight = a.getDimension(R.styleable.SRecyclerView_dividerHeight, 0);
        dividerColor = a.getColor(R.styleable.SRecyclerView_dividerColor, Color.TRANSPARENT);
        dividerRight = a.getDimension(R.styleable.SRecyclerView_dividerRightMargin, 0);
        dividerLeft = a.getDimension(R.styleable.SRecyclerView_dividerLeftMargin, 0);
        a.recycle();
        currentScrollMode = getOverScrollMode();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(manager);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (refreshHeader == null) return super.onTouchEvent(e);
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (isFirstMove) {
                    isFirstMove = false;
                    firstY = e.getRawY();
                    lastY = firstY;
                }
                float y = e.getRawY();
                float delay = y - lastY;
                lastY = y;
                if (isRefreshEnable && isTop() && isAppBarExpand) {
                    refreshHeader.move(delay);
                    setOverScrollMode(View.OVER_SCROLL_NEVER);
                    if (refreshHeader.isDelay()) return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                isFirstMove = true;
                isPullUp = e.getRawY() - firstY < 0;
                setOverScrollMode(currentScrollMode);
            default:
                if (isRefreshEnable && isTop() && isAppBarExpand) {
                    refreshHeader.up();
                }
        }
        return super.onTouchEvent(e);
    }

    private boolean isTop() {
        return refreshHeader.getParent() != null;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewParent parent = getParent();
        while (parent != null) {
            if (parent instanceof CoordinatorLayout) break;
            parent = parent.getParent();
        }
        if (parent == null) return;
        CoordinatorLayout layout = (CoordinatorLayout) parent;
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof AppBarLayout) {
                appBarLayout = (AppBarLayout) child;
                break;
            }
        }
        if (appBarLayout != null) {
            appBarLayout.addOnOffsetChangedListener(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearOnScrollListeners();
        if (getAdapter() != null && mObserver != null) {
            getAdapter().unregisterAdapterDataObserver(mObserver);
        }
        if (appBarLayout != null) {
            appBarLayout.removeOnOffsetChangedListener(this);
        }
        if (refreshHeader != null) refreshHeader.srvDetachedFromWindow();
        if (loadingFooter != null) loadingFooter.srvDetachedFromWindow();
        headers.clear();
        footers.clear();
        clearOnScrollListeners();
        loadListener = null;
        wrapperAdapter = null;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        isAppBarExpand = verticalOffset == 0;
    }

    private final AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            wrapperAdapter.notifyDataSetChanged();
            checkEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            wrapperAdapter.notifyItemRangeInserted(positionStart + getOffset(), itemCount);
            checkEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            wrapperAdapter.notifyItemRangeRemoved(positionStart + getOffset(), itemCount);
            checkEmpty();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            wrapperAdapter.notifyItemRangeChanged(positionStart + getOffset(), itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            wrapperAdapter.notifyItemRangeChanged(positionStart + getOffset(), itemCount, payload);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            wrapperAdapter.notifyItemMoved(fromPosition + getOffset(), toPosition);
        }

        private int getOffset() {
            return wrapperAdapter.getHeaderCount();
        }
    };


    /*-------------------------------setEmptyView-----------------------------------------start--------*/
    public void setEmptyView(@LayoutRes int layoutId) {
        setEmptyView(LayoutInflater.from(getContext())
                .inflate(layoutId, this, false));
    }

    public void setEmptyView(View view) {
        emptyView = view;
        if (emptyView != null) {
            checkEmpty();
        }
    }

    private void checkEmpty() {
        if (loadListener != null && wrapperAdapter != null && emptyView != null) {
            if (wrapperAdapter.isEmpty()) {
                wrapperAdapter.updateStateView(WrapperAdapter.STATE_EMPTY);
            } else {
                wrapperAdapter.updateStateView(WrapperAdapter.STATE_NORMAL);
            }
        }
    }
    /*-------------------------------setEmptyView-----------------------------------------end--------*/


    /*-------------------------------setErrorView-----------------------------------------start--------*/
    public void setErrorView(@LayoutRes int layoutId) {
        setErrorView(LayoutInflater.from(getContext())
                .inflate(layoutId, this, false));
    }

    public void setErrorView(View view) {
        errorView = view;
        if (errorView != null) {
            checkError();
        }
    }

    private void checkError() {
        if (wrapperAdapter != null && wrapperAdapter.isEmpty() && errorView != null) {
            wrapperAdapter.updateStateView(WrapperAdapter.STATE_ERROR);
        }
    }
    /*-------------------------------setErrorView-----------------------------------------end--------*/


    /*-------------------------------setLoadingView-----------------------------------------start--------*/
    public void setLoadingView(@LayoutRes int layoutId) {
        setLoadingView(LayoutInflater.from(getContext())
                .inflate(layoutId, this, false));
    }

    public void setLoadingView(View view) {
        LoadView = view;
    }

    private void checkLoading() {
        if (wrapperAdapter != null && LoadView != null) {
            wrapperAdapter.updateStateView(WrapperAdapter.STATE_LOADING);
        }
    }
    /*-------------------------------setLoadingView-----------------------------------------end--------*/


    @Override
    public void setAdapter(Adapter adapter) {
        if (getAdapter() != null) {
            getAdapter().unregisterAdapterDataObserver(mObserver);
        }
        wrapperAdapter = new WrapperAdapter(adapter);
        super.setAdapter(wrapperAdapter);
        adapter.registerAdapterDataObserver(mObserver);
        if (divider == null) {
            setDivider(dividerColor, dividerHeight, dividerLeft, dividerRight);
        }
        //设置了加载功能时，初始化刷新头和加载尾部
        if (config == null && loadListener != null && isInitLoad()) {
            initRefresh();
            initLoading();
            checkLoading();
        }
    }

    @Override
    public Adapter getAdapter() {
        if (wrapperAdapter != null) {
            return wrapperAdapter.getAdapter();
        }
        return null;
    }

    /**
     * 检查LayoutManager以确定是否需要初始化下拉刷新和加载更多，只有当LayoutManager属于
     * LinearLayoutManager和GridLayoutManager，并且方向为纵向时才返回true
     */
    private boolean isInitLoad() {
        LayoutManager manager = getLayoutManager();
        if (manager == null
                || !(manager instanceof LinearLayoutManager)
                || ((LinearLayoutManager) manager).getOrientation() != VERTICAL) {
            refreshHeader = null;
            loadingFooter = null;
            isLoadingEnable = false;
            isRefreshEnable = false;
            return false;
        }
        return true;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if (!(layout instanceof LinearLayoutManager) || (layout instanceof GridLayoutManager)) {
            if (divider != null) removeItemDecoration(divider);
        }
    }

    public void setDivider(int color, float height, float dividerLeft, float dividerRight) {
        LayoutManager layout = getLayoutManager();
        boolean isSetDivider = height != 0 && layout instanceof LinearLayoutManager;
        boolean isGridManager = layout instanceof GridLayoutManager;
        //只对LinearLayoutManager设置分割线
        if (isSetDivider && !isGridManager) {
            if (divider != null) removeItemDecoration(divider);
            LinearLayoutManager manager = (LinearLayoutManager) layout;
            if (manager.getOrientation() == LinearLayoutManager.VERTICAL) {
                divider = new SRVDivider(LinearLayoutManager.VERTICAL);
                divider.initVerticalDivider(height, color, dividerLeft, dividerRight);
                addItemDecoration(divider);
            } else if (manager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                divider = new SRVDivider(LinearLayoutManager.HORIZONTAL);
                divider.initHorizontalDivider(height, color);
                addItemDecoration(divider);
            }
        }
    }

    /**
     * 刷新和加载的监听
     */
    public interface LoadListener {
        void refresh();

        void loading();
    }

    public void setLoadListener(LoadListener listener) {
        loadListener = listener;
        //已经调用setAdapter，但是没有调用此方法
        if (getAdapter() != null && isInitLoad() && loadListener != null) {
            initRefresh();
            initLoading();
            checkLoading();
        }
    }

    /*------------------------------------------刷新头部操作----------------------------begin------*/
    private void initRefresh() {
        //只在此方法中仅仅获取一次用户全局配置
        initSRVConfig();
        //没有设置刷新头部时，设置默认的刷新头部，否则使用设置的刷新头
        if (refreshHeader == null) {
            refreshHeader = new SRVRefreshHeader(getContext());
        }
        refreshHeader.initHeader();
        wrapperAdapter.setRefreshHeader(refreshHeader);
        refreshHeader.setRefreshListener(new AbsRefreshHeader.RefreshListener() {
            @Override
            public void refresh() {
                loadListener.refresh();
            }
        });
    }

    /**
     * 获取用户配置的刷新头和加载尾
     * 配置的优先级为：代码设置 > SRVConfig配置
     */
    private void initSRVConfig() {
        config = SRVConfig.getInstance(getContext())
                .getConfig();
        if (config != null) {
            if (refreshHeader == null) refreshHeader = config.getRefreshHeader(getContext());
            if (loadingFooter == null) loadingFooter = config.getLoadingFooter(getContext());
            if (LoadView == null) LoadView = config.getLoadingView(getContext());
            if (errorView == null) errorView = config.getErrorView(getContext());
            if (emptyView == null) emptyView = config.getEmptyView(getContext());
            if (emptyView instanceof AbsStateView) {
                ((AbsStateView) emptyView).setRetryListener(new AbsStateView.RetryListener() {
                    @Override
                    public void retry(boolean isAnim) {
                        startRefresh(isAnim);
                    }
                });
            }
            if (errorView instanceof AbsStateView) {
                ((AbsStateView) errorView).setRetryListener(new AbsStateView.RetryListener() {
                    @Override
                    public void retry(boolean isAnim) {
                        startRefresh(isAnim);
                    }
                });
            }
        }
    }

    /**
     * 设置单独的刷新头部
     * 应在setAdapter之前调用才有效
     */
    public void setRefreshHeader(AbsRefreshHeader view) {
        if (view == null || getAdapter() != null) return;
        refreshHeader = view;
    }

    public void addHeader(View view) {
        if (view == null) return;
        checkAddView(view);
        headers.put(HEADER_TYPE++, view);
        if (wrapperAdapter != null) {
            wrapperAdapter.notifyAddHeader();
            checkEmpty();
        }
    }

    public void removeHeader(View view) {
        if (wrapperAdapter != null) {
            wrapperAdapter.removeHeader(view);
            checkEmpty();
        }
    }

    public void refreshComplete() {
        if (refreshHeader != null) {
            isLoading = false;
            refreshHeader.refreshComplete();
            loadingFooter.reset();
            loadingFooter.setTag(false);//isScroll
        }
    }

    public void startRefresh(boolean isAnim) {
        if (refreshHeader != null && isRefreshEnable && getAdapter() != null) {
            if (isAnim) scrollToPosition(0);
            refreshHeader.startRefresh(isAnim);
        }
    }

    public void setRefreshEnable(boolean enable) {
        isRefreshEnable = enable;
        if (!enable && refreshHeader != null && getAdapter() != null && loadListener != null) {
            isLoading = false;
            refreshHeader.refreshComplete();
        }
    }

    public void refreshError() {
        refreshComplete();
        checkError();
    }
    /*------------------------------------------刷新头部操作----------------------------end--------*/


    /*------------------------------------------尾部操作--------------------------------begin-----*/
    private void initLoading() {
        //用户没有设置刷新头部时，设置默认的刷新头部，否则使用用户的刷新头
        if (loadingFooter == null) {
            loadingFooter = new SRVLoadFooter(getContext());
        }
        loadingFooter.initFooter();
        wrapperAdapter.loadingEnable(isLoadingEnable);
        //刷新和加载只支持垂直方向的LinearLayoutManager和GridLayoutManager布局
        loadingFooter.setTag(false);//isScroll
        loadingFooter.setErrorRetryListener(new AbsLoadFooter.ErrorRetryListener() {
            @Override
            public void errorRetry() {
                judgeLastItem();
            }
        });
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                boolean isScroll = (boolean) loadingFooter.getTag();
                if (!isScroll && newState == SCROLL_STATE_IDLE && isLoadingEnable && isPullUp) {
                    judgeLastItem();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && isLoadingEnable) {
                    loadingFooter.setTag(true);//current scroll
                    judgeLastItem();
                }
            }
        });
    }

    /**
     * 判断是否开始加载更多，只有滑动到最后一个Item，并且当前有数据时，才会加载更多
     */
    private synchronized void judgeLastItem() {
        LinearLayoutManager manager = (LinearLayoutManager) getLayoutManager();
        int last = manager.findLastVisibleItemPosition();
        if (!isLoading && last > 1 && last == (wrapperAdapter.getItemCount() - 1)) {
            isLoading = true;
            loadingFooter.loadBegin();
            loadListener.loading();
        }
    }

    /**
     * 设置自己的加载尾部
     */
    public void setLoadingFooter(AbsLoadFooter view) {
        if (view == null || getAdapter() != null) return;
        loadingFooter = view;
    }

    public void addFooter(View view) {
        if (view == null) return;
        checkAddView(view);
        footers.put(FOOTER_TYPE++, view);
        if (wrapperAdapter != null) {
            wrapperAdapter.notifyAddFooter();
            checkEmpty();
        }
    }

    public void removeFooter(View view) {
        if (wrapperAdapter != null) {
            wrapperAdapter.removeFooter(view);
            checkEmpty();
        }
    }

    public void loadingComplete() {
        if (loadingFooter != null) {
            isLoading = false;
            loadingFooter.loadSuccess();
        }
    }

    public void setLoadingEnable(boolean enable) {
        isLoadingEnable = enable;
        if (wrapperAdapter != null) {
            wrapperAdapter.loadingEnable(enable);
        }
        if (!enable && getAdapter() != null && loadListener != null) {
            loadingComplete();
        }
    }

    public void loadNoMoreData() {
        if (loadingFooter != null) {
            isLoading = true;
            loadingFooter.loadingNoMoreData();
        }
    }

    public void loadingError() {
        if (loadingFooter != null) {
            isLoading = false;
            loadingFooter.loadingError();
        }
    }
    /*------------------------------------------尾部操作-------------------------------end------*/


    private void checkAddView(View view) {
        if (view.getParent() != null) {
            throw new IllegalStateException("The specified child already has a parent. "
                    + "You must call removeView() on the child's parent first.");
        }
    }

    private class WrapperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final int REFRESH_HEADER = 1314520;
        private final int LOAD_FOOTER = HEADER_TYPE + FOOTER_TYPE;
        private static final int STATE_LOADING = 1;
        private static final int STATE_EMPTY = 2;
        private static final int STATE_ERROR = 3;
        private static final int STATE_NORMAL = 0;
        private final ClickListener listener;
        private final Adapter adapter;

        private WrapperAdapter(Adapter adapter) {
            this.adapter = adapter;
            listener = new ClickListener();
        }

        private Adapter getAdapter() {
            return adapter;
        }

        private boolean isHeader(int position) {
            return position < getHeaderCount();
        }

        private void notifyAddHeader() {
            notifyItemInserted(getOnlyHeaderCount());
        }

        private void setRefreshHeader(View view) {
            headers.put(REFRESH_HEADER, view);
            notifyItemInserted(0);
        }

        private void removeHeader(View view) {
            if (view == null) return;
            for (int i = 0; i < getHeaderCount(); i++) {
                if (view == headers.valueAt(i)) {
                    headers.removeAt(i);
                    notifyItemRemoved(i);
                    break;
                }
            }
        }

        private boolean isFooter(int position) {
            return position >= (getDataCount() + getHeaderCount());
        }

        private void notifyAddFooter() {
            int insertPosition = getHeaderCount() + getDataCount() + getFooterCount();
            //如果有加载尾部，则在尾部之前插入Item，保证加载尾部是最后一个Item
            if (hasLoadingFooter()) insertPosition -= 1;
            notifyItemInserted(insertPosition);
        }

        private void setLoadFooter(View view) {
            footers.put(LOAD_FOOTER, view);
            int insertPosition = getHeaderCount() + getDataCount() + getFooterCount();
            notifyItemInserted(insertPosition);
        }

        private void removeFooter(View view) {
            if (view == null) return;
            for (int i = 0; i < getFooterCount(); i++) {
                if (view == footers.valueAt(i)) {
                    footers.removeAt(i);
                    notifyItemRemoved(getHeaderCount() + getDataCount() + i);
                    break;
                }
            }
        }

        private void loadingEnable(boolean enable) {
            if (enable) {
                if (!hasLoadingFooter()) {
                    setLoadFooter(loadingFooter);
                }
            } else {
                if (hasLoadingFooter()) {
                    removeFooter(loadingFooter);
                }
            }
        }

        private void updateStateView(int state) {
            switch (state) {
                case STATE_LOADING:
                    if (currentState == STATE_LOADING) return;
                    initStateView(STATE_LOADING);
                    initLoadingFooter(false);
                    addStateView(LoadView);
                    break;
                case STATE_EMPTY:
                    if (currentState == STATE_EMPTY) return;
                    initStateView(STATE_EMPTY);
                    initLoadingFooter(false);
                    addStateView(emptyView);
                    break;
                case STATE_ERROR:
                    if (currentState == STATE_ERROR) return;
                    initStateView(STATE_ERROR);
                    initLoadingFooter(false);
                    addStateView(errorView);
                    break;
                default:
                    if (currentState == STATE_NORMAL) return;
                    initStateView(STATE_NORMAL);
                    initLoadingFooter(true);
            }
        }

        private void initStateView(int state) {
            if (currentState == STATE_LOADING) {
                removeStateView(LoadView);
            } else if (currentState == STATE_EMPTY) {
                removeStateView(emptyView);
            } else if (currentState == STATE_ERROR) {
                removeStateView(errorView);
            }
            currentState = state;
        }

        /**
         * 状态布局显示隐藏时，加载尾部的添加和删除操作，仅用于updateStateView()方法
         */
        private void initLoadingFooter(boolean isAdd) {
            if (!isLoadingEnable) return;
            loadingEnable(isAdd);
        }

        /**
         * 状态布局以footer的方式添加显示
         *
         * @param view stateView
         */
        private void addStateView(View view) {
            footers.put(FOOTER_TYPE++, view);
            notifyAddFooter();
        }

        private void removeStateView(View view) {
            removeFooter(view);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (headers.get(viewType) != null) {
                return new Holder(headers.get(viewType));
            } else if (footers.get(viewType) != null) {
                return new Holder(footers.get(viewType));
            }
            return adapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            srvBindViewHolder(holder, position, null);
        }

        @SuppressWarnings("unchecked")
        private void srvBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
            if (isHeader(position) || isFooter(position)) return;
            position -= getHeaderCount();
            holder.itemView.setTag(R.id.srv_item_click, position);
            holder.itemView.setOnClickListener(listener);
            if (payloads == null) {
                adapter.onBindViewHolder(holder, position);
            } else {
                adapter.onBindViewHolder(holder, position, payloads);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
            srvBindViewHolder(holder, position, payloads);
        }

        @Override
        public int getItemViewType(int position) {
            if (isHeader(position)) {
                return headers.keyAt(position);
            } else if (isFooter(position)) {
                return footers.keyAt(position - getHeaderCount() - getDataCount());
            }
            return adapter.getItemViewType(position - getHeaderCount());
        }

        @Override
        public int getItemCount() {
            return getDataCount() + getHeaderCount() + getFooterCount();
        }

        private int getHeaderCount() {
            return headers.size();
        }

        private int getFooterCount() {
            return footers.size();
        }

        private int getDataCount() {
            return adapter.getItemCount();
        }

        private int getOnlyHeaderCount() {
            return getHeaderCount() - (headers.get(REFRESH_HEADER) == null ? 0 : 1);
        }

        private int getOnlyFooterCount() {
            return getFooterCount() - (hasLoadingFooter() ? 1 : 0) - (currentState == STATE_NORMAL ? 0 : 1);
        }

        private boolean hasLoadingFooter() {
            return footers.get(LOAD_FOOTER) != null;
        }

        private boolean isEmpty() {
            return (getDataCount() + getOnlyHeaderCount() + getOnlyFooterCount()) == 0;
        }

        private class Holder extends ViewHolder {
            Holder(View itemView) {
                super(itemView);
            }
        }

        /**
         * GridLayout(GridView)的头部特殊处理
         */
        @Override
        public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = (GridLayoutManager) manager;
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        boolean b = isHeader(position) || isFooter(position);
                        return b ? gridManager.getSpanCount() : 1;
                    }
                });
            }
        }

        /**
         * StaggeredGridLayout(瀑布流)的头部特殊处理
         */
        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            if (params != null && params instanceof StaggeredGridLayoutManager.LayoutParams) {
                if (holder.getLayoutPosition() < getHeaderCount() || holder.getLayoutPosition() > (getHeaderCount()
                        + getDataCount() - 1)) {
                    StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) params;
                    p.setFullSpan(true);
                }
            }
        }
    }


    /*-----------------------------------Item的点击事件------------------------------start-------*/
    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                int position = (int) v.getTag(R.id.srv_item_click);
                clickListener.click(v, position);
            }
        }
    }

    public interface ItemClickListener {
        void click(View v, int position);
    }

    private ItemClickListener clickListener;

    public void setItemClickListener(ItemClickListener listener) {
        clickListener = listener;
    }
    /*-----------------------------------Item的点击事件------------------------------end-------*/


    /**
     * 设置LinearLayoutManager的分割线
     */
    private class SRVDivider extends RecyclerView.ItemDecoration {

        private float dividerHeight, leftMargin, rightMargin;
        private final int mOrientation;
        private final Paint mPaint;

        private SRVDivider(int orientation) {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStyle(Paint.Style.FILL);
            this.mOrientation = orientation;
        }

        /**
         * 横向的分割线
         *
         * @param height      分割线高
         * @param color       分割线颜色
         * @param leftMargin  分割线距离左边的距离
         * @param rightMargin 分割线距离右边的距离
         */
        private void initVerticalDivider(float height, int color, float leftMargin, float rightMargin) {
            this.dividerHeight = height;
            this.leftMargin = leftMargin;
            this.rightMargin = rightMargin;
            mPaint.setColor(color);
        }

        private void initHorizontalDivider(float height, int color) {
            this.dividerHeight = height;
            mPaint.setColor(color);
        }

        /**
         * 刷新头部和加载尾部以及stateView不需要分割线
         */
        private boolean isLoadView(View view) {
            return refreshHeader == view || loadingFooter == view || currentState != WrapperAdapter.STATE_NORMAL;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            if (mOrientation == LinearLayoutManager.VERTICAL) {
                drawHorizontal(c, parent);
            } else {
                drawVertical(c, parent);
            }
        }

        /**
         * 绘制横向的 item 分割线
         */
        private void drawHorizontal(Canvas canvas, RecyclerView parent) {
            final float left = parent.getPaddingLeft() + leftMargin;
            final float right = parent.getMeasuredWidth() - parent.getPaddingRight() - rightMargin;
            final int childSize = parent.getChildCount();
            for (int i = 0; i < childSize; i++) {
                final View child = parent.getChildAt(i);
                if (!isLoadView(child)) {
                    RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) child.getLayoutParams();
                    final int top = child.getBottom() + p.bottomMargin;
                    final float bottom = top + dividerHeight;
                    canvas.drawRect(left, top, right, bottom, mPaint);
                }
            }
        }

        /**
         * 绘制纵向的 item 分割线
         */
        private void drawVertical(Canvas canvas, RecyclerView parent) {
            final int top = parent.getPaddingTop();
            final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
            final int childSize = parent.getChildCount();
            for (int i = 0; i < childSize; i++) {
                final View child = parent.getChildAt(i);
                if (!isLoadView(child)) {
                    RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) child.getLayoutParams();
                    final int left = child.getRight() + p.rightMargin;
                    final float right = left + dividerHeight;
                    canvas.drawRect(left, top, right, bottom, mPaint);
                }
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View v, RecyclerView p, RecyclerView.State s) {
            int size = isLoadView(v) ? 0 : (int) dividerHeight;
            if (mOrientation == LinearLayoutManager.VERTICAL) {
                outRect.set(0, 0, 0, size);
            } else {
                outRect.set(0, 0, size, 0);
            }
        }
    }

}
