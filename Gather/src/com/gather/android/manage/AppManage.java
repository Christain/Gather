package com.gather.android.manage;

import java.util.Stack;

import android.app.ActivityManager;
import android.content.Context;

import com.gather.android.baseclass.BaseActivity;
import com.gather.android.widget.swipeback.SwipeBackActivity;

public class AppManage {
	private Stack<BaseActivity> activities;
	private Stack<SwipeBackActivity> swipeActivities;
	private volatile static AppManage instance;

	public static AppManage getInstance() {
		if (instance == null) {
			synchronized (AppManage.class) {
				if (instance == null) {
					instance = new AppManage();
				}
			}
		}
		return instance;
	}

	private AppManage() {
		activities = new Stack<BaseActivity>();
		swipeActivities = new Stack<SwipeBackActivity>();
	}

	public void addActivity(BaseActivity activity) {
		if (activity != null) {
			activities.add(activity);
		}
	}
	
	public void addFragmentActivity(SwipeBackActivity fragment){
		if (fragment != null) {
			swipeActivities.add(fragment);
		}
	}

	public void removeActivity(BaseActivity activity) {
		if (activity != null) {
			activities.remove(activity);
		}
	}

	public void finishActivity(BaseActivity activity) {
		if (activity != null) {
			activities.remove(activity);
		}
		activity.finishActivity();
		activity = null;
	}
	
	public void finishFragment(SwipeBackActivity fragment) {
		if (fragment != null) {
			swipeActivities.remove(fragment);
		}
		fragment.finishActivity();
		fragment = null;
	}

	public BaseActivity getCurrentActivity() {
		BaseActivity activity = activities.lastElement();
		return activity;
	}
	
	public SwipeBackActivity getCurrentFragment(){
		SwipeBackActivity fragmentActivity = swipeActivities.lastElement();
		return fragmentActivity;
	}

	public void exit(Context context) {
		try {
			for (BaseActivity activity : activities) {
				if (activity != null) {
					activity.finishActivity();
				}
			}
			for (SwipeBackActivity fragment : swipeActivities) {
				if (fragment != null) {
					fragment.finish();
				}
			}
			activities.clear();
			swipeActivities.clear();
			ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			manager.restartPackage(context.getPackageName());
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void clearEverything(Context context){
		for (BaseActivity activity : activities) {
			if (activity != null) {
				activity.finishActivity();
			}
		}
		for (SwipeBackActivity fragment : swipeActivities) {
			if (fragment != null) {
				fragment.finish();
			}
		}
		activities.clear();
		swipeActivities.clear();
	}

}
