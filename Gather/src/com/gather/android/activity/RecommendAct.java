package com.gather.android.activity;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.gather.android.R;
import com.gather.android.application.GatherApplication;
import com.gather.android.application.GatherApplication.LocationListener;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.DialogChoiceBuilder;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.dialog.LoadingDialog.OnDismissListener;
import com.gather.android.http.MultipartRequest;
import com.gather.android.http.ResponseListener;
import com.gather.android.manage.IntentManage;
import com.gather.android.manage.PhoneManage;
import com.gather.android.params.RecommendActParam;
import com.gather.android.params.UploadActImageParam;
import com.gather.android.utils.BitmapUtils;
import com.gather.android.widget.ChoosePicAlert;
import com.gather.android.widget.swipeback.SwipeBackActivity;

@SuppressLint("InflateParams")
public class RecommendAct extends SwipeBackActivity implements OnClickListener {
	
	private View actionbarView;
	private ImageView ivLeft, ivRight;
	private TextView tvTitle, tvRight;
	
	/*******推荐活动控件*******/
	private ImageView ivAddPic, ivPic;
	private EditText etTitle, etTime, etAddress, etContact, etMore;
	private TextView tvLocation, tvCommit;
	private LinearLayout llLocation;
	
	private DialogTipsBuilder dialog;
	private LoadingDialog mLoadingDialog;
	private boolean isRequest = false;
	
	private File mIconFile = null;
	private Bitmap mIconBmp = null;
	private String path = "";
	private boolean hasPic = false;
	private DisplayMetrics metrics;
	
	private LocationClient mLocationClient;
	private GatherApplication application;

	@Override
	protected int layoutResId() {
		return R.layout.recommend_act;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
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
			this.tvTitle.setText("向我们推荐活动");
			this.tvRight.setVisibility(View.GONE);
		}	
		
		this.mLocationClient = ((GatherApplication)getApplication()).mLocationClient;
		this.mLoadingDialog = LoadingDialog.createDialog(RecommendAct.this, true);
		this.dialog = DialogTipsBuilder.getInstance(RecommendAct.this);
		this.ivAddPic = (ImageView) findViewById(R.id.ivAddPic);
		this.ivAddPic.setVisibility(View.VISIBLE);
		this.ivPic = (ImageView) findViewById(R.id.ivPic);
		this.ivPic.setVisibility(View.GONE);
		this.etTitle = (EditText) findViewById(R.id.etTitle);
		this.etTime = (EditText) findViewById(R.id.etTime);
		this.etAddress = (EditText) findViewById(R.id.etAddress);
		this.etContact = (EditText) findViewById(R.id.etContact);
		this.etMore = (EditText) findViewById(R.id.etMore);
		this.tvLocation = (TextView) findViewById(R.id.tvLocation);
		this.tvCommit = (TextView) findViewById(R.id.tvCommit);
		this.llLocation = (LinearLayout) findViewById(R.id.llLocation);
		
	    metrics = getResources().getDisplayMetrics();
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) ivPic.getLayoutParams();
		params.width = metrics.widthPixels - PhoneManage.dip2px(30, metrics.density);
		params.height = params.width * 17 / 36;
		ivPic.setLayoutParams(params);
		
		this.ivAddPic.setOnClickListener(this);
		this.ivPic.setOnClickListener(this);
		this.tvCommit.setOnClickListener(this);
		this.llLocation.setOnClickListener(this);
		
		this.mLoadingDialog.setDismissListener(new OnDismissListener() {
			@Override
			public void OnDismiss() {
				isRequest = false;
			}
		});
		this.application = (GatherApplication) getApplication();
		if (application.mLocation == null) {
			this.tvLocation.setText("正在定位...");
		} else {
			this.tvLocation.setText(application.mLocation.getAddrStr());
		}
		this.application.setLocationListener(new LocationListener() {
			@Override
			public void OnResultLocation(BDLocation location) {
				tvLocation.setText(location.getAddrStr());
			}
		});
		
		this.initLocation();
		
		this.etTime.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					etTime.setBackgroundResource(R.drawable.bg_recommend_edit_no_focus);
					etTime.setPadding(getResources().getDimensionPixelOffset(R.dimen.recommend_edit_padding_left), 0, 0, 0);
				} else {
					etTime.setBackgroundResource(R.drawable.bg_recommend_edit_has_focus);
					etTime.setPadding(getResources().getDimensionPixelOffset(R.dimen.recommend_edit_padding_left), 0, 0, 0);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
		});
		this.etAddress.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					etAddress.setBackgroundResource(R.drawable.bg_recommend_edit_no_focus);
					etAddress.setPadding(getResources().getDimensionPixelOffset(R.dimen.recommend_edit_padding_left), 0, 0, 0);
				} else {
					etAddress.setBackgroundResource(R.drawable.bg_recommend_edit_has_focus);
					etAddress.setPadding(getResources().getDimensionPixelOffset(R.dimen.recommend_edit_padding_left), 0, 0, 0);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
		});
		this.etContact.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					etContact.setBackgroundResource(R.drawable.bg_recommend_edit_no_focus);
					etContact.setPadding(getResources().getDimensionPixelOffset(R.dimen.recommend_edit_padding_left), 0, 0, 0);
				} else {
					etContact.setBackgroundResource(R.drawable.bg_recommend_edit_has_focus);
					etContact.setPadding(getResources().getDimensionPixelOffset(R.dimen.recommend_edit_padding_left), 0, 0, 0);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
		});
		this.etMore.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					etMore.setBackgroundResource(R.drawable.bg_recommend_edit_no_focus);
					etMore.setPadding(15, 15, 15, 15);
				} else {
					etMore.setBackgroundResource(R.drawable.bg_recommend_edit_has_focus);
					etMore.setPadding(15, 15, 15, 15);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			if (hasPic || !TextUtils.isEmpty(etTitle.getText().toString().trim())) {
				DialogChoiceBuilder dialog = DialogChoiceBuilder.getInstance(RecommendAct.this);
				dialog.setMessage("您确定放弃编辑返回吗？").withDuration(400).withEffect(Effectstype.SlideBottom).setOnClick(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						finish();
					}
				}).show();
			} else {
				finish();
			}
			break;
		case R.id.ivAddPic:
		case R.id.ivPic:
			ChoosePicAlert.showAlert(RecommendAct.this, "活动图片", new String[] { "相机拍照", "相册选取" }, null, new ChoosePicAlert.OnAlertSelectId() {
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
						intent = IntentManage.getSystemAlbumIntent();
						startActivityForResult(intent, IntentManage.REQUEST_CODE_ALBUM);
						break;
					}
				}
			});
			break;
		case R.id.tvCommit:
			if (!isRequest) {
				isRequest = true;
				if (!hasPic) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("请选择一张活动图片").withEffect(Effectstype.Fall).show();
					}
					isRequest = false;
					return;
				}
				if (TextUtils.isEmpty(etTitle.getText().toString().trim())) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("请填写活动名称").withEffect(Effectstype.Fall).show();
					}
					isRequest = false;
					return;
				}
				uploadImg();
			}
			break;
		case R.id.llLocation:
			this.tvLocation.setText("正在定位...");
			initLocation();
			break;
		}
	}
	
	/**
	 * 上传图片（成功后返回图片ID）
	 */
	private void uploadImg() {
		mLoadingDialog.setMessage("正在提交...");
		mLoadingDialog.show();
		UploadActImageParam param = new UploadActImageParam(RecommendAct.this, mIconFile);
		MultipartRequest task = new MultipartRequest(RecommendAct.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				try {
					JSONObject object = new JSONObject(result);
					if (object.has("imgId")) {
						uploadActInfo(object.getInt("imgId"));
					} else {
						if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
							isRequest = false;
							mLoadingDialog.dismiss();
						}
						if (dialog != null && !dialog.isShowing()) {
							dialog.setMessage("提交失败~~").withEffect(Effectstype.Shake).show();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void relogin(String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					isRequest = false;
					mLoadingDialog.dismiss();
				}
				needLogin(msg);
			}
			
			@Override
			public void error(int code, String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					isRequest = false;
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
					isRequest = false;
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
	 * 上传推荐活动的信息
	 */
	private void uploadActInfo(int imgId) {
		RecommendActParam param = new RecommendActParam(RecommendAct.this, imgId, etTitle.getText().toString(), etTime.getText().toString(), etAddress.getText().toString(), etContact.getText().toString(), etMore.getText().toString());
		MultipartRequest task = new MultipartRequest(RecommendAct.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				finish();
				toast("谢谢您推荐的活动");
			}
			
			@Override
			public void relogin(String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					isRequest = false;
					mLoadingDialog.dismiss();
				}
				needLogin(msg);
			}
			
			@Override
			public void error(int code, String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					isRequest = false;
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
					isRequest = false;
					mLoadingDialog.dismiss();
				}
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage(error.getMsg()).withEffect(Effectstype.Shake).show();
				}
			}
		}, param.getParameters());
		executeRequest(task);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case IntentManage.REQUEST_CODE_TAKE_PHOTO:// 拍照
//				path = mIconFile.getPath();
//				File files = new File(path);
				mIconBmp = BitmapUtils.getBitmapFromFile(mIconFile, -1);
				new ProgressSaveImage().execute();
				break;
			case IntentManage.REQUEST_CODE_ALBUM:// 相册
				recycleBmp();
				Uri uri = data.getData();
				String[] proj = { MediaStore.Images.Media.DATA };
				Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
				if (actualimagecursor != null) {
					int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					actualimagecursor.moveToFirst();
					path = actualimagecursor.getString(actual_image_column_index);
					File file = new File(path);
					mIconBmp = BitmapUtils.getBitmapFromFile(file, -1);
					new ProgressSaveImage().execute();
				}
				else {
					Toast.makeText(getApplicationContext(), "图片不存在", Toast.LENGTH_SHORT).show();
				}

			}
		}
	}

	private File getImageTempFile() {
		File file = new File(Constant.UPLOAD_FILES_DIR_PATH + System.currentTimeMillis() + ".jpg");
		return file;
	}

	private void recycleBmp() {
		ivPic.setImageResource(R.drawable.default_image);
		if (mIconBmp != null && !mIconBmp.isRecycled()) {
			mIconBmp.recycle();
			System.gc();
		}
		mIconBmp = null;
	}

	/**
	 * 处理图片
	 */
	private class ProgressSaveImage extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mLoadingDialog.setMessage("正在处理图片");
			mLoadingDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (mIconBmp != null && !mIconBmp.isRecycled()) {
				return BitmapUtils.saveBitmapToImageFile(RecommendAct.this, Bitmap.CompressFormat.JPEG, mIconBmp,
					mIconFile, 100);
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
				ivAddPic.setVisibility(View.GONE);
				ivPic.setVisibility(View.VISIBLE);
				ivPic.setImageBitmap(mIconBmp);
				hasPic = true;
			} else {
				Toast.makeText(getApplicationContext(), "图片处理失败，请重试", Toast.LENGTH_LONG).show();
				recycleBmp();
				ivAddPic.setVisibility(View.VISIBLE);
				ivPic.setVisibility(View.GONE);
				hasPic = false;
				mIconFile = null;
			}
		}
	}
	
	@Override
	public void onStop() {
		mLocationClient.stop();
		super.onStop();
	}
	
	/**
	 * 初始化定位
	 */
	private void initLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
		option.setScanSpan(60000);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);//是否需要地址信息
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) { // 返回键监听
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (hasPic || !TextUtils.isEmpty(etTitle.getText().toString().trim())) {
				DialogChoiceBuilder dialog = DialogChoiceBuilder.getInstance(RecommendAct.this);
				dialog.setMessage("您确定放弃编辑返回吗？").withDuration(400).withEffect(Effectstype.SlideBottom).setOnClick(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						finish();
					}
				}).show();
			} else {
				finish();
			}
		}
		return true;
	}
}
