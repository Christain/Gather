package com.gather.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.gather.android.activity.Message.MessageItem;

public class ListViewCompat extends ListView {

	private static final String TAG = "ListViewCompat";

	private SlideView mFocusedItemView;

	private static final int TOUCH_STATE_NONE = 0;
	private static final int TOUCH_STATE_X = 1;
	private static final int TOUCH_STATE_Y = 2;

	private int MAX_Y = 5;
	private int MAX_X = 3;
	private float mDownX;
	private float mDownY;
	private int mTouchState;

	public ListViewCompat(Context context) {
		super(context);
		init();
	}

	public ListViewCompat(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ListViewCompat(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void shrinkListItem(int position) {
		View item = getChildAt(position);

		if (item != null) {
			try {
				((SlideView) item).shrink();
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		}
	}

	private void init() {
		MAX_X = dp2px(MAX_X);
		MAX_Y = dp2px(MAX_Y);
		mTouchState = TOUCH_STATE_NONE;
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			int x = (int) event.getX();
			int y = (int) event.getY();
			int position = pointToPosition(x, y);
//			Log.e(TAG, "postion=" + position);
			if (position != INVALID_POSITION) {
				MessageItem data = (MessageItem) getItemAtPosition(position);
				mFocusedItemView = data.slideView;
//				Log.e(TAG, "FocusedItemView=" + mFocusedItemView);
			}
			mDownX = event.getX();
			mDownY = event.getY();
			mTouchState = TOUCH_STATE_NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			float dy = Math.abs((event.getY() - mDownY));
			float dx = Math.abs((event.getX() - mDownX));
			if (mTouchState == TOUCH_STATE_X) {
				if (mFocusedItemView != null) {
					mFocusedItemView.onRequireTouchEvent(event);
				}
				return true;
			} else {
				if (mTouchState != TOUCH_STATE_X && Math.abs(dy) > MAX_Y) {
					mTouchState = TOUCH_STATE_Y;
				}
				if (mTouchState != TOUCH_STATE_Y && dx > MAX_X) {
					mTouchState = TOUCH_STATE_X;
				}
			}
			break;
		}

		if (mFocusedItemView != null) {
			mFocusedItemView.onRequireTouchEvent(event);
		}

		return super.onTouchEvent(event);
	}

}
