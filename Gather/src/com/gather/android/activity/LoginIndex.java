package com.gather.android.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.dialog.LoadingDialog.OnDismissListener;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.ResponseListener;
import com.gather.android.params.LoginThirdParam;
import com.gather.android.preference.AppPreference;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class LoginIndex extends BaseActivity implements OnClickListener {

	private Button btPhone;
	private FrameLayout flTencent, flSina;
	private LoadingDialog mLoadingDialog;
	private DialogTipsBuilder dialog;
	private boolean isReuqest = false;

	private Tencent mTencent;
	private IUiListener listener;
	private WeiboAuth mWeiboAuth;
	private Oauth2AccessToken mAccessToken;
	private SsoHandler mSsoHandler;

	@Override
	protected int layoutResId() {
		return R.layout.login_index;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		this.dialog = DialogTipsBuilder.getInstance(LoginIndex.this);
		this.mLoadingDialog = LoadingDialog.createDialog(LoginIndex.this, true);
		this.btPhone = (Button) findViewById(R.id.btPhone);
		this.flTencent = (FrameLayout) findViewById(R.id.flTencent);
		this.flSina = (FrameLayout) findViewById(R.id.flSina);

		this.btPhone.setOnClickListener(this);
		this.flTencent.setOnClickListener(this);
		this.flSina.setOnClickListener(this);

		this.mTencent = Tencent.createInstance(Constant.TENCENT_APPID, this.getApplicationContext());
		this.mWeiboAuth = new WeiboAuth(this, Constant.SINA_APPID, Constant.SINA_CALLBACK_URL, Constant.SINA_SCOPE);

		this.mLoadingDialog.setDismissListener(new OnDismissListener() {
			@Override
			public void OnDismiss() {
				isReuqest = false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btPhone:
			intent = new Intent(LoginIndex.this, LoginPhone.class);
			startActivity(intent);
			break;
		case R.id.flTencent:
			if (!isReuqest) {
				isReuqest = true;
				if (!AppPreference.getUserPersistent(LoginIndex.this, AppPreference.QQ_ID).equals("")) {
					mTencent.setOpenId(AppPreference.getUserPersistent(LoginIndex.this, AppPreference.QQ_ID));
					mTencent.setAccessToken(AppPreference.getUserPersistent(LoginIndex.this, AppPreference.QQ_TOKEN), AppPreference.getUserPersistent(LoginIndex.this, AppPreference.QQ_EXPIRES));
				}
				if (!mTencent.isSessionValid()) {
					listener = new IUiListener() {
						@Override
						public void onCancel() {
							isReuqest = false;
						}

						@Override
						public void onComplete(Object arg0) {
							try {
								JSONObject object = (JSONObject) arg0;
								String openid = object.getString("openid");
								String token = object.getString("access_token");
								String expires_in = object.getString("expires_in");
								AppPreference.saveLoginInfo(LoginIndex.this, AppPreference.TYPE_QQ, openid, token, expires_in);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							if (Constant.SHOW_LOG) {
								Log.e("aaaaaaaaaaaa", arg0.toString());
							}
							TencentLogin();
						}

						@Override
						public void onError(UiError arg0) {
							isReuqest = false;
							toast(arg0.toString());
						}
					};
					mTencent.login(LoginIndex.this, "all", listener);
				} else {
					TencentLogin();
				}
			}
			break;
		case R.id.flSina:
			if (!isReuqest) {
				isReuqest = true;
				Oauth2AccessToken token = new Oauth2AccessToken();
				if (!AppPreference.getUserPersistent(LoginIndex.this, AppPreference.SINA_ID).equals("")) {
					token.setUid(AppPreference.getUserPersistent(LoginIndex.this, AppPreference.SINA_ID));
					token.setToken(AppPreference.getUserPersistent(LoginIndex.this, AppPreference.SINA_TOKEN));
					token.setExpiresTime(Long.parseLong(AppPreference.getUserPersistent(LoginIndex.this, AppPreference.SINA_EXPIRES)));
				}
				if (token.isSessionValid()) {
					mAccessToken = token;
					SinaLogin();
				} else {
					if (checkSinaPackage()) {
						mSsoHandler = new SsoHandler(LoginIndex.this, mWeiboAuth);
						mSsoHandler.authorize(new AuthListener());
					} else {
						mWeiboAuth.anthorize(new AuthListener());
					}
				}
			}
			break;
		}
	}

	class AuthListener implements WeiboAuthListener {
		@Override
		public void onComplete(Bundle values) {
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (Constant.SHOW_LOG) {
				Log.e("aaaaaaaaaaa", "uid: " + mAccessToken.getUid() + "\n" + "token:" + mAccessToken.getToken() + "\n" + "expirestime: " + mAccessToken.getExpiresTime());
			}
			AppPreference.saveLoginInfo(LoginIndex.this, AppPreference.TYPE_SINA, mAccessToken.getUid(), mAccessToken.getToken(), String.valueOf(mAccessToken.getExpiresTime()));
			SinaLogin();
		}

		@Override
		public void onCancel() {
			isReuqest = false;
		}

		@Override
		public void onWeiboException(WeiboException e) {
			isReuqest = false;
		}
	}
	
	/**
	 * 新浪微博登录
	 */
	private void SinaLogin() {
		mLoadingDialog.setMessage("正在登录...");
		mLoadingDialog.show();
		LoginThirdParam params = new LoginThirdParam(LoginIndex.this, 3, mAccessToken.getUid(), mAccessToken.getToken(), String.valueOf(mAccessToken.getExpiresTime()));
		HttpStringPost post = new HttpStringPost(LoginIndex.this, params.getUrl(), new ResponseListener() {

			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					isReuqest = false;
					mLoadingDialog.dismiss();
				}
				try {
					JSONObject object = new JSONObject(result);
					if (object.has("is_regist") && object.getInt("is_regist") == 0) {
						Intent intent = new Intent(LoginIndex.this, RegisterData.class);
						startActivity(intent);
					} else {
						Intent intent = new Intent(LoginIndex.this, IndexHome.class);
						startActivity(intent);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						finishActivity();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
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

			@Override
			public void relogin(String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				needLogin(msg);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage(error.getMsg()).withEffect(Effectstype.Shake).show();
				}
			}
		}, params.getParameters());
		executeRequest(post);
	}

	/**
	 * QQ登录
	 */
	private void TencentLogin() {
		mLoadingDialog.setMessage("正在登录...");
		mLoadingDialog.show();
		LoginThirdParam params = new LoginThirdParam(LoginIndex.this, 4, AppPreference.getUserPersistent(LoginIndex.this, AppPreference.QQ_ID), AppPreference.getUserPersistent(LoginIndex.this, AppPreference.QQ_TOKEN), AppPreference.getUserPersistent(LoginIndex.this, AppPreference.QQ_EXPIRES));
		HttpStringPost post = new HttpStringPost(LoginIndex.this, params.getUrl(), new ResponseListener() {

			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					isReuqest = false;
					mLoadingDialog.dismiss();
				}
				try {
					JSONObject object = new JSONObject(result);
					if (object.has("is_regist") && object.getInt("is_regist") == 0) {
						Intent intent = new Intent(LoginIndex.this, RegisterData.class);
						startActivity(intent);
					} else {
						Intent intent = new Intent(LoginIndex.this, IndexHome.class);
						startActivity(intent);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						finishActivity();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
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

			@Override
			public void relogin(String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				needLogin(msg);
			}

		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage(error.getMsg()).withEffect(Effectstype.Shake).show();
				}
			}
		}, params.getParameters());
		executeRequest(post);
	}

	/**
	 * 检测新浪微博是否安装
	 * 
	 */
	private boolean checkSinaPackage() {
		PackageManager manager = getPackageManager();
		List<PackageInfo> pkgList = manager.getInstalledPackages(0);
		for (int i = 0; i < pkgList.size(); i++) {
			PackageInfo pI = pkgList.get(i);
			if (pI.packageName.equalsIgnoreCase("com.sina.weibo")) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_API) {
			if (resultCode == Constants.RESULT_LOGIN) {
				mTencent.handleLoginData(data, listener);
			}
			super.onActivityResult(requestCode, resultCode, data);
		}
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitApp();
		}
		return true;
	}
}
