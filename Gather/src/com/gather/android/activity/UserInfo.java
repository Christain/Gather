package com.gather.android.activity;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.DialogDateSelect;
import com.gather.android.dialog.DialogDateSelect.OnDateClickListener;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.dialog.LoadingDialog.OnDismissListener;
import com.gather.android.http.HttpGetUtil;
import com.gather.android.http.HttpStringGet;
import com.gather.android.http.MultipartRequest;
import com.gather.android.http.ResponseListener;
import com.gather.android.manage.IntentManage;
import com.gather.android.model.UserInfoModel;
import com.gather.android.params.RegisterUploadPhotoParam;
import com.gather.android.params.UploadUserInfoParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.BitmapUtils;
import com.gather.android.utils.TimeUtil;
import com.gather.android.widget.ChoosePicAlert;
import com.gather.android.widget.MMAlert;
import com.gather.android.widget.OverScrollView;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

@SuppressLint("InflateParams")
public class UserInfo extends SwipeBackActivity implements OnClickListener {

	private View actionbarView;
	private ImageView ivLeft, ivRight;
	private TextView tvTitle, tvRight;

	/************** 个人信息控件 ********************/
	private LinearLayout llUserIcon, llNickName, llUserSex, llUserAge, llUserName, llUserPhone, llUserAddress, llUserSina, llUserTencent;
	private ImageView ivUserIcon;
	private TextView tvNickName, tvUserSex, tvUserAge, tvUserName, tvUserPhone, tvUserAddress, tvUserSina, tvUserTencent;
	private OverScrollView scrollView;
	private Animation alphaIn;

	private DialogTipsBuilder dialog;
	private LoadingDialog mLoadingDialog;
	private boolean isRequest = false;
	private UserInfoModel model = null;
	
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	private String nickName, userName, contact_phone, contact_address;
	private int sex, age;
	private File mIconFile = null;
	private Bitmap mIconBmp = null;
	private Uri imgUri;
	private boolean hasIcon = false;
	private int photoId = 0;
	
	public static final int REQUEST_NICK_NAME = 0x21;
	public static final int REQUEST_USER_NAME = 0x22;
	public static final int REQUEST_CONTACT_PHONE = 0x23;
	public static final int REQUEST_CONTACT_ADDRESS = 0x24;
	
	/**********第三方绑定*************/
	private Tencent mTencent;
	private IUiListener listener;
	private WeiboAuth mWeiboAuth;
	private Oauth2AccessToken mAccessToken;
	private SsoHandler mSsoHandler;
	

	@Override
	protected int layoutResId() {
		return R.layout.user_info;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("MODEL")) {
			model = (UserInfoModel) intent.getSerializableExtra("MODEL");
		}
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

		this.mTencent = Tencent.createInstance(Constant.TENCENT_APPID, this.getApplicationContext());
		this.mWeiboAuth = new WeiboAuth(this, Constant.SINA_APPID, Constant.SINA_CALLBACK_URL, Constant.SINA_SCOPE);
		this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.icon_share_tencent).showImageForEmptyUri(R.drawable.icon_share_tencent).showImageOnFail(R.drawable.icon_share_tencent).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new RoundedBitmapDisplayer(90)).bitmapConfig(Bitmap.Config.RGB_565).build();
		this.dialog = DialogTipsBuilder.getInstance(UserInfo.this);
		this.mLoadingDialog = LoadingDialog.createDialog(UserInfo.this, true);
		this.mLoadingDialog.setDismissListener(new OnDismissListener() {
			@Override
			public void OnDismiss() {
				isRequest = false;
			}
		});
		this.alphaIn = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
		this.scrollView = (OverScrollView) findViewById(R.id.scrollView);
		this.llUserIcon = (LinearLayout) findViewById(R.id.llUserIcon);
		this.llNickName = (LinearLayout) findViewById(R.id.llNickName);
		this.llUserSex = (LinearLayout) findViewById(R.id.llUserSex);
		this.llUserAge = (LinearLayout) findViewById(R.id.llUserAge);
		this.llUserName = (LinearLayout) findViewById(R.id.llUserName);
		this.llUserPhone = (LinearLayout) findViewById(R.id.llUserPhone);
		this.llUserAddress = (LinearLayout) findViewById(R.id.llUserAddress);
		this.llUserSina = (LinearLayout) findViewById(R.id.llUserSina);
		this.llUserTencent = (LinearLayout) findViewById(R.id.llUserTencent);

		this.tvNickName = (TextView) findViewById(R.id.tvNickName);
		this.tvUserSex = (TextView) findViewById(R.id.tvUserSex);
		this.tvUserAge = (TextView) findViewById(R.id.tvUserAge);
		this.tvUserName = (TextView) findViewById(R.id.tvUserName);
		this.tvUserPhone = (TextView) findViewById(R.id.tvUserPhone);
		this.tvUserAddress = (TextView) findViewById(R.id.tvUserAddress);
		this.tvUserSina = (TextView) findViewById(R.id.tvUserSina);
		this.tvUserTencent = (TextView) findViewById(R.id.tvUserTencent);
		this.ivUserIcon = (ImageView) findViewById(R.id.ivUserIcon);

		this.llUserIcon.setOnClickListener(this);
		this.llNickName.setOnClickListener(this);
		this.llUserSex.setOnClickListener(this);
		this.llUserAge.setOnClickListener(this);
		this.llUserName.setOnClickListener(this);
		this.llUserPhone.setOnClickListener(this);
		this.llUserAddress.setOnClickListener(this);
		this.llUserSina.setOnClickListener(this);
		this.llUserTencent.setOnClickListener(this);
		
		if (model == null) {
			scrollView.setVisibility(View.GONE);
			getUserInfo();
		} else {
			scrollView.setVisibility(View.VISIBLE);
			setUserInfo();
		}
	}
	
	/**
	 * 获取个人信息
	 */
	private void getUserInfo() {
		mLoadingDialog.setMessage("获取个人信息...");
		mLoadingDialog.show();
		HttpGetUtil param = new HttpGetUtil(UserInfo.this, "act/userInfo/getInfo");
		HttpStringGet task = new HttpStringGet(UserInfo.this, param.toString(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				try {
					JSONObject object = new JSONObject(result);
					Gson gson = new Gson();
					model = gson.fromJson(object.getString("uInfo"), UserInfoModel.class);
					if (model != null ) {
						AppPreference.saveUserInfo(UserInfo.this, model);
						setUserInfo();
						scrollView.setVisibility(View.VISIBLE);
						scrollView.startAnimation(alphaIn);
					} else {
						Toast.makeText(UserInfo.this, "获取个人信息失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(UserInfo.this, "个人信息解析失败", Toast.LENGTH_SHORT).show();
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
				Toast.makeText(UserInfo.this, msg, Toast.LENGTH_SHORT).show();
				finish();
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				Toast.makeText(UserInfo.this, "获取个人信息失败", Toast.LENGTH_SHORT).show();
				finish();
			}
		});
		executeRequest(task);
	}
	
	/**
	 * 显示个人信息
	 */
	private void setUserInfo(){
		nickName = model.getNick_name();
		userName = model.getReal_name();
		contact_phone = model.getPho_num();
		contact_address = model.getAddress();
		sex = model.getSex();
		age = TimeUtil.getUserAge(model.getBirth());
		
		imageLoader.displayImage(model.getHead_img_url(), ivUserIcon, options);
		tvNickName.setText(nickName);
		if (sex == 1) {
			tvUserSex.setText("男");
		} else {
			tvUserSex.setText("女");
		}
		if (age == -1) {
			tvUserAge.setText("0");
		} else {
			tvUserAge.setText(age +"");
		}
		tvUserName.setText(userName);
		if (contact_phone.length() == 11) {
			tvUserPhone.setText(ProgressPhoneNum(contact_phone));
		} else {
			contact_phone = "";
			tvUserPhone.setText(contact_phone);
		}
		tvUserAddress.setText(contact_address);
		if (!model.getSina_openid().equals("")) {
			
		} else {
			tvUserSina.setText("未绑定");
			tvUserSina.setTextColor(0xFF999999);
			tvUserSina.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);
		}
		if (!model.getQq_openid().equals("")) {
			
		} else {
			tvUserTencent.setText("未绑定");
			tvUserTencent.setTextColor(0xFF999999);
			tvUserTencent.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			finish();
			break;
		case R.id.tvRight:
			if (!isRequest) {
				isRequest = true;
				if (hasIcon) {
					uploadPhoto();
				} else {
					uploadUserInfo();
				}
			}
			break;
		case R.id.llUserIcon:
			ChoosePicAlert.showAlert(UserInfo.this, "修改个人头像", new String[] { "相机拍照", "相册选取" }, null, new ChoosePicAlert.OnAlertSelectId() {
				public void onDismissed() {
				}

				public void onClick(int whichButton) {
					Intent intent;
					switch (whichButton) {
					case 0:// 拍照
						mIconFile = getImageTempFile();
						intent = IntentManage.getSystemCameraIntent(mIconFile);
						startActivityForResult(intent, IntentManage.REQUEST_CODE_TAKE_PHOTO);
						break;
					case 1:// 相册
						mIconFile = getImageTempFile();
						imgUri = Uri.fromFile(mIconFile);
						intent = new Intent(Intent.ACTION_PICK);
						intent.setType("image/*");
						intent.putExtra("crop", "true");
						intent.putExtra("aspectX", 1);
						intent.putExtra("aspectY", 1);
						intent.putExtra("outputX", 500);
						intent.putExtra("outputY", 500);
						intent.putExtra("scale", true);
						intent.putExtra("return-data", false);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
						intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
						intent.putExtra("noFaceDetection", false);
						startActivityForResult(intent, IntentManage.REQUEST_CODE_CROP_IMAGE);
						break;
					}
				}
			});
			break;
		case R.id.llNickName:
			Intent intent  = new Intent(UserInfo.this, EditData.class);
			intent.putExtra("CONTENT", nickName);
			intent.putExtra("TYPE", REQUEST_NICK_NAME);
			startActivityForResult(intent, REQUEST_NICK_NAME);
			break;
		case R.id.llUserSex:
			MMAlert.showAlert(UserInfo.this, "修改性别", new String[] { "男", "女" }, null, new MMAlert.OnAlertSelectId() {
				public void onDismissed() {
				}

				public void onClick(int whichButton) {
					switch (whichButton) {
					case 0:
						sex = 1;
						tvUserSex.setText("男");
						break;
					case 1:
						sex = 2;
						tvUserSex.setText("女");
						break;
					}
				}
			});
			break;
		case R.id.llUserAge:
			DialogDateSelect dialogDate = new DialogDateSelect(UserInfo.this, R.style.dialog_date);
			dialogDate.withDuration(400).withEffect(Effectstype.Fall).setOnSureClick(new OnDateClickListener() {
				@Override
				public void onDateListener(String date) {
					if (TimeUtil.getUserAge(date) == -1) {
						if (dialog != null && !dialog.isShowing()) {
							dialog.setMessage("年龄选择错误").withEffect(Effectstype.Shake).show();
						}
					} else {
						model.setBirth(date);
						age = TimeUtil.getUserAge(date);
						tvUserAge.setText(age +"");
					}
				}
			}).show();
			break;
		case R.id.llUserName:
			Intent name  = new Intent(UserInfo.this, EditData.class);
			name.putExtra("CONTENT", userName);
			name.putExtra("TYPE", REQUEST_USER_NAME);
			startActivityForResult(name, REQUEST_USER_NAME);
			break;
		case R.id.llUserPhone:
			Intent phone  = new Intent(UserInfo.this, EditData.class);
			phone.putExtra("CONTENT", contact_phone);
			phone.putExtra("TYPE", REQUEST_CONTACT_PHONE);
			startActivityForResult(phone, REQUEST_CONTACT_PHONE);
			break;
		case R.id.llUserAddress:
			Intent address  = new Intent(UserInfo.this, EditData.class);
			address.putExtra("CONTENT", contact_address);
			address.putExtra("TYPE", REQUEST_CONTACT_ADDRESS);
			startActivityForResult(address, REQUEST_CONTACT_ADDRESS);
			break;
		case R.id.llUserSina:
			
			break;
		case R.id.llUserTencent:

			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case IntentManage.REQUEST_CODE_TAKE_PHOTO:// 拍照
				imgUri = Uri.fromFile(mIconFile);
				tailor(imgUri);
				break;
			case IntentManage.REQUEST_CODE_ALBUM:// 相册
				imgUri = Uri.fromFile(mIconFile);
				tailor(imgUri);
				break;
			case IntentManage.REQUEST_CODE_CROP_IMAGE:// 裁剪图片
				if (imgUri != null) {
					recycleBmp();
					mIconBmp = BitmapUtils.getBitmapFromUri(UserInfo.this, imgUri);
					new ProgressSaveImage().execute();
				} else {
					toast("图片截取失败，请重试");
				}
				break;
			case REQUEST_NICK_NAME:
				if (data != null) {
					nickName = data.getStringExtra("CONTENT");
					tvNickName.setText(nickName);
				}
				break;
			case REQUEST_USER_NAME:
				if (data != null) {
					userName = data.getStringExtra("CONTENT");
					tvUserName.setText(userName);
				}
				break;
			case REQUEST_CONTACT_PHONE:
				if (data != null) {
					contact_phone = data.getStringExtra("CONTENT");
					tvUserPhone.setText(contact_phone);
				}
				break;
			case REQUEST_CONTACT_ADDRESS:
				if (data != null) {
					contact_address = data.getStringExtra("CONTENT");
					tvUserAddress.setText(contact_address);
				}
				break;
			}
		}
	}

	private void tailor(Uri uri) { // 截图
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 500);
		intent.putExtra("outputY", 500);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, IntentManage.REQUEST_CODE_CROP_IMAGE);
	}

	private File getImageTempFile() {
		File file = new File(Constant.UPLOAD_FILES_DIR_PATH + System.currentTimeMillis() + ".jpg");
		return file;
	}

	private void recycleBmp() {
		ivUserIcon.setImageResource(R.drawable.icon_share_tencent);
		if (mIconBmp != null && !mIconBmp.isRecycled()) {
			mIconBmp.recycle();
			System.gc();
		}
		mIconBmp = null;
	}

	private class ProgressSaveImage extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mLoadingDialog.setMessage("正在处理头像");
			mLoadingDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (mIconBmp != null && !mIconBmp.isRecycled()) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
				mLoadingDialog.dismiss();
			}
			if (result) {
				ivUserIcon.setImageBitmap(BitmapUtils.getRoundBitmap(mIconBmp));
				hasIcon = true;
				imgUri = null;
			} else {
				Toast.makeText(getApplicationContext(), "头像处理失败，请重试", Toast.LENGTH_LONG).show();
				recycleBmp();
				hasIcon = false;
				mIconFile = null;
				imgUri = null;
			}
		}
	}
	
	/**
	 * 上传头像
	 */
	private void uploadPhoto(){
		mLoadingDialog.setMessage("正在提交...");
		mLoadingDialog.show();
		RegisterUploadPhotoParam param = new RegisterUploadPhotoParam(UserInfo.this, mIconFile);
		MultipartRequest task = new MultipartRequest(UserInfo.this, param.getUrl(), new ResponseListener() {
			
			@Override
			public void success(int code, String msg, String result) {
				try {
					JSONObject object = new JSONObject(result);
					photoId = object.getInt("imgId");
					uploadUserInfo();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void relogin(String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					isRequest = true;
					mLoadingDialog.dismiss();
				}
				needLogin(msg);
			}
			
			@Override
			public void error(int code, String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					isRequest = true;
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
					isRequest = true;
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
	 * 上传个人信息
	 */
	private void uploadUserInfo() {
		if (photoId == 0) {
			if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
				mLoadingDialog.setMessage("提交中...");
				mLoadingDialog.show();
			}
		}
		UploadUserInfoParam param = new UploadUserInfoParam(UserInfo.this, nickName, sex, model.getBirth(), contact_address, userName, contact_phone, photoId);
		MultipartRequest task = new MultipartRequest(UserInfo.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (photoId != 0) {
					photoId = 0;
					model.setHead_img_url("file:///mnt/sdcard/Gather/upload/" + mIconFile.getName());
//					model.setHead_img_url(mIconFile.getAbsolutePath());
				}
				model.setNick_name(nickName);
				model.setSex(sex);
				model.setReal_name(userName);
				model.setPho_num(contact_phone);
				model.setAddress(contact_address);
				Intent intent = new Intent();
				intent.putExtra("MODEL", model);
				setResult(RESULT_OK, intent);
				finish();
			}
			
			@Override
			public void relogin(String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					isRequest = true;
					mLoadingDialog.dismiss();
				}
				needLogin(msg);
			}
			
			@Override
			public void error(int code, String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					isRequest = true;
					mLoadingDialog.dismiss();
				}
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage(msg).withEffect(Effectstype.Shake).show();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					isRequest = true;
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
	 * Tencent验证
	 */
	private void IdentifyTencent() {
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
					AppPreference.saveThirdLoginInfo(UserInfo.this, AppPreference.TYPE_QQ, openid, token, expires_in);
				} catch (JSONException e) {
					e.printStackTrace();
				}
//				TencentLogin();
			}

			@Override
			public void onError(UiError arg0) {
				isRequest = false;
				toast(arg0.toString());
			}
		};
		mTencent.login(UserInfo.this, "all", listener);
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
