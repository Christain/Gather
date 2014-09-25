package com.gather.android.dialog;

import java.util.Calendar;

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
import com.gather.android.widget.DatePicker;

public class DialogDateSelect extends Dialog {

	private View mDialogView;
	private TextView tvTips, tvCancel, tvSure;
	private DatePicker datePicker;
	private LinearLayout mLinearLayoutView;
	private int mDuration;
	// private Calendar mCalendar;
	private OnDateClickListener listener;

	private Effectstype type = null;

	public DialogDateSelect(Context context) {
		super(context);
		init(context);
	}

	public DialogDateSelect(Context context, int theme) {
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

	private void init(Context context) {
		// mCalendar = Calendar.getInstance();
		mDialogView = View.inflate(context, R.layout.dialog_select_date, null);
		mLinearLayoutView = (LinearLayout) mDialogView.findViewById(R.id.parentPanel);
		tvTips = (TextView) mDialogView.findViewById(R.id.tvTips);
		tvCancel = (TextView) mDialogView.findViewById(R.id.tvCancel);
		tvSure = (TextView) mDialogView.findViewById(R.id.tvSure);
		datePicker = (DatePicker) mDialogView.findViewById(R.id.datePicker);

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
		this.tvSure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (listener != null) {
					// mCalendar.set(Calendar.YEAR, datePicker.getYear());
					// mCalendar.set(Calendar.MONTH, datePicker.getMonth());
					// mCalendar.set(Calendar.DAY_OF_MONTH,
					// datePicker.getDay());
					listener.onDateListener(getBirthday(datePicker.getYear(), datePicker.getMonth(), datePicker.getDay()));
					dismiss();
				}
			}
		});
	}

	public DialogDateSelect setTips(CharSequence title) {
		tvTips.setText(title);
		return this;
	}

	public DialogDateSelect setCancel(CharSequence cancel) {
		tvCancel.setText(cancel);
		return this;
	}

	public DialogDateSelect setSure(CharSequence sure) {
		tvSure.setText(sure);
		return this;
	}

	public DialogDateSelect setOnCancelClick(View.OnClickListener cancelClick) {
		tvSure.setOnClickListener(cancelClick);
		return this;
	}

	public DialogDateSelect setOnSureClick(OnDateClickListener sureClick) {
		this.listener = sureClick;
		return this;
	}

	public DialogDateSelect withDuration(int duration) {
		this.mDuration = duration;
		return this;
	}

	public DialogDateSelect withEffect(Effectstype type) {
		this.type = type;
		return this;
	}

	public DialogDateSelect isCancelableOnTouchOutside(boolean cancelable) {
		this.setCanceledOnTouchOutside(cancelable);
		return this;
	}

	private void start(Effectstype type) {
		BaseEffects animator = type.getAnimator();
		if (mDuration != -1) {
			animator.setDuration(Math.abs(mDuration));
		}
		animator.start(mLinearLayoutView);
	}

	public interface OnDateClickListener {
		public void onDateListener(String date);
	}

	public void setDateListener(OnDateClickListener listener) {
		this.listener = listener;
	}

	private String getBirthday(int year, int month, int day) {
		StringBuilder sb = new StringBuilder();
		sb.append(year);
		sb.append("-");
		if (month + 1 < 10) {
			sb.append("0");
		}
		sb.append(month + 1);
		sb.append("-");
		sb.append(day);
		return sb.toString();
	}

}
