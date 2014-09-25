package com.gather.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;

public class DialogShareAct extends Dialog implements OnClickListener {
	
	private LinearLayout llWeChat, llSquare, llTencent, llZoon;
	private TextView tvCancel;
	private ShareClickListener listener;
	
	public final static int WECHAT = 1;
	public final static int SQUARE = 2;
	public final static int TENCENT = 3;
	public final static int ZOON = 4;

	public DialogShareAct(Context context) {
		super(context);
	}

	public DialogShareAct(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_share_act);
		Window w = getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
		lp.gravity = Gravity.BOTTOM;
		onWindowAttributesChanged(lp);
		
		this.llWeChat = (LinearLayout) findViewById(R.id.llWeChat);
		this.llSquare = (LinearLayout) findViewById(R.id.llSquare);
		this.llTencent = (LinearLayout) findViewById(R.id.llTencent);
		this.llZoon = (LinearLayout) findViewById(R.id.llZoon);
		this.tvCancel = (TextView) findViewById(R.id.tvCancel);
		
		this.llWeChat.setOnClickListener(this);
		this.llSquare.setOnClickListener(this);
		this.llTencent.setOnClickListener(this);
		this.llZoon.setOnClickListener(this);
		this.tvCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llWeChat:
			if (listener != null) {
				listener.OnShareClickListener(WECHAT);
				dismiss();
			}
			break;
		case R.id.llSquare:
			if (listener != null) {
				listener.OnShareClickListener(SQUARE);
				dismiss();
			}
			break;
		case R.id.llTencent:
			if (listener != null) {
				listener.OnShareClickListener(TENCENT);
				dismiss();
			}
			break;
		case R.id.llZoon:
			if (listener != null) {
				listener.OnShareClickListener(ZOON);
				dismiss();
			}
			break;
		case R.id.tvCancel:
			dismiss();
			break;
		}
	}
	
	public interface ShareClickListener {
		public void OnShareClickListener(int position);
	}
	
	public DialogShareAct setOnShareClickListener(ShareClickListener listener) {
		this.listener = listener;
		return this;
	}

}
