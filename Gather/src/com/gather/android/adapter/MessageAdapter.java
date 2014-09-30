package com.gather.android.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.activity.RecommendAct;
import com.gather.android.activity.Message.MessageItem;
import com.gather.android.dialog.DialogChoiceBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.widget.SlideView;
import com.gather.android.widget.SlideView.OnSlideListener;

@SuppressLint("InflateParams")
public class MessageAdapter extends BaseAdapter implements OnClickListener, OnSlideListener {

	private LayoutInflater mInflater;
	private Context mActivity;
	private List<MessageItem> list;
	private SlideView mLastSlideViewWithStatusOn;

	public MessageAdapter(Context activity, ArrayList<MessageItem> list) {
		this.mActivity = activity;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		SlideView slideView = (SlideView) convertView;
		if (slideView == null) {
			this.mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = mInflater.inflate(R.layout.item_message, null);
			slideView = new SlideView(mActivity);
			slideView.setContentView(itemView);

			holder = new ViewHolder(slideView);
			slideView.setOnSlideListener(this);
			slideView.setTag(holder);
		} else {
			holder = (ViewHolder) slideView.getTag();
		}
		MessageItem item = list.get(position);
		item.slideView = slideView;
		item.slideView.shrink();

		holder.tvMessageName.setText(item.name);
		holder.tvMessageContent.setText(item.content);
		holder.tvMessageType.setText(item.type);
		holder.tvTime.setText(item.time);

		holder.deleteHolder.setOnClickListener(this);

		return slideView;
	}

	private static class ViewHolder {
		public ImageView ivUser;
		public TextView tvMessageName, tvMessageContent, tvMessageType, tvTime;
		public ViewGroup deleteHolder;

		ViewHolder(View view) {
			ivUser = (ImageView) view.findViewById(R.id.ivUser);
			tvMessageName = (TextView) view.findViewById(R.id.tvMessageName);
			tvMessageContent = (TextView) view.findViewById(R.id.tvMessageContent);
			tvMessageType = (TextView) view.findViewById(R.id.tvType);
			tvTime = (TextView) view.findViewById(R.id.tvTime);
			deleteHolder = (ViewGroup) view.findViewById(R.id.holder);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.holder:
			final DialogChoiceBuilder dialog = DialogChoiceBuilder.getInstance(mActivity);
			dialog.setMessage("您确定要删除这条消息？").withDuration(400).withEffect(Effectstype.SlideBottom).setOnClick(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
				}
			}).show();
			break;
		}
	}

	@Override
	public void onSlide(View view, int status) {
		if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
			mLastSlideViewWithStatusOn.shrink();
		}

		if (status == SLIDE_STATUS_ON) {
			mLastSlideViewWithStatusOn = (SlideView) view;
		}
	}

}
