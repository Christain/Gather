package com.gather.android.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.dialog.DialogChoiceBuilder;
import com.gather.android.dialog.DialogDateSelect;
import com.gather.android.dialog.DialogDateSelect.OnDateClickListener;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.model.RegisterDataModel;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.gather.android.widget.swipeback.SwipeBackLayout;

@SuppressLint("InflateParams")
public class RegisterData extends SwipeBackActivity implements OnClickListener {

	private ImageView ivLeft, ivRight;
	private TextView tvTitle, tvRight;
	private View actionbarView;
	private DialogTipsBuilder dialog;
	private RegisterDataModel model;

	private EditText etNickName, etPassword, etAddress, etMail;
	private ImageView ivMaleBox, ivFemaleBox;
	private LinearLayout llBirthday;
	private TextView tvBirthday;
	private int sex; // 1男，2女

	@Override
	protected int layoutResId() {
		return R.layout.register_data;
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
			tvTitle.setText("完善资料");
			tvRight.setText("下一步");
			tvRight.setOnClickListener(this);
		}

		SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
		mSwipeBackLayout.setEnableGesture(false);
		this.model = new RegisterDataModel();
		this.dialog = DialogTipsBuilder.getInstance(RegisterData.this);
		this.etNickName = (EditText) findViewById(R.id.etNickName);
		this.etPassword = (EditText) findViewById(R.id.etPassword);
		this.etAddress = (EditText) findViewById(R.id.etAddress);
		this.etMail = (EditText) findViewById(R.id.etMail);
		this.ivMaleBox = (ImageView) findViewById(R.id.ivMaleBox);
		this.ivFemaleBox = (ImageView) findViewById(R.id.ivFemaleBox);
		this.llBirthday = (LinearLayout) findViewById(R.id.llBirthday);
		this.tvBirthday = (TextView) findViewById(R.id.tvBirthday);

		this.llBirthday.setOnClickListener(this);
		this.ivMaleBox.setOnClickListener(this);
		this.ivFemaleBox.setOnClickListener(this);

		this.init();
	}

	private void init() {
		ivMaleBox.setImageResource(R.drawable.register_select_true);
		ivFemaleBox.setImageResource(R.drawable.register_select_false);
		sex = 1;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			DialogChoiceBuilder dialogChoice = DialogChoiceBuilder.getInstance(RegisterData.this);
			dialogChoice.setMessage("您确定要退出吗？").withDuration(400).withEffect(Effectstype.SlideBottom).setOnClick(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					onBackPressed();
				}
			}).show();
			break;
		case R.id.tvRight:
			if (TextUtils.isEmpty(etNickName.getText().toString().trim())) {
				dialog.setMessage("请输入您的昵称").withEffect(Effectstype.Shake).show();
				return;
			}
			int passwordLength = etPassword.getText().toString().trim().length();
			if (passwordLength == 0) {
				dialog.setMessage("请输入密码").withEffect(Effectstype.Shake).show();
				return;
			}
			if (passwordLength < 6 || passwordLength > 8) {
				dialog.setMessage("密码长度应该在6~8之间").withEffect(Effectstype.Shake).show();
				return;
			}
			if (tvBirthday.getText().toString().length() < 2) {
				dialog.setMessage("请选择您的生日").withEffect(Effectstype.Shake).show();
				return;
			}
			if (TextUtils.isEmpty(etAddress.getText().toString().trim())) {
				dialog.setMessage("请输入您的地址").withEffect(Effectstype.Shake).show();
				return;
			}
			if (!isEmail(etMail.getText().toString().trim())) {
				dialog.setMessage("您的邮箱格式不正确").withEffect(Effectstype.Shake).show();
				return;
			}
			model.setNickname(etNickName.getText().toString().trim());
			model.setPassword(etPassword.getText().toString().trim());
			model.setSex(sex);
			model.setBirthday(tvBirthday.getText().toString().trim());
			model.setAddress(etAddress.getText().toString().trim());
			model.setEmail(etMail.getText().toString().trim());
			Intent intent = new Intent(RegisterData.this, RegisterIcon.class);
			intent.putExtra("MODEL", model);
			startActivityForResult(intent, 100);
			break;
		case R.id.llBirthday:
			DialogDateSelect dialogDate = new DialogDateSelect(RegisterData.this, R.style.dialog_date);
			dialogDate.withDuration(400).withEffect(Effectstype.Fall).setOnSureClick(new OnDateClickListener() {
				@Override
				public void onDateListener(String date) {
					tvBirthday.setText(date);
				}
			}).show();
			break;
		case R.id.ivMaleBox:
			if (sex != 1) {
				sex = 1;
				ivMaleBox.setImageResource(R.drawable.register_select_true);
				ivFemaleBox.setImageResource(R.drawable.register_select_false);
			}
			break;
		case R.id.ivFemaleBox:
			if (sex != 2) {
				sex = 2;
				ivMaleBox.setImageResource(R.drawable.register_select_false);
				ivFemaleBox.setImageResource(R.drawable.register_select_true);
			}
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 100:
				if (data != null && data.hasExtra("MODEL")) {
					model = (RegisterDataModel) data.getSerializableExtra("MODEL");
				}
				break;
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			DialogChoiceBuilder dialog = DialogChoiceBuilder.getInstance(RegisterData.this);
			dialog.setMessage("您确定到退出吗？").withDuration(400).withEffect(Effectstype.SlideBottom).setOnClick(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					onBackPressed();
				}
			}).show();
		}
		return true;
	}

	/**
	 * 判断邮箱格式
	 * @param email
	 * @return
	 */
	private boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

}
