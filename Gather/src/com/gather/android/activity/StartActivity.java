package com.gather.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.gather.android.R;
import com.gather.android.application.GatherApplication;
import com.gather.android.constant.Constant;
import com.gather.android.manage.ExceptionManage;
import com.gather.android.manage.PhoneManage;
import com.gather.android.preference.AppPreference;

public class StartActivity extends Activity implements Runnable{
	
	private ImageView ivStartBg;
	private static final long START_DURATION = 1000;// 启动页持续时间
	private static final int START_PROGRESS_OVER = 12;
	private GatherApplication app;
	private boolean needLogin = false, needGuide;
	private static final String LOGIN_TIMES = "LOGIN_TIMES";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		this.ivStartBg = (ImageView) findViewById(R.id.ivStartBg);
		this.app = (GatherApplication) getApplication();
	
		new Thread(this).start();
		
		ExceptionManage.startInstance(getApplication());// 启动错误管理
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	public void run() {
		long startTime = System.currentTimeMillis();
		// 初始化操作----------------------------------------------
		if (PhoneManage.isSdCardExit()) {
			Constant.checkPath();
		}else {
			handler.sendEmptyMessage(0);
		}

		// 登录判断
		String sid = AppPreference.getUserPersistent(this, AppPreference.USER_ID);
		needLogin = (sid == null || sid.equals(""));
		
		//登录次数判断，用于是否显示导航页
		SharedPreferences timePreferences = StartActivity.this.getSharedPreferences(LOGIN_TIMES, Context.MODE_PRIVATE);
		int times = timePreferences.getInt("TIMES", 0);
		if (times == 0) {
			needGuide = true;
			SharedPreferences.Editor editor = timePreferences.edit();
			editor.putInt("TIMES", 1);
			editor.commit();
		}else {
			needGuide = false;
		}
		
		// 初始化结束-----------------------------------------------
		long endTime = System.currentTimeMillis();
		long diffTime = endTime - startTime;
		while (diffTime <= START_DURATION) {
			diffTime = System.currentTimeMillis() - startTime;
			/** 线程等待 **/
			Thread.yield();
		}
		handler.sendEmptyMessage(START_PROGRESS_OVER);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case START_PROGRESS_OVER:
				Intent intent ;
				if (needGuide) {
					intent = new Intent(StartActivity.this, LoginIndex.class);
				} else {
					if (needLogin) {
						intent = new Intent(StartActivity.this, LoginIndex.class);
					} else {// 跳首页
						intent = new Intent(StartActivity.this, LoginIndex.class);
					}
				}
				StartActivity.this.startActivity(intent);
				finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				break;
			case 0:
				Toast.makeText(StartActivity.this, "SD卡不存在", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

}
