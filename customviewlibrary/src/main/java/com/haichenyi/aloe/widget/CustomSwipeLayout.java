package com.haichenyi.aloe.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.haichenyi.aloe.interfaces.LoadMoreInterface;
import com.haichenyi.aloe.tools.ToastUtils;


/**
 * @Title: CustomSwipeLayout
 * @Description: 自定义SwipeRefreshLayout，带上拉加载
 * 完成ListView的上拉加载实现，RecyclerView的逻辑实现了，功能还没有
 * @Author: wz
 * @Date: 2018/5/25
 * @Version: V1.0
 */
public class CustomSwipeLayout extends SwipeRefreshLayout {
    private View loadMore;
    /**
     * the target of the gesture
     */
    private View mTarget;
    /**
     * 是否在底部
     */
    private boolean isEnd = false;
    /**
     * 能否上拉加载
     */
    private boolean isEnableLoadMore = false;
    /**
     * 是否正在上拉
     */
    private boolean isPullUp = false;
    private boolean isLoading = false;
    private LoadMoreInterface moreListener;

    public CustomSwipeLayout(@NonNull Context context) {
        super(context);
    }

    public CustomSwipeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (getChildCount() == 0) {
            return;
        }
        if (mTarget == null) {
            ensureTarget();
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() == 0) {
            return;
        }
        if (mTarget == null) {
            ensureTarget();
        }
    }

    private float startY, currentY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 移动的起点
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                currentY = ev.getY();
                isPullUpIng(startY - currentY > 0);
                break;
            case MotionEvent.ACTION_UP:
                isPullUpIng(false);
                break;
            default:
        }
        return super.dispatchTouchEvent(ev);
    }

    private void addLoadMoreView() {
        if (mTarget != null && loadMore != null) {
            if (mTarget instanceof RecyclerView) {
                ToastUtils.showTipMsg("添加脚布局");
                RecyclerView.Adapter adapter = ((RecyclerView) mTarget).getAdapter();
            } else if (mTarget instanceof ListView) {
                ((ListView) mTarget).addFooterView(loadMore);
                isLoading = true;
                //禁止下拉刷新
                setEnabled(false);
            }
            if (moreListener != null) {
                moreListener.loadMore();
            }
        }
    }

    /**
     * 是否满足上拉加载的条件
     *
     * @return boolean
     */
    private boolean canLoad() {
        return isEnableLoadMore && isEnd && isPullUp && !isLoading;
    }

    /**
     * 是否正在上拉
     *
     * @param pullUp 是否正在上拉
     * @return boolean
     */
    private boolean isPullUpIng(boolean pullUp) {
        return isPullUp = pullUp;
    }

    /**
     * 获取子view，并添加滑动监听
     */
    private void ensureTarget() {
        if (null == mTarget) {
            for (int i = 0; i < getChildCount(); i++) {
                View view = getChildAt(i);
                if (view instanceof RecyclerView) {
                    mTarget = view;
                    addRecyclerScrollListener((RecyclerView) (mTarget));
                    break;
                } else if (view instanceof ListView) {
                    mTarget = view;
                    addListViewScrollListener((ListView) (mTarget));
                    break;
                }
            }
        }
    }

    private void addListViewScrollListener(ListView listView) {
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                isEnd = isListViewToEnd(view);
                if (canLoad() && !isRefreshing()) {
                    addLoadMoreView();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    /**
     * 判断listView是否达到底部
     *
     * @param listView listView
     * @return boolean
     */
    private boolean isListViewToEnd(final AbsListView listView) {
        boolean result = false;
        if (listView.getLastVisiblePosition() == (listView.getCount() - 1)) {
            final View bottomChildView = listView.getChildAt(listView.getLastVisiblePosition() - listView.getFirstVisiblePosition());
            result = (listView.getHeight() >= bottomChildView.getBottom());
        }
        return result;
    }

    private void addRecyclerScrollListener(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                isEnd = isRecyclerToEnd(recyclerView);
                if (canLoad() && !isRefreshing()) {
                    addLoadMoreView();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    /**
     * 判断recyclerView是否到达底部
     *
     * @param recyclerView recyclerView
     * @return boolean
     */
    private boolean isRecyclerToEnd(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        //得到当前显示的最后一个item的view
        View lastChildView = layoutManager.getChildAt(layoutManager.getChildCount() - 1);
        //得到lastChildView的bottom坐标值
        int lastChildBottom = lastChildView.getBottom();
        //得到RecyclerView的底部坐标减去底部padding值，也就是显示内容最底部的坐标
        int recyclerBottom = recyclerView.getBottom() - recyclerView.getPaddingBottom();
        //通过这个lastChildView得到这个view当前的position值
        int lastPosition = layoutManager.getPosition(lastChildView);
        //判断lastChildView的bottom值跟recyclerBottom
        //判断lastPosition是不是最后一个position
        //如果两个条件都满足则说明是真正的滑动到了底部
        return (lastChildBottom - recyclerBottom) < 3 && lastPosition == layoutManager.getItemCount() - 1;
    }

    public void setEnableLoadMore(boolean enableLoadMore) {
        isEnableLoadMore = enableLoadMore;
    }

    public void setLoadMoreListener(LoadMoreInterface moreListener) {
        this.moreListener = moreListener;
    }

    public void onLoadMoreFinish() {
        if (mTarget != null && loadMore != null) {
            if (mTarget instanceof RecyclerView) {
                RecyclerView.Adapter adapter = ((RecyclerView) mTarget).getAdapter();
            } else if (mTarget instanceof ListView) {
                ((ListView) mTarget).removeFooterView(loadMore);
            }
        }
        isLoading = false;
        //开启下拉刷新
        setEnabled(true);
    }

    public void setLoadCustomView(View view) {
        if (view != null) {
            loadMore = view;
        }
    }
}
