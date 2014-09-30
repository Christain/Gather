package com.gather.android.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.InterestActAdapter;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.listener.OnAdapterLoadMoreOverListener;
import com.gather.android.listener.OnAdapterRefreshOverListener;
import com.gather.android.widget.XListView;
import com.gather.android.widget.XListView.IXListViewListener;
import com.gather.android.widget.swipeback.SwipeBackActivity;

@SuppressLint("InflateParams")
public class InterestActList extends SwipeBackActivity implements OnClickListener {
	
	private View actionbarView;
	private ImageView ivLeft, ivRight;
	private TextView tvTitle, tvRight;
	private XListView listView;
	private LinearLayout llNoAct;
	private InterestActAdapter adapter;
	private DialogTipsBuilder dialog;

	@Override
	protected int layoutResId() {
		return R.layout.interest_act_list;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		ActionBar actionBar = getSupportActionBar();
		if (null != actionBar) {
			actionBar.setDisplayShowHomeEnabled(false);
			actionBar.setDisplayShowCustomEnabled(true);
			actionBar.setDisplayShowTitleEnabled(false);
			LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.actionbarView = inflator.inflate(R.layout.actionbar_title, null);
			ActionBar.LayoutParams layout = new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			actionBar.setCustomView(actionbarView, layout);

			this.ivLeft = (ImageView) actionbarView.findViewById(R.id.ivLeft);
			this.ivRight = (ImageView) actionbarView.findViewById(R.id.ivRight);
			this.tvTitle = (TextView) actionbarView.findViewById(R.id.tvTitle);
			this.tvRight = (TextView) actionbarView.findViewById(R.id.tvRight);

			this.ivLeft.setOnClickListener(this);
			this.ivRight.setVisibility(View.GONE);
			this.tvTitle.setText("感兴趣的活动");
			this.tvRight.setVisibility(View.GONE);
		}
		this.dialog = DialogTipsBuilder.getInstance(InterestActList.this);
		this.llNoAct = (LinearLayout) findViewById(R.id.llNoAct);
		this.llNoAct.setVisibility(View.GONE);
		this.listView = (XListView) findViewById(R.id.listview);

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		this.listView.setPullLoadEnable(true);
		this.listView.setPullRefreshEnable(true);
		this.listView.setLoadMoreNeedHigh(metrics.heightPixels);
		this.listView.stopLoadMoreMessageBox();
		this.listView.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				adapter.getInterestAct();
			}

			@Override
			public void onLoadMore() {
				adapter.loadMore();
			}
		});

		this.adapter = new InterestActAdapter(InterestActList.this);
		this.adapter.setRefreshOverListener(new OnAdapterRefreshOverListener() {
			@Override
			public void refreshOver(int code, String msg) {
				listView.stopRefresh();
				switch (code) {
				case 0:
					if (msg.equals(SuperAdapter.ISNULL)) {
						listView.setVisibility(View.GONE);
						llNoAct.setVisibility(View.VISIBLE);
					} else {
						listView.setText(msg);
					}
					break;
				case 5:
					needLogin(msg);
					break;
				default:
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage(msg).withEffect(Effectstype.Shake).show();
					}
					break;
				}
			}
		});
		this.adapter.setLoadMoreOverListener(new OnAdapterLoadMoreOverListener() {
			@Override
			public void loadMoreOver(int code, String msg) {
				listView.stopLoadMore();
				switch (code) {
				case 0:
					listView.setText(msg);
					break;
				case 5:
					needLogin(msg);
					break;
				default:
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage(msg).withEffect(Effectstype.Shake).show();
					}
					break;
				}
			}
		});
		this.listView.setAdapter(adapter);
		this.listView.onClickRefush();
		this.adapter.getInterestAct();
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
