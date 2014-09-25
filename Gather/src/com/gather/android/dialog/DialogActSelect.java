package com.gather.android.dialog;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.custom.vg.list.CustomListView;
import com.custom.vg.list.OnItemClickListener;
import com.gather.android.R;
import com.gather.android.adapter.SelectInterestAdapter;
import com.gather.android.model.InterestStringModel;

public class DialogActSelect extends Dialog implements android.view.View.OnClickListener {

	private View mDialogView;
	private TextView tvTips, tvCancel, tvSure;
	private CustomListView gridview;
	private RelativeLayout rlOne, rlTwo, rlThree, rlFour;
	private ImageView ivOne, ivTwo, ivThree, ivFour;
	private int TimeType = 0;
	private SelectInterestAdapter adapter;
	private LinearLayout mLinearLayoutView;
	private int mDuration;
	private OnActivityClickListener listener;
	
	private ArrayList<InterestStringModel> list;
	private final static int MAX_INTEREST = 2;
	private int num = 0;
	private boolean hasInterest = false;//判断是否有兴趣标签

	private Effectstype type = null;

	public DialogActSelect(Context context) {
		super(context);
		init(context);
	}

	public DialogActSelect(Context context, int theme, ArrayList<InterestStringModel> list, int timeType) {
		super(context, theme);
		this.TimeType = timeType;
		if (list != null) {
			hasInterest = true;
			this.list = list;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).isSelect()) {
					num = num +1;
					if (num > MAX_INTEREST) {
						list.get(i).setSelect(false);
						num = num - 1;
					}
				}
			}
		} else {
			hasInterest = false;
		}
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
		mDialogView = View.inflate(context, R.layout.dialog_select_act, null);
		mLinearLayoutView = (LinearLayout) mDialogView.findViewById(R.id.parentPanel);
		tvTips = (TextView) mDialogView.findViewById(R.id.tvTips);
		tvCancel = (TextView) mDialogView.findViewById(R.id.tvCancel);
		tvSure = (TextView) mDialogView.findViewById(R.id.tvSure);
		this.gridview = (CustomListView) mDialogView.findViewById(R.id.gridview);
		if (hasInterest) {
			this.gridview.setVisibility(View.VISIBLE);
			this.tvTips.setVisibility(View.VISIBLE);
			this.gridview.setDividerHeight(context.getResources().getDimensionPixelOffset(R.dimen.divider_act_size));
			this.gridview.setDividerWidth(context.getResources().getDimensionPixelOffset(R.dimen.divider_act_size));
			this.adapter = new SelectInterestAdapter(context, list, "ACT_SELECT");
			this.gridview.setAdapter(adapter);
			
			this.gridview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
					if (num <= MAX_INTEREST) {
						InterestStringModel model = adapter.getItem(position);
						if (null != model) {
							if (model.isSelect()) {
								num = num - 1;
								model.setSelect(false);
							} else {
								if (num < MAX_INTEREST) {
									num = num + 1;
									model.setSelect(true);
								} else {
									Toast.makeText(getContext(), "最多能选"+ MAX_INTEREST+"个标签", Toast.LENGTH_SHORT).show();
								}
							}
							adapter.notifyDataSetChanged();
						}
					} else {
						Toast.makeText(getContext(), "最多能选"+ MAX_INTEREST+"个标签", Toast.LENGTH_SHORT).show();
					}
				}
			});
		} else {
			this.gridview.setVisibility(View.GONE);
			this.tvTips.setVisibility(View.GONE);
		}
		
		this.rlOne = (RelativeLayout) mDialogView.findViewById(R.id.rlOne);
		this.rlTwo = (RelativeLayout) mDialogView.findViewById(R.id.rlTwo);
		this.rlThree = (RelativeLayout) mDialogView.findViewById(R.id.rlThree);
		this.rlFour = (RelativeLayout) mDialogView.findViewById(R.id.rlFour);
		this.ivOne = (ImageView) mDialogView.findViewById(R.id.ivArrowOne);
		this.ivTwo = (ImageView) mDialogView.findViewById(R.id.ivArrowTwo);
		this.ivThree = (ImageView) mDialogView.findViewById(R.id.ivArrowThree);
		this.ivFour = (ImageView) mDialogView.findViewById(R.id.ivArrowFour);
		
		this.rlOne.setOnClickListener(this);
		this.rlTwo.setOnClickListener(this);
		this.rlThree.setOnClickListener(this);
		this.rlFour.setOnClickListener(this);

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
					if (num != 0) {
						ArrayList<Integer> idList = new ArrayList<Integer>(MAX_INTEREST);
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).isSelect()) {
								idList.add(list.get(i).getId());
							}
						}
						listener.onSelectListener(idList, TimeType);
					} else {
						listener.onSelectListener(null, TimeType);
					}
					dismiss();
				}
			}
		});
		this.initView();
	}
	
	private void initView() {
		switch (TimeType) {
		case 0:
			rlOneSelect(false);
			rlTwoSelect(false);
			rlThreeSelect(false);
			rlFourSelect(false);
			break;
		case 1:
			rlOneSelect(true);
			rlTwoSelect(false);
			rlThreeSelect(false);
			rlFourSelect(false);
			break;
		case 2:
			rlOneSelect(false);
			rlTwoSelect(true);
			rlThreeSelect(false);
			rlFourSelect(false);
			break;
		case 3:
			rlOneSelect(false);
			rlTwoSelect(false);
			rlThreeSelect(true);
			rlFourSelect(false);
			break;
		case 4:
			rlOneSelect(false);
			rlTwoSelect(false);
			rlThreeSelect(false);
			rlFourSelect(true);
			break;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rlOne:
			if (TimeType != 1) {
				TimeType = 1;
				rlOneSelect(true);
				rlTwoSelect(false);
				rlThreeSelect(false);
				rlFourSelect(false);
			}
			break;
		case R.id.rlTwo:
			if (TimeType != 2) {
				TimeType = 2;
				rlOneSelect(false);
				rlTwoSelect(true);
				rlThreeSelect(false);
				rlFourSelect(false);
			}
			break;
		case R.id.rlThree:
			if (TimeType != 3) {
				TimeType = 3;
				rlOneSelect(false);
				rlTwoSelect(false);
				rlThreeSelect(true);
				rlFourSelect(false);
			}
			break;
		case R.id.rlFour:
			if (TimeType != 4) {
				TimeType = 4;
				rlOneSelect(false);
				rlTwoSelect(false);
				rlThreeSelect(false);
				rlFourSelect(true);
			}
			break;
		}
	}

	public DialogActSelect setTips(CharSequence title) {
		tvTips.setText(title);
		return this;
	}

	public DialogActSelect setCancel(CharSequence cancel) {
		tvCancel.setText(cancel);
		return this;
	}

	public DialogActSelect setSure(CharSequence sure) {
		tvSure.setText(sure);
		return this;
	}

	public DialogActSelect setOnCancelClick(View.OnClickListener cancelClick) {
		tvSure.setOnClickListener(cancelClick);
		return this;
	}

	public DialogActSelect setOnSureClick(OnActivityClickListener sureClick) {
		this.listener = sureClick;
		return this;
	}

	public DialogActSelect withDuration(int duration) {
		this.mDuration = duration;
		return this;
	}

	public DialogActSelect withEffect(Effectstype type) {
		this.type = type;
		return this;
	}

	public DialogActSelect isCancelableOnTouchOutside(boolean cancelable) {
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

	public interface OnActivityClickListener {
		public void onSelectListener(ArrayList<Integer> list, int timeType);
	}

	public void setActivityListener(OnActivityClickListener listener) {
		this.listener = listener;
	}
	
	private void rlOneSelect(boolean select){
		if (select) {
			rlOne.setSelected(true);
			ivOne.setVisibility(View.VISIBLE);
		} else {
			rlOne.setSelected(false);
			ivOne.setVisibility(View.GONE);
		}
	}
	
	private void rlTwoSelect(boolean select){
		if (select) {
			rlTwo.setSelected(true);
			ivTwo.setVisibility(View.VISIBLE);
		} else {
			rlTwo.setSelected(false);
			ivTwo.setVisibility(View.GONE);
		}
	}
	
	private void rlThreeSelect(boolean select){
		if (select) {
			rlThree.setSelected(true);
			ivThree.setVisibility(View.VISIBLE);
		} else {
			rlThree.setSelected(false);
			ivThree.setVisibility(View.GONE);
		}
	}
	
	private void rlFourSelect(boolean select){
		if (select) {
			rlFour.setSelected(true);
			ivFour.setVisibility(View.VISIBLE);
		} else {
			rlFour.setSelected(false);
			ivFour.setVisibility(View.GONE);
		}
	}

}
