package com.gather.android.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.utils.MobileUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;

@SuppressLint("InflateParams")
public class EditData extends SwipeBackActivity implements OnClickListener {
	
	private View actionbarView;
	private ImageView ivLeft, ivRight;
	private TextView tvTitle, tvRight;
	
	private EditText etContent;
	private ImageView ivClear;
	private TextView tvTips;
	private Intent intent;
	private int type;
	private String content = "";
	private DialogTipsBuilder dialog;

	@Override
	protected int layoutResId() {
		return R.layout.edit_data;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		intent = getIntent();
		if (intent.hasExtra("TYPE") && intent.hasExtra("CONTENT")) {
			this.type = intent.getExtras().getInt("TYPE");
			this.content = intent.getStringExtra("CONTENT");
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
				this.tvTitle.setText("个人信息");
				this.tvRight.setVisibility(View.VISIBLE);
				this.tvRight.setText("确定");
				this.tvRight.setOnClickListener(this);
			}
			
			this.dialog = DialogTipsBuilder.getInstance(EditData.this);
			this.etContent = (EditText) findViewById(R.id.etContent);
			this.ivClear = (ImageView) findViewById(R.id.ivClear);
			this.tvTips = (TextView) findViewById(R.id.tvTips);
			
			this.ivClear.setOnClickListener(this);
			
			this.initView();
		} else {
			finish();
			toast("信息不全~~");
		}
	}
	
	private void initView() {
		if (type == UserInfo.REQUEST_CONTACT_PHONE) {
			etContent.setText(ProgressPhoneNum(content));
		} else {
			etContent.setText(content);
		}
		Selection.setSelection(etContent.getText(), etContent.getText().length());
		if (content.length() == 0) {
			ivClear.setVisibility(View.GONE);
		} else {
			ivClear.setVisibility(View.VISIBLE);
		}
		switch (type) {
		case UserInfo.REQUEST_NICK_NAME:
			etContent.addTextChangedListener(mTextWatcher);
			tvTitle.setText("修改昵称");
			tvTips.setText("以中文或英文字母开头，限1-8字符");
			etContent.setHint("请输入昵称");
			etContent.setFilters(new InputFilter[] { new InputFilter.LengthFilter(8)});
			break;
		case UserInfo.REQUEST_USER_NAME:
			etContent.addTextChangedListener(mTextWatcher);
			tvTitle.setText("修改姓名");
			tvTips.setText("用于报名活动的信息核实");
			etContent.setHint("请输入姓名");
			etContent.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4)});
			break;
		case UserInfo.REQUEST_CONTACT_PHONE:
			etContent.addTextChangedListener(new phoneTextWatcher());
			tvTitle.setText("修改联系电话");
			tvTips.setText("请填写正确手机号码，以便于我们与您联系");
			etContent.setHint("请输入手机号码");
			etContent.setInputType(InputType.TYPE_CLASS_NUMBER);
			etContent.setFilters(new InputFilter[] { new InputFilter.LengthFilter(13)});
			break;
		case UserInfo.REQUEST_CONTACT_ADDRESS:
			etContent.addTextChangedListener(mTextWatcher);
			tvTitle.setText("修改联系地址");
			tvTips.setText("请填写正确地址信息，以便于我们与您联系");
			etContent.setHint("请输入联系地址");
			etContent.setFilters(new InputFilter[] { new InputFilter.LengthFilter(25)});
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			finish();
			break;
		case R.id.tvRight:
			switch (type) {
			case UserInfo.REQUEST_NICK_NAME:
				if (TextUtils.isEmpty(etContent.getText().toString().trim())) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("昵称不能为空").withEffect(Effectstype.Shake).show();
					}
					return;
				}
				Intent intent = new Intent();
				intent.putExtra("CONTENT", etContent.getText().toString().trim());
				setResult(RESULT_OK, intent);
				finish();
				break;
			case UserInfo.REQUEST_USER_NAME:
				Intent name = new Intent();
				if (TextUtils.isEmpty(etContent.getText().toString().trim())) {
					name.putExtra("CONTENT", "");
				} else {
					name.putExtra("CONTENT", etContent.getText().toString().trim());
				}
				setResult(RESULT_OK, name);
				finish();
				break;
			case UserInfo.REQUEST_CONTACT_PHONE:
				if (!TextUtils.isEmpty(etContent.getText().toString().trim())) {
					if (!MobileUtil.execute(etContent.getText().toString().trim().replace(" ", "")).equals("未知")) {
						Intent phone = new Intent();
						phone.putExtra("CONTENT", etContent.getText().toString().trim().replace(" ", ""));
						setResult(RESULT_OK, phone);
						finish();
					} else {
						if (dialog != null && !dialog.isShowing()) {
							dialog.setMessage("请输入正确的电话号码").withEffect(Effectstype.Shake).show();
						}
						return;
					}
				} else {
					Intent phone = new Intent();
					phone.putExtra("CONTENT", etContent.getText().toString().trim());
					setResult(RESULT_OK, phone);
					finish();
				}
				break;
			case UserInfo.REQUEST_CONTACT_ADDRESS:
				Intent address = new Intent();
				address.putExtra("CONTENT", etContent.getText().toString().trim());
				setResult(RESULT_OK, address);
				finish();
				break;
			}
			break;
		case R.id.ivClear:
			etContent.setText("");
			break;
		}
	}
	
	private TextWatcher mTextWatcher = new TextWatcher() { // 字数监听
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.length() == 0) {
				ivClear.setVisibility(View.GONE);
			} else {
				if (!ivClear.isShown()) {
					ivClear.setVisibility(View.VISIBLE);
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
		}
	};
	
	private class phoneTextWatcher implements TextWatcher {
		@Override
		public void onTextChanged(CharSequence str, int arg1, int arg2, int arg3) {
			String contents = str.toString();
			int length = contents.length();
			if (length == 4) {
				if (contents.substring(3).equals(new String(" "))) {
					contents = contents.substring(0, 3);
					etContent.setText(contents);
					etContent.setSelection(contents.length());
				} else {
					contents = contents.substring(0, 3) + " " + contents.substring(3);
					etContent.setText(contents);
					etContent.setSelection(contents.length());
				}
			} else if (length == 9) {
				if (contents.substring(8).equals(new String(" "))) {
					contents = contents.substring(0, 8);
					etContent.setText(contents);
					etContent.setSelection(contents.length());
				} else {
					contents = contents.substring(0, 8) + " " + contents.substring(8);
					etContent.setText(contents);
					etContent.setSelection(contents.length());
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence str, int arg1, int arg2, int arg3) {

		}

		@Override
		public void afterTextChanged(Editable arg0) {

		}
	}
	
	/**
	 * 处理手机号码的显示
	 */
	private String ProgressPhoneNum(String phone) {
		StringBuffer sb = new StringBuffer();
		sb.append(phone.substring(0, 3));
		sb.append(" ");
		sb.append(phone.substring(3, 7));
		sb.append(" ");
		sb.append(phone.substring(7, phone.length()));
		return sb.toString();
	}
}
