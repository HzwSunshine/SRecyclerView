package com.hzw.srecyclerview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;


/**
 * 功能：添加滑动监听加载数据
 * Created by 何志伟 on 2017/7/6.
 */
public class SRecyclerView extends RecyclerView implements AppBarLayout.OnOffsetChangedListener {

    private static final int APP_BAR_EXPAND = 0;
    private static final int APP_BAR_CLOSE = 1;
    private static final int NO_COLOR = -1;

    private AbsRefreshHeader refreshHeader;
    private WrapperAdapter wrapperAdapter;
    private AbsLoadFooter loadingFooter;
    private LoadListener loadListener;
    private XRVDivider divider;
    private View emptyView;

    private boolean isFirstMove = true;
    //当前是否正在加载数据
    private boolean isLoading = false;
    private boolean isLoadingEnable;
    private boolean isRefreshEnable;
    private boolean isPullUp;

    private float dividerHeight;
    private float dividerRight;
    private float dividerLeft;
    private int dividerColor;

    private int currentScrollMode;
    private int refreshGravity;
    private int refreshHeight;
    private int appBarState;
    private int duration;
    private float lastY;


    public SRecyclerView(Context context) {
        super(context);
        init(null, 0);
    }

    public SRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int def) {
        //初始化XRV的配置
        initXRVConfig(attrs, def);
        //other
        currentScrollMode = getOverScrollMode();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(manager);
        isLoadingEnable = true;
        isRefreshEnable = true;
    }

    /**
     * 获取用户的配置
     * 所有配置的优先级为：代码设置 > xml设置 > XRVConfig配置
     * 可配置的选项有：刷新头部，加载尾部，刷新高度，刷新头部的Gravity，刷新动画的Duration
     */
    private void initXRVConfig(AttributeSet attrs, int def) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SRecyclerView, def, 0);
        int height = (int) a.getDimension(R.styleable.SRecyclerView_refreshHeight, dip2px(60));
        int gravity = a.getInteger(R.styleable.SRecyclerView_refreshGravity, 1);
        int duration = a.getInteger(R.styleable.SRecyclerView_refreshDuration, 200);
        dividerHeight = a.getDimension(R.styleable.SRecyclerView_dividerHeight, 0);
        dividerColor = a.getColor(R.styleable.SRecyclerView_dividerColor, NO_COLOR);
        dividerRight = a.getDimension(R.styleable.SRecyclerView_dividerRightMargin, 0);
        dividerLeft = a.getDimension(R.styleable.SRecyclerView_dividerLeftMargin, 0);
        a.recycle();
        //因为XRVConfig配置的优先级最低，所以先读取此配置
        SRecyclerViewModule config = new GetSRVModule(getContext()).getConfig();
        if (config != null) {
            refreshHeader = config.getRefreshHeader(getContext());
            loadingFooter = config.getLoadingFooter(getContext());
            refreshHeight = config.getRefreshHeight(getContext());
            this.duration = config.getRefreshDuration();
            refreshGravity = config.getRefreshGravity();
            if (refreshHeight == 0) refreshHeight = height;
            if (this.duration == 0) this.duration = duration;
            if (refreshGravity == 0) refreshGravity = gravity;
        } else {
            refreshHeight = height;
            refreshGravity = gravity;
            this.duration = duration;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (refreshHeader == null || !isRefreshEnable) return super.onTouchEvent(e);
        if (e.getAction() == MotionEvent.ACTION_MOVE) {
            if (isFirstMove) {
                isFirstMove = false;
                lastY = e.getRawY();
            }
            float y = e.getRawY();
            float delay = y - lastY;
            isPullUp = delay < 0;
            lastY = y;
            if (isTop() && appBarState == APP_BAR_EXPAND) {
                refreshHeader.move(delay);
                setOverScrollMode(View.OVER_SCROLL_NEVER);
                if (refreshHeader.isMove()) return false;
            }
        } else if (e.getAction() == MotionEvent.ACTION_UP) {
            setOverScrollMode(currentScrollMode);
            isFirstMove = true;
            refreshHeader.up();
        }
        return super.onTouchEvent(e);
    }

    private boolean isTop() {
        return refreshHeader.getParent() != null;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //解决嵌套CoordinatorLayout时的滑动冲突
        ViewParent parent = getParent();
        while (parent != null) {
            if (parent instanceof CoordinatorLayout) break;
            parent = parent.getParent();
        }
        if (parent == null) return;
        CoordinatorLayout layout = (CoordinatorLayout) parent;
        AppBarLayout appBarLayout = null;
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
        if (refreshHeader != null) refreshHeader.cancelAnim();
        if (loadingFooter != null) loadingFooter.loadingOver();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        appBarState = verticalOffset == 0 ? APP_BAR_EXPAND : APP_BAR_CLOSE;
    }

    private AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            wrapperAdapter.notifyDataSetChanged();
            checkEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            wrapperAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            wrapperAdapter.notifyItemRangeRemoved(positionStart, itemCount);
            checkEmpty();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            wrapperAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            wrapperAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            wrapperAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    };

    private void checkEmpty() {
        if (emptyView != null && wrapperAdapter != null) {
            if (wrapperAdapter.getItemCount() == 0) {
                emptyView.setVisibility(VISIBLE);
                this.setVisibility(GONE);
            } else {
                emptyView.setVisibility(GONE);
                this.setVisibility(VISIBLE);
            }
        }
    }

    public void setEmptyView(View view) {
        emptyView = view;
        if (wrapperAdapter != null) {
            mObserver.onChanged();
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (getAdapter() != null) {
            getAdapter().unregisterAdapterDataObserver(mObserver);
        }
        wrapperAdapter = new WrapperAdapter(adapter);
        super.setAdapter(wrapperAdapter);
        adapter.registerAdapterDataObserver(mObserver);
        mObserver.onChanged();
        //设置了加载功能时，初始化刷新头和加载尾部
        if (loadListener != null && isInitLoad()) {
            initRefresh();
            initLoading();
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
        if (manager == null || !(manager instanceof LinearLayoutManager) ||
                ((LinearLayoutManager) manager).getOrientation() != VERTICAL) {
            refreshHeader = null;
            return false;
        }
        return true;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        setDivider(layout);
    }

    private void setDivider(LayoutManager layout) {
        boolean isSetDivider = !(layout == null || !(layout instanceof LinearLayoutManager)
                || dividerColor == NO_COLOR || dividerHeight == 0);
        boolean isGridManager = layout instanceof GridLayoutManager;
        if (divider != null) removeItemDecoration(divider);
        //只对LinearLayoutManager设置分割线
        if (isSetDivider && !isGridManager) {
            LinearLayoutManager manager = (LinearLayoutManager) layout;
            if (manager.getOrientation() == LinearLayoutManager.VERTICAL) {
                divider = new XRVDivider(LinearLayoutManager.VERTICAL);
                divider.initVerticalDivider(dividerHeight, dividerColor, dividerLeft, dividerRight);
                addItemDecoration(divider);
            } else if (manager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                divider = new XRVDivider(LinearLayoutManager.HORIZONTAL);
                divider.initHorizontalDivider(dividerHeight, dividerColor);
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
        if (getAdapter() != null && isInitLoad()) {
            initRefresh();
            initLoading();
        }
    }

    private static int dip2px(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /*------------------------------------------刷新头部操作----------------------------begin------*/
    private void initRefresh() {
        //用户没有设置刷新头部时，设置默认的刷新头部，否则使用用户的刷新头
        if (refreshHeader == null) {
            refreshHeader = new SRVRefreshHeader(getContext());
        }
        refreshHeader.initHeader(refreshHeight, refreshGravity, duration);
        wrapperAdapter.addHeader(refreshHeader);
        refreshHeader.setRefreshListener(new AbsRefreshHeader.ReFreshListener() {
            @Override
            public void refresh() {
                if (loadListener != null) loadListener.refresh();
            }
        });
    }

    /**
     * 设置自己的刷新头部
     * 应在setAdapter之前调用才有效
     */
    public void setRefreshHeader(AbsRefreshHeader view) {
        refreshHeader = view;
    }

    public void addHeader(View view) {
        if (wrapperAdapter != null) {
            wrapperAdapter.addHeader(view);
        }
    }

    public void removeHeader(View view) {
        if (wrapperAdapter != null) {
            wrapperAdapter.removeHeader(view);
        }
    }

    public void refreshComplete() {
        if (refreshHeader != null) {
            isLoading = false;
            refreshHeader.refreshComplete();
        }
    }

    public void startRefresh(boolean isAnim) {
        if (refreshHeader != null && isRefreshEnable) {
            scrollToPosition(0);
            refreshHeader.startRefresh(isAnim);
        }
    }

    public void setRefreshEnable(boolean enable) {
        isRefreshEnable = enable;
    }
    /*------------------------------------------刷新头部操作----------------------------end--------*/


    /*------------------------------------------尾部操作--------------------------------begin-----*/
    private void initLoading() {
        //用户没有设置刷新头部时，设置默认的刷新头部，否则使用用户的刷新头
        if (loadingFooter == null) {
            loadingFooter = new SRVLoadFooter(getContext());
        }
        loadingFooter.initFooter();
        wrapperAdapter.setLoadFooter(loadingFooter);
        //刷新和加载只支持垂直方向的LinearLayoutManager和GridLayoutManager布局
        //这里manager的类型，在isInitLoad方法中已经做过验证，可以直接强转
        final LinearLayoutManager manager = (LinearLayoutManager) getLayoutManager();
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == SCROLL_STATE_IDLE && isLoadingEnable && isPullUp) {
                    judgeLastItem(manager.findLastVisibleItemPosition());
                }
            }
        });
    }

    /**
     * 判断是否开始加载更多，只有滑动到最后一个Item，并且当前有数据时，才会加载
     */
    private void judgeLastItem(int index) {
        int itemCount = wrapperAdapter.getItemCount() - 1;
        int dataCount = getAdapter().getItemCount();
        if (index == itemCount && dataCount != 0 && !isLoading) {
            isLoading = true;
            loadingFooter.loading();
            loadListener.loading();
        }
    }

    /**
     * 设置自己的加载尾部
     * 应在setAdapter之前调用才有效
     */
    public void setLoadingFooter(AbsLoadFooter view) {
        loadingFooter = view;
    }

    public void addFooter(View view) {
        if (wrapperAdapter != null) {
            wrapperAdapter.addFooter(view);
        }
    }

    public void removeFooter(View view) {
        if (wrapperAdapter != null) {
            wrapperAdapter.removeFooter(view);
        }
    }

    public void loadingComplete() {
        if (loadingFooter != null) {
            isLoading = false;
            loadingFooter.loadingOver();
        }
    }

    public void setLoadingEnable(boolean enable) {
        isLoadingEnable = enable;
    }

    public void loadNoMoreData() {
        if (loadingFooter != null) {
            isLoading = true;
            loadingFooter.loadingNoMoreData();
        }
    }
    /*------------------------------------------尾部操作-------------------------------end------*/


    private class WrapperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private SparseArray<View> headers = new SparseArray<>();
        private SparseArray<View> footers = new SparseArray<>();
        private int HEADER_TYPE = 1314521;
        private int FOOTER_TYPE = HEADER_TYPE * 10;
        private final int LOAD_FOOTER = HEADER_TYPE + FOOTER_TYPE;
        private ClickListener listener;
        private Adapter adapter;

        WrapperAdapter(Adapter adapter) {
            this.adapter = adapter;
            listener = new ClickListener();
        }

        Adapter getAdapter() {
            return adapter;
        }

        private boolean isHeader(int position) {
            return position < getHeaderCount();
        }

        void addHeader(View view) {
            if (view == null) return;
            checkAddView(view);
            headers.put(HEADER_TYPE++, view);
            notifyItemInserted(getHeaderCount() - 1);
        }

        void removeHeader(View view) {
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

        void addFooter(View view) {
            if (view == null) return;
            checkAddView(view);
            footers.put(FOOTER_TYPE++, view);
            int insertPosition = getHeaderCount() + getDataCount() + getFooterCount();
            //如果有加载尾部，则在尾部之前插入Item，保证加载尾部是最后一个Item
            if (footers.get(LOAD_FOOTER) != null) insertPosition -= 1;
            notifyItemInserted(insertPosition);
        }

        void setLoadFooter(View view) {
            footers.put(LOAD_FOOTER, view);
            int insertPosition = getHeaderCount() + getDataCount() + getFooterCount();
            notifyItemInserted(insertPosition);
        }

        void removeFooter(View view) {
            if (view == null) return;
            for (int i = 0; i < getFooterCount(); i++) {
                if (view == footers.valueAt(i)) {
                    footers.removeAt(i);
                    notifyItemRemoved(getHeaderCount() + getDataCount() + i);
                    break;
                }
            }
        }

        private void checkAddView(View view) {
            if (view.getParent() != null) {
                throw new IllegalStateException("The specified child already has a parent. " +
                        "You must call removeView() on the child's parent first.");
            }
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

        @SuppressWarnings("unchecked")
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (isHeader(position) || isFooter(position)) return;
            position -= getHeaderCount();
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(listener);
            adapter.onBindViewHolder(holder, position);
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

        class Holder extends ViewHolder {
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
                if (holder.getLayoutPosition() < getHeaderCount()
                        || holder.getLayoutPosition() > (getHeaderCount() + getDataCount() - 1)) {
                    StaggeredGridLayoutManager.LayoutParams p =
                            (StaggeredGridLayoutManager.LayoutParams) params;
                    p.setFullSpan(true);
                }
            }
        }
    }


    /*-----------------------------------Item的点击事件-------------------------------------*/
    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.click(v, (Integer) v.getTag());
        }
    }

    public interface ItemClickListener {
        void click(View v, int position);
    }

    private ItemClickListener clickListener;

    public void setItemClickListener(ItemClickListener listener) {
        clickListener = listener;
    }
    /*-----------------------------------Item的点击事件-------------------------------------*/


    /**
     * 设置LinearLayoutManager的分割线
     */
    private class XRVDivider extends RecyclerView.ItemDecoration {

        private int mOrientation = LinearLayoutManager.VERTICAL;
        private float dividerHeight, leftMargin, rightMargin;
        private Paint mPaint;

        XRVDivider(int orientation) {
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
        void initVerticalDivider(float height, int color, float leftMargin, float rightMargin) {
            this.dividerHeight = height;
            this.leftMargin = leftMargin;
            this.rightMargin = rightMargin;
            mPaint.setColor(color);
        }

        void initHorizontalDivider(float height, int color) {
            this.dividerHeight = height;
            mPaint.setColor(color);
        }

        /**
         * 刷新头部和加载尾部不需要分割线
         */
        private boolean isLoadView(View view) {
            return refreshHeader == view || loadingFooter == view;
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
