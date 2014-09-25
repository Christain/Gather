package com.gather.android.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.dialog.LoadingDialog.OnDismissListener;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.ResponseListener;
import com.gather.android.params.LoginPhoneParam;
import com.gather.android.utils.MobileUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;

public class LoginPhone extends SwipeBackActivity implements OnClickListener {

	private ImageView ivLeft, ivRight;
	private TextView tvTitle, tvRight, tvFindPassword, tvRegister;
	private EditText etPhone, etPassword;
	private Button btLogin;
	private View actionbarView;
	private LoadingDialog mLoadingDialog;
	private DialogTipsBuilder dialog;

	private static final int REGISTER = 1;
	private static final int FIND_PASSWORD = 2;
	private boolean isRequest = false;

	@Override
	protected int layoutResId() {
		return R.layout.login_phone;
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
			tvTitle.setText("集合啦");
			tvRight.setVisibility(View.GONE);
		}
		this.dialog = DialogTipsBuilder.getInstance(LoginPhone.this);
		this.mLoadingDialog = LoadingDialog.createDialog(LoginPhone.this, true);
		this.etPhone = (EditText) findViewById(R.id.etPhone);
		this.etPassword = (EditText) findViewById(R.id.etPassword);
		this.btLogin = (Button) findViewById(R.id.btLogin);
		this.tvFindPassword = (TextView) findViewById(R.id.tvFindPassword);
		this.tvRegister = (TextView) findViewById(R.id.tvRegister);

		this.btLogin.setOnClickListener(this);
		this.tvFindPassword.setOnClickListener(this);
		this.tvRegister.setOnClickListener(this);
		this.etPhone.addTextChangedListener(new phoneTextWatcher());
		this.mLoadingDialog.setDismissListener(new OnDismissListener() {
			@Override
			public void OnDismiss() {
				isRequest = false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.ivLeft:
			finish();
			break;
		case R.id.btLogin:
			if (!isRequest) {
				isRequest = true;
				if (!MobileUtil.execute(etPhone.getText().toString().trim().replace(" ", "")).equals("未知")) {
					int passwordLength = etPassword.getText().toString().length();
					if (passwordLength == 0) {
						dialog.setMessage("请输入密码").withEffect(Effectstype.Shake).show();
						isRequest = false;
						return;
					}
					if (passwordLength < 6 || passwordLength > 8) {
						dialog.setMessage("密码长度应该在6~8之间").withEffect(Effectstype.Shake).show();
						isRequest = false;
						return;
					}
					Login();
				} else {
					dialog.setMessage("请输入正确的电话号码").withEffect(Effectstype.Shake).show();
					isRequest = false;
					return;
				}
			}
			break;
		case R.id.tvFindPassword:
			intent = new Intent(LoginPhone.this, RegisterPhone.class);
			intent.putExtra("TYPE", FIND_PASSWORD);
			startActivity(intent);
			break;
		case R.id.tvRegister:
			intent = new Intent(LoginPhone.this, RegisterPhone.class);
			intent.putExtra("TYPE", REGISTER);
			startActivity(intent);
			break;
		}
	}
	
	/**
	 * 登录
	 */
	private void Login() {
		mLoadingDialog.setMessage("正在登录...");
		mLoadingDialog.show();
		LoginPhoneParam param = new LoginPhoneParam(LoginPhone.this, 1, etPhone.getText().toString().trim().replace(" ", ""), etPassword.getText().toString().trim());
		HttpStringPost task = new HttpStringPost(LoginPhone.this, param.getUrl(), new ResponseListener() {
			
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				try {
					JSONObject object = new JSONObject(result);
					if (object.has("is_regist") && object.getInt("is_regist") == 0) {
						Intent intent = new Intent(LoginPhone.this, RegisterData.class);
						startActivity(intent);
					} else {
						Intent intent = new Intent(LoginPhone.this, IndexHome.class);
						startActivity(intent);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						finish();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void relogin(String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				needLogin(msg);
			}
			
			@Override
			public void error(int code, String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage(msg).withEffect(Effectstype.Shake).show();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage(error.getMsg()).withEffect(Effectstype.Shake).show();
				}
			}
		}, param.getParameters());
		executeRequest(task);
	}

	private class phoneTextWatcher implements TextWatcher {
		@Override
		public void onTextChanged(CharSequence str, int arg1, int arg2, int arg3) {
			String contents = str.toString();
			int length = contents.length();
			if (length == 4) {
				if (contents.substring(3).equals(new String(" "))) {
					contents = contents.substring(0, 3);
					etPhone.setText(contents);
					etPhone.setSelection(contents.length());
				} else {
					contents = contents.substring(0, 3) + " " + contents.substring(3);
					etPhone.setText(contents);
					etPhone.setSelection(contents.length());
				}
			} else if (length == 9) {
				if (contents.substring(8).equals(new String(" "))) {
					contents = contents.substring(0, 8);
					etPhone.setText(contents);
					etPhone.setSelection(contents.length());
				} else {
					contents = contents.substring(0, 8) + " " + contents.substring(8);
					etPhone.setText(contents);
					etPhone.setSelection(contents.length());
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

}
