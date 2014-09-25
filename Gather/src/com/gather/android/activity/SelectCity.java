package com.gather.android.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.widget.swipeback.SwipeBackActivity;

public class SelectCity extends SwipeBackActivity implements OnClickListener {
	
	private ImageView ivLeft, ivRight;
	private TextView tvTitle, tvRight;
	private View actionbarView;

	@Override
	protected int layoutResId() {
		return R.layout.select_city;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		ActionBar actionBar = getSupportActionBar();
		if (null != actionBar) {
			actionBar.setDisplayShowHomeEnabled(false);
			actionBar.setDisplayShowCustomEnabled(true);
			actionBar.setDisplayShowTitleEnabled(false);
			LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			actionbarView = inflator.inflate(R.layout.actionbar_title, null);
			ActionBar.LayoutParams layout = new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			actionBar.setCustomView(actionbarView, layout);

			ivLeft = (ImageView) actionbarView.findViewById(R.id.ivLeft);
			ivRight = (ImageView) actionbarView.findViewById(R.id.ivRight);
			tvTitle = (TextView) actionbarView.findViewById(R.id.tvTitle);
			tvRight = (TextView) actionbarView.findViewById(R.id.tvRight);

			ivLeft.setOnClickListener(this);
			ivRight.setVisibility(View.GONE);
			tvTitle.setText("城市");
			tvRight.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			finish();
			break;
		}
	}

}
