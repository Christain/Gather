package com.gather.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;

/**
 * Created by lee on 2014/7/30.
 */
public class DialogChoiceBuilder extends Dialog implements DialogInterface {

	private Effectstype type = null;

	private LinearLayout mLinearLayoutView;

	private TextView tvTips;

	private TextView tvMessage;

	private TextView tvSure;
	
	private TextView tvCancel;

	private View mDialogView;

	private int mDuration = -1;

	private boolean isCancelable = true;

	private volatile static DialogChoiceBuilder instance;

	public DialogChoiceBuilder(Context context) {
		super(context);
		init(context);

	}

	public DialogChoiceBuilder(Context context, int theme) {
		super(context, theme);
		init(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.height = ViewGroup.LayoutParams.MATCH_PARENT;
		params.width = ViewGroup.LayoutParams.MATCH_PARENT;
		getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
		
	}

	public static DialogChoiceBuilder getInstance(Context context) {
		instance = null;
		if (instance == null) {
			synchronized (DialogChoiceBuilder.class) {
				if (instance == null) {
					instance = new DialogChoiceBuilder(context, R.style.dialog_untran);
				}
			}
		}
		return instance;
	}

	private void init(Context context) {

		mDialogView = View.inflate(context, R.layout.dialog_choice, null);

		mLinearLayoutView = (LinearLayout) mDialogView.findViewById(R.id.parentPanel);
		tvTips = (TextView) mDialogView.findViewById(R.id.tvTips);
		tvMessage = (TextView) mDialogView.findViewById(R.id.tvMessage);
		tvSure = (TextView) mDialogView.findViewById(R.id.tvSure);
		tvCancel = (TextView) mDialogView.findViewById(R.id.tvCancel);

		setContentView(mDialogView);
		this.setCanceledOnTouchOutside(true);
		this.withDuration(400);
		this.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialogInterface) {

				mLinearLayoutView.setVisibility(View.VISIBLE);
				if (type == null) {
					type = Effectstype.Slidetop;
				}
				start(type);

			}
		});
		this.tvCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});

	}

	public DialogChoiceBuilder setTips(CharSequence title) {
		tvTips.setText(title);
		return this;
	}

	public DialogChoiceBuilder setMessage(CharSequence mseeage) {
		tvMessage.setText(mseeage);
		return this;
	}

	public DialogChoiceBuilder setSure(CharSequence sure) {
		tvSure.setText(sure);
		return this;
	}

	public DialogChoiceBuilder setOnClick(View.OnClickListener click) {
		tvSure.setOnClickListener(click);
		return this;
	}

	public DialogChoiceBuilder withDuration(int duration) {
		this.mDuration = duration;
		return this;
	}

	public DialogChoiceBuilder withEffect(Effectstype type) {
		this.type = type;
		return this;
	}

	public DialogChoiceBuilder isCancelableOnTouchOutside(boolean cancelable) {
		this.isCancelable = cancelable;
		this.setCanceledOnTouchOutside(cancelable);
		return this;
	}

	public DialogChoiceBuilder isCancelable(boolean cancelable) {
		this.isCancelable = cancelable;
		this.setCancelable(cancelable);
		return this;
	}

	private void start(Effectstype type) {
		BaseEffects animator = type.getAnimator();
		if (mDuration != -1) {
			animator.setDuration(Math.abs(mDuration));
		}
		animator.start(mLinearLayoutView);
	}

}
