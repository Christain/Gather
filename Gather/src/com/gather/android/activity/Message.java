package com.gather.android.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
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
import com.gather.android.adapter.MessageAdapter;
import com.gather.android.dialog.DialogChoiceBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.widget.ListViewCompat;
import com.gather.android.widget.SlideView;
import com.gather.android.widget.swipeback.SwipeBackActivity;

@SuppressLint("InflateParams")
public class Message extends SwipeBackActivity implements OnClickListener {
	
	private View actionbarView;
	private ImageView ivLeft, ivRight;
	private TextView tvTitle, tvRight;
	
	private ListViewCompat listView;
	private MessageAdapter adapter;
	private ArrayList<MessageItem> list;

	@Override
	protected int layoutResId() {
		return R.layout.message;
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
			this.tvTitle.setText("消息");
			this.tvRight.setVisibility(View.VISIBLE);
			this.tvRight.setText("清空");
			this.tvRight.setOnClickListener(this);
		}
		
		list = new ArrayList<MessageItem>();
		for (int i = 0; i < 20; i++) {
			MessageItem item = new MessageItem();
			item.name = "系统消息";
			item.content = "腾讯新闻腾讯新闻腾讯新闻腾讯新闻腾讯新闻腾讯新闻腾讯新闻腾讯新闻腾讯新闻腾讯新闻";
			item.type = "青岛爆炸";
			item.time = "晚上18:18";
			list.add(item);
		}
		this.listView = (ListViewCompat) findViewById(R.id.listview);
		this.adapter = new MessageAdapter(Message.this, list);
		this.listView.setAdapter(adapter);
	}
	
	public class MessageItem {
		public String name, content, type, time;
		public SlideView slideView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			finish();
			break;
		case R.id.tvRight:
			final DialogChoiceBuilder dialog = DialogChoiceBuilder.getInstance(Message.this);
			dialog.setMessage("您确定要删除全部消息？").withDuration(400).withEffect(Effectstype.SlideBottom).setOnClick(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
				}
			}).show();
			break;
		}
	}

}
