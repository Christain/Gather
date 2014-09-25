package com.gather.android.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.TouchGalleyAdapter;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.model.ActDetailPicModel;
import com.gather.android.widget.touchgallery.GalleryViewPager;

public class TouchGallery extends BaseActivity {
	
	private TextView tvNum;
	private GalleryViewPager gallery;
	private TouchGalleyAdapter adapter;
	private ArrayList<ActDetailPicModel> list;
	private int position;

	@Override
	protected int layoutResId() {
		return R.layout.touch_gallery;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("LIST") && intent.hasExtra("POSITION")) {
			this.list = (ArrayList<ActDetailPicModel>) intent.getSerializableExtra("LIST");
			this.position = intent.getExtras().getInt("POSITION");
			ActionBar actionBar = getSupportActionBar();
			if (null != actionBar) {
				actionBar.hide();
			}
			this.tvNum = (TextView) findViewById(R.id.tvNum);
			this.gallery = (GalleryViewPager) findViewById(R.id.viewpager);
			ArrayList<String> resources = new ArrayList<String>();
			for (int i = 0; i < list.size(); i++) {
//				resources.add(list.get(i).getImg_url());
				resources.add("http://c.hiphotos.baidu.com/image/pic/item/738b4710b912c8fce9a55ecafe039245d6882166.jpg");
			}
			this.gallery.setOnPageChangeListener(new myPageChangeListener());
			this.gallery.setOffscreenPageLimit(3);
			this.adapter = new TouchGalleyAdapter(TouchGallery.this, resources);
			this.gallery.setAdapter(adapter);
			
			this.initView();
		} else {
			finish();
			toast("图片参数错误~~");
		}
	}
	
	private void initView() {
		if (list.size() == 1) {
			tvNum.setVisibility(View.GONE);
		} else {
			tvNum.setVisibility(View.VISIBLE);
			tvNum.setText((position+1)+"/"+list.size());
		}
		gallery.setCurrentItem(position);
	}
	
	private class myPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageSelected(int position) {
			tvNum.setText((position+1)+"/"+list.size());
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			finish();
			overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
			break;
		}
		return true;
	}
	
}
