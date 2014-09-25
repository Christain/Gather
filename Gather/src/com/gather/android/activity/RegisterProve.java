package com.gather.android.activity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.gather.android.R;
import com.gather.android.activity.LoginIndex.AuthListener;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.dialog.LoadingDialog.OnDismissListener;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.ResponseListener;
import com.gather.android.params.LoginThirdParam;
import com.gather.android.params.RegisterPhoneGetNumParam;
import com.gather.android.params.RegisterPhoneIdentifyNumParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class RegisterProve extends SwipeBackActivity implements OnClickListener {

	private ImageView ivLeft, ivRight;
	private TextView tvTitle, tvRight, tvPhoneNum, tvTime, tvResend;
	private EditText etProveNum;
	private LinearLayout llThird, llResend, llTencent, llSina;
	private View actionbarView;
	private LoadingDialog mLoadingDialog;
	private DialogTipsBuilder dialog;
	private boolean isRequest = false;

	private TimerTask timerTask;
	private Timer timer = new Timer();
	private int second = 60;
	private Animation anim;
	private int type; // 1是注册，2是找回密码
	private String phoneNum;
	
	private Tencent mTencent;
	private IUiListener listener;
	private WeiboAuth mWeiboAuth;
	private Oauth2AccessToken mAccessToken;
	private SsoHandler mSsoHandler;


	@Override
	protected int layoutResId() {
		return R.layout.register_prove;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("TYPE")) {
			type = intent.getExtras().getInt("TYPE");
			phoneNum = intent.getStringExtra("PHONE");
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
				tvRight.setText("下一步");
				tvRight.setOnClickListener(this);
			}
			this.mTencent = Tencent.createInstance(Constant.TENCENT_APPID, this.getApplicationContext());
			this.mWeiboAuth = new WeiboAuth(this, Constant.SINA_APPID, Constant.SINA_CALLBACK_URL, Constant.SINA_SCOPE);
			this.dialog = DialogTipsBuilder.getInstance(RegisterProve.this);
			this.mLoadingDialog = LoadingDialog.createDialog(RegisterProve.this, true);
			this.tvPhoneNum = (TextView) findViewById(R.id.tvNum);
			this.tvPhoneNum.setText("\"" + phoneNum + "\"");
			this.etProveNum = (EditText) findViewById(R.id.etProveNum);
			this.tvPhoneNum = (TextView) findViewById(R.id.tvNum);
			this.tvTime = (TextView) findViewById(R.id.tvTime);
			this.tvTime.setVisibility(View.VISIBLE);
			this.tvResend = (TextView) findViewById(R.id.tvResend);
			this.tvResend.setTextColor(0xFF696969);
			this.llTencent = (LinearLayout) findViewById(R.id.llTencent);
			this.llSina = (LinearLayout) findViewById(R.id.llSina);
			this.llThird = (LinearLayout) findViewById(R.id.llThird);
			this.llThird.setVisibility(View.GONE);
			this.llResend = (LinearLayout) findViewById(R.id.llResend);
			this.llResend.setBackgroundColor(0x00000000);

			this.anim = AnimationUtils.loadAnimation(RegisterProve.this, R.anim.alpha_in);
			this.llResend.setOnClickListener(this);
			this.llTencent.setOnClickListener(this);
			this.llSina.setOnClickListener(this);
			this.mLoadingDialog.setDismissListener(new OnDismissListener() {
				@Override
				public void OnDismiss() {
					isRequest = false;
				}
			});

			this.timerTask = new TimerTask() {
				@Override
				public void run() {
					handler.sendEmptyMessage(0);
				}
			};
			timer.schedule(timerTask, 200, 1000);
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (second == 1) {
				timerTask.cancel();
				tvTime.setVisibility(View.GONE);
				tvResend.setTextColor(0xffffffff);
				llResend.setBackgroundResource(R.drawable.shape_register_resend);
				if (!llThird.isShown() && type == 1) {
					llThird.startAnimation(anim);
					llThird.setVisibility(View.VISIBLE);
				}
			} else {
				second = second - 1;
				tvTime.setText(second + "s ");
			}
		};
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llResend:
			if (!tvTime.isShown() && !isRequest) {
				isRequest = true;
				getIdentifyNum(phoneNum.replace(" ", ""));
			}
			break;
		case R.id.llTencent:
			if (!isRequest) {
				isRequest = true;
				if (!AppPreference.getUserPersistent(RegisterProve.this, AppPreference.QQ_ID).equals("")) {
					mTencent.setOpenId(AppPreference.getUserPersistent(RegisterProve.this, AppPreference.QQ_ID));
					mTencent.setAccessToken(AppPreference.getUserPersistent(RegisterProve.this, AppPreference.QQ_TOKEN), AppPreference.getUserPersistent(RegisterProve.this, AppPreference.QQ_EXPIRES));
				}
				if (!mTencent.isSessionValid()) {
					listener = new IUiListener() {
						@Override
						public void onCancel() {
							isRequest = false;
						}

						@Override
						public void onComplete(Object arg0) {
							try {
								JSONObject object = (JSONObject) arg0;
								String openid = object.getString("openid");
								String token = object.getString("access_token");
								String expires_in = object.getString("expires_in");
								AppPreference.saveLoginInfo(RegisterProve.this, AppPreference.TYPE_QQ, openid, token, expires_in);
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
							isRequest = false;
							toast(arg0.toString());
						}
					};
					mTencent.login(RegisterProve.this, "all", listener);
				} else {
					TencentLogin();
				}
			}
			break;
		case R.id.llSina:
			if (!isRequest) {
				isRequest = true;
				Oauth2AccessToken token = new Oauth2AccessToken();
				if (!AppPreference.getUserPersistent(RegisterProve.this, AppPreference.SINA_ID).equals("")) {
					token.setUid(AppPreference.getUserPersistent(RegisterProve.this, AppPreference.SINA_ID));
					token.setToken(AppPreference.getUserPersistent(RegisterProve.this, AppPreference.SINA_TOKEN));
					token.setExpiresTime(Long.parseLong(AppPreference.getUserPersistent(RegisterProve.this, AppPreference.SINA_EXPIRES)));
				}
				if (token.isSessionValid()) {
					mAccessToken = token;
					SinaLogin();
				} else {
					if (checkSinaPackage()) {
						mSsoHandler = new SsoHandler(RegisterProve.this, mWeiboAuth);
						mSsoHandler.authorize(new AuthListener());
					} else {
						mWeiboAuth.anthorize(new AuthListener());
					}
				}
			}
			break;
		case R.id.tvRight:
			if (!isRequest) {
				isRequest = true;
				if (TextUtils.isEmpty(etProveNum.getText().toString().trim())) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("请输入验证码").withEffect(Effectstype.Shake).show();
					}
					isRequest = false;
					return;
				} else {
					IdentifyNum();
				}
			}
			break;
		case R.id.ivLeft:
			onBackPressed();
			break;
		}
	}

	/**
	 * 验证验证码,Ps：注册和找回密码的验证接口一样
	 */
	private void IdentifyNum() {
		mLoadingDialog.setMessage("正在验证...");
		mLoadingDialog.show();
		RegisterPhoneIdentifyNumParam param = new RegisterPhoneIdentifyNumParam(RegisterProve.this, phoneNum.replace(" ", ""), etProveNum.getText().toString().trim());
		HttpStringPost task = new HttpStringPost(RegisterProve.this, param.getUrl(), new ResponseListener() {

			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (type == 1) {
					Intent intent = new Intent(RegisterProve.this, RegisterData.class);
					startActivity(intent);
					finishActivity();
				} else {
					Intent mIntent = new Intent(RegisterProve.this, ResetPassword.class);
					startActivity(mIntent);
					finishActivity();
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
	
	/**
	 * 注册时重新获取验证码
	 * @param content
	 */
	private void getIdentifyNum(String content) {
		mLoadingDialog.setMessage("正在获取...");
		mLoadingDialog.show();
		RegisterPhoneGetNumParam param = new RegisterPhoneGetNumParam(RegisterProve.this, content.replace(" ", ""));
		HttpStringPost task = new HttpStringPost(RegisterProve.this, param.getUrl(), new ResponseListener() {

			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				second = 60;
				tvTime.setVisibility(View.VISIBLE);
				tvResend.setTextColor(0xff696969);
				llResend.setBackgroundColor(0x00000000);
				timerTask = new TimerTask() {
					@Override
					public void run() {
						handler.sendEmptyMessage(0);
					}
				};
				timer.schedule(timerTask, 200, 1000);
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
	
	class AuthListener implements WeiboAuthListener {
		@Override
		public void onComplete(Bundle values) {
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (Constant.SHOW_LOG) {
				Log.e("aaaaaaaaaaa", "uid: " + mAccessToken.getUid() + "\n" + "token:" + mAccessToken.getToken() + "\n" + "expirestime: " + mAccessToken.getExpiresTime());
			}
			AppPreference.saveLoginInfo(RegisterProve.this, AppPreference.TYPE_SINA, mAccessToken.getUid(), mAccessToken.getToken(), String.valueOf(mAccessToken.getExpiresTime()));
			SinaLogin();
		}

		@Override
		public void onCancel() {
			isRequest = false;
		}

		@Override
		public void onWeiboException(WeiboException e) {
			isRequest = false;
		}
	}
	
	/**
	 * 新浪微博登录
	 */
	private void SinaLogin() {
		mLoadingDialog.setMessage("正在登录...");
		mLoadingDialog.show();
		LoginThirdParam params = new LoginThirdParam(RegisterProve.this, 3, mAccessToken.getUid(), mAccessToken.getToken(), String.valueOf(mAccessToken.getExpiresTime()));
		HttpStringPost post = new HttpStringPost(RegisterProve.this, params.getUrl(), new ResponseListener() {

			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				try {
					JSONObject object = new JSONObject(result);
					if (object.has("is_regist") && object.getInt("is_regist") == 0) {
						Intent intent = new Intent(RegisterProve.this, RegisterData.class);
						startActivity(intent);
						finish();
					} else {
						Intent intent = new Intent(RegisterProve.this, IndexHome.class);
						startActivity(intent);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						finish();
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
		LoginThirdParam params = new LoginThirdParam(RegisterProve.this, 4, AppPreference.getUserPersistent(RegisterProve.this, AppPreference.QQ_ID), AppPreference.getUserPersistent(RegisterProve.this, AppPreference.QQ_TOKEN), AppPreference.getUserPersistent(RegisterProve.this, AppPreference.QQ_EXPIRES));
		HttpStringPost post = new HttpStringPost(RegisterProve.this, params.getUrl(), new ResponseListener() {

			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				try {
					JSONObject object = new JSONObject(result);
					if (object.has("is_regist") && object.getInt("is_regist") == 0) {
						Intent intent = new Intent(RegisterProve.this, RegisterData.class);
						startActivity(intent);
						finish();
					} else {
						Intent intent = new Intent(RegisterProve.this, IndexHome.class);
						startActivity(intent);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						finish();
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

}
