package com.gather.android.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;

public class XListViewFooter extends LinearLayout{

	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_LOADING = 2;

	private Context mContext;

	private View mContentView;
	private View mProgressBar;
	private TextView mHintView;
	private TextView mNoMessage;
	private boolean isVisible = false;

	public XListViewFooter(Context context) {
		super(context);
		initView(context);
	}

	public XListViewFooter(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public void setState(int state) {
		if (!isVisible) {
			mHintView.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.GONE);
			if (state == STATE_READY) {
				mHintView.setVisibility(View.VISIBLE);
				mHintView.setText(R.string.xlistview_footer_hint_ready);
			} else if (state == STATE_LOADING) {
				mProgressBar.setVisibility(View.VISIBLE);
			} else {
				mHintView.setVisibility(View.VISIBLE);
				mHintView.setText(R.string.xlistview_footer_hint_normal);
			}
		}
	}

	public void setMessage(String msg) { // 有内容时的状态
		if (isVisible) {
			mNoMessage.setVisibility(View.GONE);
			isVisible = false;
		}
		mHintView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
		mHintView.setText(msg);
	}

	public void setImageView(String msg, int resource) { // 没有内容时显示图片和文字提示
		mHintView.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.GONE);
		mNoMessage.setVisibility(View.VISIBLE);
		isVisible = true;
		mNoMessage.setText(msg);
		Drawable drawable = getResources().getDrawable(resource);
		mNoMessage.setCompoundDrawablePadding(5);
		mNoMessage.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
	}

	public String getMessage() {
		return mHintView.getText().toString();
	}

	public boolean getMessageStauts() { // list中是否有内容，没有的时候加载更多无效，显示图片
		return isVisible;
	}

	public void setBottomMargin(int height) {
		if (height < 0)
			return;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
		lp.bottomMargin = height;
		mContentView.setLayoutParams(lp);
	}

	public int getBottomMargin() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
		return lp.bottomMargin;
	}

	/**
	 * normal status
	 */
	public void normal() {
		mHintView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
		mNoMessage.setVisibility(View.GONE);
		isVisible = false;
	}

	/**
	 * loading status
	 */
	public void loading() {
		isVisible = false;
		mNoMessage.setVisibility(View.GONE);
		mHintView.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.VISIBLE);
	}

	/**
	 * hide footer when disable pull load more
	 */
	public void hide() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
		lp.height = 0;
		mContentView.setLayoutParams(lp);
	}

	/**
	 * show footer
	 */
	public void show() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
		lp.height = LayoutParams.WRAP_CONTENT;
		mContentView.setLayoutParams(lp);
	}

	private void initView(Context context) {
		mContext = context;
		LinearLayout moreView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.xlistview_footer, null);
		addView(moreView);
		moreView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		mContentView = moreView.findViewById(R.id.xlistview_footer_content);
		mProgressBar = moreView.findViewById(R.id.xlistview_footer_progressbar);
		mHintView = (TextView) moreView.findViewById(R.id.xlistview_footer_hint_textview);
		mNoMessage = (TextView) moreView.findViewById(R.id.xlistview_footer_no_message);
	}
}
