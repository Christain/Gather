package com.gather.android.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.utils.TimeUtil;

@SuppressLint("ClickableViewAccessibility")
public class XListView extends ListView implements OnScrollListener {

	private float mLastY = -1;
	private Scroller mScroller;
	private OnScrollListener mScrollListener;
//	private ChangeListViewBgListener mChangeListViewBgListener;
	private IXListViewListener mListViewListener;

	private XListViewHeader mHeaderView;
	private RelativeLayout mHeaderViewContent;
	private TextView mHeaderTimeView, mHeaderTitleView;
	private int mHeaderViewHeight, mLoadMoreNeedHigh;
	private boolean mEnablePullRefresh = true;
	private boolean mPullRefreshing = false;
	private boolean isRefresh = false; // 判断是上拉还是下拉
	private boolean isFirstMove = true;

	private XListViewFooter mFooterView;
	private RelativeLayout mFooterViewContent;
	private TextView mFooterTitleView;
	private boolean mEnablePullLoad;
	private boolean mPullLoading;
	private boolean mIsFooterReady = false;

	private int mTotalItemCount;
	private boolean isLoadmore = true;
	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;
	private final static int SCROLLBACK_FOOTER = 1;

	private final static int SCROLL_DURATION = 400;// 回弹的时间
	private final static int PULL_LOAD_MORE_DELTA = 30; // 上拉多少高度加载更多
	private final static float OFFSET_RADIO = 2.5f;
	static final int PULL_TO_REFRESH = 0x0;
	static final int RELEASE_TO_REFRESH = 0x1;
	static final int REFRESHING = 0x2;
	static final int MANUAL_REFRESHING = 0x3;
	private long time;
	
	// 滑动距离及坐标
	private float xDistance, yDistance, xLast, yLast;

	/**
	 * @param context
	 */
	public XListView(Context context) {
		super(context);
		initWithContext(context);
	}

	public XListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public XListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	private void initWithContext(Context context) {
		mScroller = new Scroller(context, new DecelerateInterpolator());
		super.setOnScrollListener(this);

		mHeaderView = new XListViewHeader(context);
		mHeaderViewContent = (RelativeLayout) mHeaderView.findViewById(R.id.xlistview_header_content);
		mHeaderTitleView = (TextView) mHeaderView.findViewById(R.id.xlistview_header_hint_textview);
		mHeaderTimeView = (TextView) mHeaderView.findViewById(R.id.xlistview_header_time);
		addHeaderView(mHeaderView);

		mFooterView = new XListViewFooter(context);
		mFooterViewContent = (RelativeLayout) mFooterView.findViewById(R.id.xlistview_footer_content);
		mFooterTitleView = (TextView) mFooterView.findViewById(R.id.xlistview_footer_hint_textview);
		if (mIsFooterReady == false) {
			mIsFooterReady = true;
			addFooterView(mFooterView);
		}

		mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			public void onGlobalLayout() {
				mHeaderViewHeight = mHeaderViewContent.getHeight();
				getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
		checkSDK();
	}

	public void setTitle(String msg) {
		mHeaderTitleView.setText(msg);
	}

	public TextView getHeaderTitle() {
		return mHeaderTitleView;
	}

	public TextView getHeaderTime() {
		return mHeaderTimeView;
	}

	public RelativeLayout getFooterLayout() {
		return mFooterViewContent;
	}

	public TextView getFooterTitle() {
		return mFooterTitleView;
	}

	public void setLoadMoreNeedHigh(int high) {
		mLoadMoreNeedHigh = high;
	}

	public void setFooterImageView(String msg, int resource) {
		mFooterView.setImageView(msg, resource);
	}

	private void checkSDK() {// 去掉顶部或底部的黄色
		final int apiLevel = Build.VERSION.SDK_INT;
		if (apiLevel >= 9) {
			setOverScrollMode(View.OVER_SCROLL_NEVER);
		}
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		// make sure XListViewFooter is the last footer view, and only add once.
		if (time != 0) {
			setRefreshTime("最近更新: " + TimeUtil.getRefreshFormatedTime(time));
		} else {
			setRefreshTime("最近更新: " + "刚刚");
		}
		time = System.currentTimeMillis();
		super.setAdapter(adapter);
	}

	public void setText(String msg) {
		mFooterView.setMessage(msg);
	}

	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh) {
			mHeaderViewContent.setVisibility(View.INVISIBLE);
		} else {
			mHeaderViewContent.setVisibility(View.VISIBLE);
		}
	}

	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			mFooterView.hide();
			mFooterTitleView.setOnClickListener(null);
		} else {
			mPullLoading = false;
			mFooterView.show();
			mFooterView.setState(XListViewFooter.STATE_LOADING);
			mFooterTitleView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}

	/**
	 * stop refresh, reset header view.
	 */
	public void stopRefresh() {
		if (mPullRefreshing == true) {
			mPullRefreshing = false;
			resetHeaderHeight();
			if (time != 0) {
				setRefreshTime("最近更新: " + TimeUtil.getRefreshFormatedTime(time));
			} else {
				setRefreshTime("最近更新: " + "刚刚");
			}
			time = System.currentTimeMillis();
		}
	}

	public void stopLoadMore() {
		if (mPullLoading == true) {
			mPullLoading = false;
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
		}
	}

	/**
	 * set last refresh time
	 * 
	 * @param time
	 */
	public void setRefreshTime(String time) {
		mHeaderTimeView.setText(time);
	}

	// private void invokeOnScrolling() {
	// if (mScrollListener instanceof OnXScrollListener) {
	// OnXScrollListener l = (OnXScrollListener) mScrollListener;
	// l.onXScrolling(this);
	// }
	// }

	private void updateHeaderHeight(float delta) {
//		if (mChangeListViewBgListener != null) {
//			mChangeListViewBgListener.refreshBackground();
//		}
		mHeaderView.setVisiableHeight((int) delta + mHeaderView.getVisiableHeight());
		if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
			if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
				mHeaderView.setState(XListViewHeader.STATE_READY);
			} else {
				mHeaderView.setState(XListViewHeader.STATE_NORMAL);
			}
		}
		setSelection(0);
	}

	/**
	 * reset header view's height.
	 */
	private void resetHeaderHeight() {
		int height = mHeaderView.getVisiableHeight();
		if (height == 0) // not visible.
			return;
		// refreshing and header isn't shown fully. do nothing.
		if (mPullRefreshing && height <= mHeaderViewHeight) {
			return;
		}
		int finalHeight = 0; // default: scroll back to dismiss header.
		// is refreshing, just scroll back to show all the header.
		if (mPullRefreshing && height > mHeaderViewHeight) {
			finalHeight = mHeaderViewHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);
		invalidate();
	}

	private void updateFooterHeight(float delta) {
//		if (mChangeListViewBgListener != null) {
//			mChangeListViewBgListener.loadmoreBackground();
//		}
		int height = mFooterView.getBottomMargin() + (int) delta;
		if (mEnablePullLoad && !mPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) {
				mFooterView.setState(XListViewFooter.STATE_READY);
			}
			// else {
			// mFooterView.setState(XListViewFooter.STATE_NORMAL);
			// }
		}
		mFooterView.setBottomMargin(height);
	}

	private void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin, SCROLL_DURATION);
			invalidate();
		}
	}

	private void startLoadMore() {
		mPullLoading = true;
		mFooterView.setState(XListViewFooter.STATE_LOADING);
		if (mListViewListener != null) {
			mListViewListener.onLoadMore();
		}
	}
	
	/**
	 * 判断是左右滑还是上下滑，如果是左右滑，则不执行上下滑动
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()& MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			xLast = ev.getX();
			yLast = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();

			xDistance += Math.abs(curX - xLast);
			yDistance += Math.abs(curY - yLast);
			xLast = curX;
			yLast = curY;

			if (xDistance > yDistance) {
				return false;
			}
			break;
		}

		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
//		if (mLastY == -1) {
//			mLastY = ev.getRawY();
//		}
		switch (ev.getAction()& MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			if (mLastY == -1) {
				mLastY = ev.getRawY();
				break;
			}
			final float deltaY = ev.getRawY()  - mLastY;
			mLastY = ev.getRawY();
			if (getFirstVisiblePosition() == 0 && (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
				isRefresh = true;
				up(deltaY);
			} else if (getLastVisiblePosition() == mTotalItemCount - 1 && (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
				if (isFirstMove) {
					int[] location = new int[2];
					mFooterViewContent.getLocationOnScreen(location);
					int y = location[1];
					if (y == mLoadMoreNeedHigh - mFooterViewContent.getMeasuredHeight()) {
						isFirstMove = false;
						if (isLoadmore) {
							isRefresh = false;
							down(deltaY);
						}
					}
				} else {
					if (isLoadmore) {
						isRefresh = false;
						down(deltaY);
					}
				}
			}
			if (getLastVisiblePosition() < mTotalItemCount - 1) {
				mFooterView.setBottomMargin(0);
			}
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mLastY = -1;
			break;
		case MotionEvent.ACTION_UP:
			mLastY = -1; // reset
			isFirstMove = true;
			if (isRefresh) {
				if (getFirstVisiblePosition() == 0) {
					refush();
				}
			} else {
				if (getLastVisiblePosition() == mTotalItemCount - 1) {
					loadmore();
				}
			}
			break;
		}
		return super.onTouchEvent(ev);
//		return false;
	}

	public void up(float deltaY) {
		updateHeaderHeight(deltaY / OFFSET_RADIO);
	}

	public void down(float deltaY) {
		updateFooterHeight(-deltaY / OFFSET_RADIO);
	}

	public void refush() {
		if (mEnablePullRefresh && mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
			mPullRefreshing = true;
			mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
			if (mListViewListener != null) {
				mListViewListener.onRefresh();
			}
		}
		resetHeaderHeight();
	}

	public void loadmore() {
		if (mEnablePullLoad && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA && !mFooterView.getMessageStauts()) {
			startLoadMore();
		}
		resetFooterHeight();
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				mHeaderView.setVisiableHeight(mScroller.getCurrY());
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}
			postInvalidate();
		}
		super.computeScroll();
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		mTotalItemCount = totalItemCount;
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}

	public void setXListViewListener(IXListViewListener l) {
		mListViewListener = l;
	}

	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	public interface IXListViewListener {
		public void onRefresh();

		public void onLoadMore();
	}

//	public interface ChangeListViewBgListener { // 为了改变刷新时ListView背景的颜色
//		public void refreshBackground();
//
//		public void loadmoreBackground();
//	}
//
//	public void setChangeListViewBgListener(ChangeListViewBgListener l) {
//		mChangeListViewBgListener = l;
//	}

	public void onClickRefush() {
		up((int)getResources().getDimension(R.dimen.header_height) * OFFSET_RADIO);
		mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
		mFooterView.setMessage("");
		mPullRefreshing = true;
		resetHeaderHeight();
	}

	// 关闭下面的加载更多
	public void closeLoadMore() {
		isLoadmore = false;
		mFooterView.setVisibility(View.GONE);
	}

	// 下拉提示加载更多
	public void pullLoadMore() {
		mHeaderView.pullLoadMore();
	}

	/**
	 * stop refresh, reset header view.
	 */
	public void stopLoadMoreMessageBox() {
		mPullLoading = false;
		mFooterView.setState(XListViewFooter.STATE_NORMAL);
	}
}
