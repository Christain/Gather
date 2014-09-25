package com.gather.android.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.gather.android.application.GatherApplication;

public class InfiniteLoopViewPager extends MyViewPager {
	
	private GatherApplication mApplication;
	private Handler handler;
	// 滑动距离及坐标
//	private float xDistance, yDistance, xLast, yLast;
//	private int totalNum;
	
	public InfiniteLoopViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		mApplication = (GatherApplication)context.getApplicationContext();
	}

	public InfiniteLoopViewPager(Context context) {
		super(context);
		mApplication = (GatherApplication)context.getApplicationContext();
	}

	@Override
	public void setAdapter(MyPagerAdapter adapter) {
		super.setAdapter(adapter);
		//设置当前展示的位置
		setCurrentItem(0);
	}
	
	public void setInfinateAdapter(Handler handler,MyPagerAdapter adapter){
		this.handler = handler;
		setAdapter(adapter);
	}
	
	@Override
	public void setCurrentItem(int item) {
		item = getOffsetAmount() + (item % getAdapter().getCount());
		super.setCurrentItem(item);
	}
	/**
	 * 从0开始向右(负向滑动)可以滑动的次数
	 * @return
	 */
	private int getOffsetAmount() {
		if (getAdapter() instanceof InfiniteLoopViewPagerAdapter) {
			InfiniteLoopViewPagerAdapter infiniteAdapter = (InfiniteLoopViewPagerAdapter) getAdapter();
			// 允许向前滚动400000次
//			totalNum = infiniteAdapter.getRealCount() * 100000;
			return infiniteAdapter.getRealCount() * 100000;
		} else {
//			totalNum = 0;
			return 0;
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			mApplication.isRun = false;
			mApplication.isDown = true;
			handler.removeCallbacksAndMessages(null);
//			System.out.println("InfiniteLoopViewPager  dispatchTouchEvent =====>>> ACTION_DOWN");
		} else if (action == MotionEvent.ACTION_UP) {
			mApplication.isRun = true;
			mApplication.isDown = false;
			handler.removeCallbacksAndMessages(null);
			handler.sendEmptyMessage(1);
//			System.out.println("InfiniteLoopViewPager  dispatchTouchEvent =====>>> ACTION_UP");
		}
		return super.dispatchTouchEvent(ev);
	}
	
	@Override
	public void setOffscreenPageLimit(int limit) {
		super.setOffscreenPageLimit(limit);
	}
}
