package com.zhongyou.meettvapplicaion.view;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by xingyuna on 2017/3/6.
 */

public class RecyclerViewTV extends RecyclerView implements PrvInterface {
    private static final int DEFAULT_SELECTION = Integer.MAX_VALUE >> 1;
    private boolean mFirstOnLayout;
    public RecyclerViewTV(Context context) {
        this(context, null);
    }

    public RecyclerViewTV(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public RecyclerViewTV(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private View mItemView;
    private boolean mSelectedItemCentered = true;
    private int mSelectedItemOffsetStart;
    private int mSelectedItemOffsetEnd;
    private int position = 0;
    private OnItemListener mOnItemListener;
    private OnItemClickListener mOnItemClickListener; // item 单击事件.
    private ItemListener mItemListener;
    private int offset = -1;

    private RecyclerViewTV.OnChildViewHolderSelectedListener mChildViewHolderSelectedListener;
    private View mCurrentCenterChildView;
    private void init(Context context) {
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setHasFixedSize(true);
        setWillNotDraw(true);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        setChildrenDrawingOrderEnabled(true);
//        //
        setClipChildren(false);
        setClipToPadding(false);
//
        setClickable(false);
        setFocusable(true);
        setFocusableInTouchMode(true);
        //
        mItemListener = new ItemListener() {
            /**
             * 子控件的点击事件
             * @param itemView
             */
            @Override
            public void onClick(View itemView) {
                if (null != mOnItemClickListener) {
                    mOnItemClickListener.onItemClick(RecyclerViewTV.this, itemView, getChildLayoutPosition(itemView));
                }
            }

            /**
             * 子控件的焦点变动事件
             * @param itemView
             * @param hasFocus
             */
            @Override
            public void onFocusChange(View itemView, boolean hasFocus) {
                if (null != mOnItemListener) {
                    if (null != itemView) {
                        mItemView = itemView; // 选中的item.
                        itemView.setSelected(hasFocus);
                        if (hasFocus) {
                            mOnItemListener.onItemSelected(RecyclerViewTV.this, itemView, getChildLayoutPosition(itemView));
                        } else {
                            mOnItemListener.onItemPreSelected(RecyclerViewTV.this, itemView, getChildLayoutPosition(itemView));
                        }
                    }
                }
            }
        };
    }

    private int getFreeWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int getFreeHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    @Override
    public void onChildAttachedToWindow(View child) {
        // 设置单击事件，修复.
        if (!child.hasOnClickListeners()) {
            child.setOnClickListener(mItemListener);
        }
        // 设置焦点事件，修复.
        if (child.getOnFocusChangeListener() == null) {
            child.setOnFocusChangeListener(mItemListener);
        }
    }

    private boolean status = true;
    private int lastSelectItem = 0;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        lastSelectItem = getSelectPostion();
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (getChildAt(lastSelectItem) == null) {
            return;
        }

    }

    @Override
    public boolean hasFocus() {

        return super.hasFocus();
    }

    @Override
    public boolean isInTouchMode() {
        // 解决4.4版本抢焦点的问题
        if (Build.VERSION.SDK_INT == 19) {
            return !(hasFocus() && !super.isInTouchMode());
        } else {
            return super.isInTouchMode();
        }
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        // 一行的选中.
        if (mChildViewHolderSelectedListener != null) {
            int pos = getPositionByView(child);
            RecyclerView.ViewHolder vh = getChildViewHolder(child);
            mChildViewHolderSelectedListener.onChildViewHolderSelected(this, vh, pos);
        }
        //
        if (null != child) {
            if (mSelectedItemCentered) {
                mSelectedItemOffsetStart = !isVertical() ? (getFreeWidth() - child.getWidth()-40) : (getFreeHeight() - child.getHeight());
                mSelectedItemOffsetStart /= 2;
                mSelectedItemOffsetEnd = mSelectedItemOffsetStart;
            }
        }
        super.requestChildFocus(child, focused);
    }

    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {

        final int parentLeft = getPaddingLeft();
        final int parentTop = getPaddingTop();
        final int parentRight = getWidth() - getPaddingRight();
        final int parentBottom = getHeight() - getPaddingBottom();

        final int childLeft = child.getLeft() + rect.left;
        final int childTop = child.getTop() + rect.top;

//        final int childLeft = child.getLeft() + rect.left - child.getScrollX();
//        final int childTop = child.getTop() + rect.top - child.getScrollY();

        final int childRight = childLeft + rect.width();
        final int childBottom = childTop + rect.height();

        final int offScreenLeft = Math.min(0, childLeft - parentLeft - mSelectedItemOffsetStart);
        final int offScreenTop = Math.min(0, childTop - parentTop - mSelectedItemOffsetStart);
        final int offScreenRight = Math.max(0, childRight - parentRight + mSelectedItemOffsetEnd);
        final int offScreenBottom = Math.max(0, childBottom - parentBottom + mSelectedItemOffsetEnd);
        final boolean canScrollHorizontal = getLayoutManager().canScrollHorizontally();
        final boolean canScrollVertical = getLayoutManager().canScrollVertically();

        // Favor the "start" layout direction over the end when bringing one side or the other
        // of a large rect into view. If we decide to bring in end because start is already
        // visible, limit the scroll such that start won't go out of bounds.
        final int dx;
        if (canScrollHorizontal) {
            if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                //右滚左
                dx = offScreenRight != 0 ? offScreenRight
                        : Math.max(offScreenLeft, childRight - parentRight);
            } else {
                //左滚右
                dx = offScreenLeft != 0 ? offScreenLeft
                        : Math.min(childLeft - parentLeft, offScreenRight);
            }
        } else {
            dx = 0;
        }

        // Favor bringing the top into view over the bottom. If top is already visible and
        // we should scroll to make bottom visible, make sure top does not go out of bounds.
        final int dy;
        if (canScrollVertical) {
            dy = offScreenTop != 0 ? offScreenTop : Math.min(childTop - parentTop, offScreenBottom);
        } else {
            dy = 0;
        }
        if (cannotScrollForwardOrBackward(isVertical() ? dy : dx)) {
            offset = -1;
        } else {
            offset = isVertical() ? dy : dx;
            if (dx != 0 || dy != 0) {
                if (immediate) {
                    scrollBy(dx, dy);
                } else {
                    smoothScrollBy(dx, dy);
                }
                return true;
            }

        }

        // 重绘是为了选中item置顶，具体请参考getChildDrawingOrder方法
        postInvalidate();

        return false;
    }

    private boolean cannotScrollForwardOrBackward(int value) {
//        return cannotScrollBackward(value) || cannotScrollForward(value);
        return false;
    }

    /**
     * 判断第一个位置，没有移动.
     * getStartWithPadding --> return (mIsVertical ? getPaddingTop() : getPaddingLeft());
     */
    public boolean cannotScrollBackward(int delta) {
        return (getFirstVisiblePosition() == 0 && delta <= 0);
    }

    /**
     * 判断是否达到了最后一个位置，没有再移动了.
     * getEndWithPadding -->  mIsVertical ?  (getHeight() - getPaddingBottom()) :
     * (getWidth() - getPaddingRight());
     */
    public boolean cannotScrollForward(int delta) {
        return ((getFirstVisiblePosition() + getLayoutManager().getChildCount()) == getLayoutManager().getItemCount()) && (delta >= 0);
    }

    @Override
    public int getBaseline() {
        return offset;
    }

    @Override
    public void smoothScrollBy(int dx, int dy) {
        // ViewFlinger --> smoothScrollBy(int dx, int dy, int duration, Interpolator interpolator)
        //  ViewFlinger --> run --> hresult = mLayout.scrollHorizontallyBy(dx, mRecycler, mState);
        // LinearLayoutManager --> scrollBy --> mOrientationHelper.offsetChildren(-scrolled);
        super.smoothScrollBy(dx, dy);
    }

    public int getSelectedItemOffsetStart() {
        return mSelectedItemOffsetStart;
    }

    public int getSelectedItemOffsetEnd() {
        return mSelectedItemOffsetEnd;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
    }

    /**
     * 判断是垂直，还是横向.
     */
    private boolean isVertical() {
        LinearLayoutManager layout = (LinearLayoutManager) getLayoutManager();
        return layout.getOrientation() == LinearLayoutManager.VERTICAL;
    }

    /**
     * 设置选中的Item距离开始或结束的偏移量；
     * 与滚动方向有关；
     * 与setSelectedItemAtCentered()方法二选一
     *
     * @param offsetStart
     * @param offsetEnd   从结尾到你移动的位置.
     */
    public void setSelectedItemOffset(int offsetStart, int offsetEnd) {
        setSelectedItemAtCentered(false);
        this.mSelectedItemOffsetStart = offsetStart;
        this.mSelectedItemOffsetEnd = offsetEnd;
    }

    /**
     * 设置选中的Item居中；
     * 与setSelectedItemOffset()方法二选一
     *
     * @param isCentered
     */
    public void setSelectedItemAtCentered(boolean isCentered) {
        this.mSelectedItemCentered = isCentered;
    }

    public View getSelectView() {
        if (mItemView == null)
            mItemView = getFocusedChild();
        return mItemView;
    }

    public int getSelectPostion() {
        View view = getSelectView();
        if (view != null)
            return getPositionByView(view);
        return -1;
    }
    public int getFocusPostion() {
        View view = getFocusedChild();
        if (view != null)
            return getPositionByView(view);
        return -1;
    }
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        View view = getFocusedChild();
        if (null != view) {
            position = getChildAdapterPosition(view) - getFirstVisiblePosition();
            if (position < 0) {
                return i;
            } else {
                if (i == childCount - 1) {//这是最后一个需要刷新的item
                    if (position > i) {
                        position = i;
                    }
                    return position;
                }
                if (i == position) {//这是原本要在最后一个刷新的item
                    return childCount - 1;
                }
            }
        }
        return i;
    }

    public int getFirstVisiblePosition() {
        if (getChildCount() == 0)
            return 0;
        else
            return getChildLayoutPosition(getChildAt(0));
    }

    public int getLastVisiblePosition() {
        final int childCount = getChildCount();
        if (childCount == 0)
            return 0;
        else
            return getChildLayoutPosition(getChildAt(childCount - 1));
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (state == SCROLL_STATE_IDLE) {
            offset = -1;
            final View focuse = getFocusedChild();
            if (null != mOnItemListener && null != focuse) {
                mOnItemListener.onReviseFocusFollow(this, focuse, getChildLayoutPosition(focuse));
            }
        }
        super.onScrollStateChanged(state);
    }

    @Override
    public void setOnLoadMoreComplete() {
        isLoading = false;
    }
    @Override
    public void setOnLoadMoreUpComplete() {
        isLoadUping = false;
    }
    @Override
    public void setPagingableListener(PagingableListener pagingableListener) {
        this.mPagingableListener = pagingableListener;
    }

    @Override
    public void setPagingableUpListener(PagingableUpListener pagingableListener) {
        this.mPagingableUpListener = pagingableListener;
    }
    private interface ItemListener extends OnClickListener, OnFocusChangeListener {
    }

    public interface OnItemListener {
        void onItemPreSelected(RecyclerViewTV parent, View itemView, int position);

        void onItemSelected(RecyclerViewTV parent, View itemView, int position);

        void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position);
    }

    public interface OnChildViewHolderSelectedListener {
        public void onChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder vh,
                                              int position);
    }

    public interface OnItemClickListener {
        void onItemClick(RecyclerViewTV parent, View itemView, int position);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    /**
     * 控制焦点高亮问题.
     * 2016.08.29
     */
    public void setOnChildViewHolderSelectedListener(OnChildViewHolderSelectedListener listener) {
        mChildViewHolderSelectedListener = listener;
    }

    private int getPositionByView(View view) {
        if (view == null) {
            return NO_POSITION;
        }
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        if (params == null || params.isItemRemoved()) {
            // when item is removed, the position value can be any value.
            return NO_POSITION;
        }
        return params.getViewPosition();
    }

    /////////////////// 按键加载更多 start start start //////////////////////////

    private RecyclerViewTV.PagingableListener mPagingableListener;
    private RecyclerViewTV.PagingableUpListener mPagingableUpListener;
    private boolean isLoading = false;
    private boolean isLoadUping = false;

    public interface PagingableListener {
        void onLoadMoreItems();
    }
    public interface PagingableUpListener {
        void onLoadMoreItems();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (action == KeyEvent.ACTION_UP) {
            if (!isHorizontalLayoutManger() && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                // 垂直布局向下按键.
                exeuteKeyEvent();
            } else if (isHorizontalLayoutManger() && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                // 横向布局向右按键.
                exeuteKeyEvent();
            }else if(!isHorizontalLayoutManger() && keyCode == KeyEvent.KEYCODE_DPAD_UP){
                exeuteUpKeyEvent();
            }
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && DefaultValue) {
            getChildAt(0).requestFocus();
            Log.v("dispatchKeyEvent", "getChildAt(0)==" + getChildAt(0));
            DefaultValue = false;
        }

        return super.dispatchKeyEvent(event);
    }

    boolean DefaultValue = false;

    public void setDefaultValue(boolean value) {
        DefaultValue = value;
    }
    private boolean exeuteUpKeyEvent() {

        if (getLayoutManager() != null) {
            int totalItemCount = getLayoutManager().getItemCount();
            int lastVisibleItem = findLastVisibleItemPosition();
            int lastComVisiPos = findLastCompletelyVisibleItemPosition();
            int visibleItemCount = getChildCount();
            int firstVisibleItem = findFirstVisibleItemPosition();
            // 判断是否显示最底了.
            Log.v("nanahaobang","=="+getFocusPostion());
             if(!isLoadUping && getFocusPostion() == 0){
                isLoadUping = true;
                if (mPagingableUpListener != null) {
                    mPagingableUpListener.onLoadMoreItems();
                    return true;
                }
            }
        }

        return false;
    }
    private boolean exeuteKeyEvent() {

        if (getLayoutManager() != null) {
            int totalItemCount = getLayoutManager().getItemCount();
            int lastVisibleItem = findLastVisibleItemPosition();
            int lastComVisiPos = findLastCompletelyVisibleItemPosition();
            int visibleItemCount = getChildCount();
            int firstVisibleItem = findFirstVisibleItemPosition();
            // 判断是否显示最底了.
            if (!isLoading && totalItemCount - visibleItemCount <= firstVisibleItem) {
                isLoading = true;
                if (mPagingableListener != null) {
                    mPagingableListener.onLoadMoreItems();
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 判断是否为横向布局
     */
    private boolean isHorizontalLayoutManger() {
        LayoutManager lm = getLayoutManager();
        if (lm != null) {
            if (lm instanceof LinearLayoutManager) {
                LinearLayoutManager llm = (LinearLayoutManager) lm;
                return LinearLayoutManager.HORIZONTAL == llm.getOrientation();
            }
            if (lm instanceof GridLayoutManager) {
                GridLayoutManager glm = (GridLayoutManager) lm;
                return GridLayoutManager.HORIZONTAL == glm.getOrientation();
            }
        }
        return false;
    }

    /**
     * 最后的位置.
     */
    public int findLastVisibleItemPosition() {
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager != null) {
            if (layoutManager instanceof LinearLayoutManager) {
                return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
            if (layoutManager instanceof GridLayoutManager) {
                return ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
        }
        return RecyclerView.NO_POSITION;
    }

    /**
     * 滑动到底部.
     */
    public int findLastCompletelyVisibleItemPosition() {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager != null) {
            if (layoutManager instanceof LinearLayoutManager) {
                return ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
            }
            if (layoutManager instanceof GridLayoutManager) {
                return ((GridLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
            }
        }
        return RecyclerView.NO_POSITION;
    }

    public int findFirstVisibleItemPosition() {
        LayoutManager lm = getLayoutManager();
        if (lm != null) {
            if (lm instanceof LinearLayoutManager) {
                return ((LinearLayoutManager) lm).findFirstVisibleItemPosition();
            }
            if (lm instanceof GridLayoutManager) {
                return ((GridLayoutManager) lm).findFirstVisibleItemPosition();
            }
        }
        return RecyclerView.NO_POSITION;
    }

    /////////////////// 按键加载更多 end end end //////////////////////////

    /////////////////// 按键拖动 Item start start start ///////////////////////

    private final ArrayList<OnItemKeyListener> mOnItemKeyListeners =
            new ArrayList<OnItemKeyListener>();

    public static interface OnItemKeyListener {
        public boolean dispatchKeyEvent(KeyEvent event);
    }

    public void addOnItemKeyListener(OnItemKeyListener listener) {
        mOnItemKeyListeners.add(listener);
    }

    public void removeOnItemKeyListener(OnItemKeyListener listener) {
        mOnItemKeyListeners.remove(listener);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return super.onInterceptTouchEvent(e);
    }

    ////////////////// 按键拖动 Item end end end /////////////////////////

    /**
     * 设置默认选中.
     */

    public void setDefaultSelect(int pos) {
        GeneralAdapter.ViewHolder vh = (GeneralAdapter.ViewHolder) findViewHolderForAdapterPosition(pos);
        requestFocusFromTouch();
        if (vh != null)
            vh.itemView.requestFocus();
    }

    /**
     * 延时选中默认.
     */
    public void setDelayDefaultSelect(int pos, int time) {
        Message msg = mHandler.obtainMessage();
        msg.arg1 = pos;
        mHandler.sendMessageDelayed(msg, time);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int pos = msg.arg1;
            setDefaultSelect(pos);
        }
    };

//    public View findViewAtCenter() {
//        if (getLayoutManager().canScrollVertically()) {
//            return findViewAt(0, getHeight() / 2);
//        } else if (getLayoutManager().canScrollHorizontally()) {
//            return findViewAt(getWidth() / 2, 0);
//        }
//        return null;
//    }
//
//    public View findViewAt(int x, int y) {
//        final int count = getChildCount();
//        for (int i = 0; i < count; ++i) {
//            final View v = getChildAt(i);
//            final int x0 = v.getLeft();
//            final int y0 = v.getTop();
//            final int x1 = v.getWidth() + x0;
//            final int y1 = v.getHeight() + y0;
//            if (x >= x0 && x <= x1 && y >= y0 && y <= y1) {
//                return v;
//            }
//        }
//        return null;
//    }
//
//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, t, r, b);
////            scrollToPosition(DEFAULT_SELECTION);
//        if (!mFirstOnLayout) {
//            mFirstOnLayout = true;
//            mPostHandler.sendEmptyMessage(0);
//        }
//            mCurrentCenterChildView = findViewAtCenter();
//            smoothScrollToView(mCurrentCenterChildView);
//
//    }
//
//    public void smoothScrollToView(View v) {
//        int distance = 0;
//        if (getLayoutManager() instanceof FocusFixedLinearLayoutManager) {
//
//            if (getLayoutManager().canScrollVertically()) {
//                Log.v("smoothScrollToView","12222222222222222222222222");
//                final float y = v.getY() + v.getHeight() * 0.5f;
//                final float halfHeight = getHeight() * 0.5f;
//                distance = (int) (y - halfHeight);
//            } else if (getLayoutManager().canScrollHorizontally()) {
//                Log.v("smoothScrollToView","1211111111111111111111");
//                final float x = v.getX() + v.getWidth() * 0.5f;
//                final float halfWidth = getWidth() * 0.5f;
//                distance = (int) (x - halfWidth);
//            }
//
//        } else
//            throw new IllegalArgumentException("CircleRecyclerView just support T extend LinearLayoutManager!");
//        smoothScrollBy(distance,distance);
//    }
//
//    private Handler mPostHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            Log.v("guideractivity","DEFAULT_SELECTION=="+DEFAULT_SELECTION);
//            scrollToPosition(100);
//        }
//    };
}
