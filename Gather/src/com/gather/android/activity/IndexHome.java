package com.gather.android.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.fragment.HomeFragment;
import com.gather.android.fragment.MenuFragment;
import com.nineoldandroids.view.ViewHelper;

public class IndexHome extends BaseActivity {

	private SlidingPaneLayout slidingPaneLayout;
	private MenuFragment menuFragment;
	private HomeFragment homeFragment;
	private FrameLayout flMenu, flContent;

	@Override
	protected int layoutResId() {
		return R.layout.index_home;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		slidingPaneLayout = (SlidingPaneLayout) findViewById(R.id.slidingpanellayout);
		slidingPaneLayout.setSliderFadeColor(0x00000000);
		flMenu = (FrameLayout) findViewById(R.id.frame_menu);
		flContent = (FrameLayout) findViewById(R.id.frame_content);
		menuFragment = new MenuFragment();
		homeFragment = new HomeFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.frame_menu, menuFragment).commit();
		getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, homeFragment).commit();

		slidingPaneLayout.setPanelSlideListener(new PanelSlideListener() {
			@Override
			public void onPanelSlide(View panel, float slideOffset) {
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
					flContent.setScaleY(1 - slideOffset / 5);
					flMenu.setScaleY(slideOffset / 2 + 0.5F);
					flMenu.setScaleX(slideOffset / 2 + 0.5F);
					flMenu.setAlpha(slideOffset);
				} else {
					ViewHelper.setScaleY(flContent, 1 - slideOffset / 5);
					ViewHelper.setScaleY(flMenu, slideOffset / 2 + 0.5F);
					ViewHelper.setScaleX(flMenu, slideOffset / 2 + 0.5F);
					ViewHelper.setAlpha(flMenu, slideOffset);
				}
			}

			@Override
			public void onPanelOpened(View panel) {

			}

			@Override
			public void onPanelClosed(View panel) {

			}
		});
	}

	/**
	 * 监听返回和菜单键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (!slidingPaneLayout.isOpen()) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);
			} else {
				slidingPaneLayout.closePane();
			}
			break;
		case KeyEvent.KEYCODE_MENU:
			if (!slidingPaneLayout.isOpen()) {
				slidingPaneLayout.openPane();
			} else {
				slidingPaneLayout.closePane();
			}
			break;
		}
		return true;
	}

	/**
	 * 显示主页面
	 */
	public void showContent() {
		if (slidingPaneLayout != null) {
			slidingPaneLayout.closePane();
		}
	}

	/**
	 * 显示菜单
	 */
	public void showMenu() {
		if (slidingPaneLayout != null) {
			slidingPaneLayout.openPane();
		}
	}

	/**
	 * 判断菜单是否打开
	 */
	public boolean isOpen() {
		if (slidingPaneLayout != null) {
			if (slidingPaneLayout.isOpen()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
