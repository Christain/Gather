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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.dialog.LoadingDialog.OnDismissListener;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.MultipartRequest;
import com.gather.android.http.ResponseListener;
import com.gather.android.manage.IntentManage;
import com.gather.android.model.InterestStringList;
import com.gather.android.model.RegisterDataModel;
import com.gather.android.params.RegisterGetInterestParam;
import com.gather.android.params.RegisterUploadPhotoParam;
import com.gather.android.params.RegisterUploadUserInfoParam;
import com.gather.android.utils.BitmapUtils;
import com.gather.android.widget.ChoosePicAlert;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;

@SuppressLint("InflateParams")
public class RegisterIcon extends SwipeBackActivity implements OnClickListener {

	private ImageView ivLeft, ivRight, ivPhoto;
	private TextView tvTitle, tvRight, tvTips;
	private View actionbarView;
	private LoadingDialog mLoadingDialog;
	private DialogTipsBuilder dialog;
	private RegisterDataModel model;

	private File mIconFile = null;
	private Bitmap mIconBmp = null;
	private Uri imgUri;
	private boolean hasIcon = false;
	private boolean isRequest = false;

	@Override
	protected int layoutResId() {
		return R.layout.register_icon;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("MODEL")) {
			model = (RegisterDataModel) intent.getSerializableExtra("MODEL");
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
				tvTitle.setText("上传头像");
				tvRight.setText("下一步");
				tvRight.setOnClickListener(this);
			}

			this.dialog = DialogTipsBuilder.getInstance(RegisterIcon.this);
			this.mLoadingDialog = LoadingDialog.createDialog(RegisterIcon.this, true);
			this.ivPhoto = (ImageView) findViewById(R.id.ivPhoto);
			this.tvTips = (TextView) findViewById(R.id.tvTips);

			this.ivPhoto.setOnClickListener(this);
			this.mLoadingDialog.setDismissListener(new OnDismissListener() {
				@Override
				public void OnDismiss() {
					isRequest = false;
				}
			});
			
			this.init();
		}
	}

	private void init() {
		if (model != null && model.getPhotoPath().length() > 2) {
			hasIcon = true;
			mIconFile = new File(model.getPhotoPath());
			ivPhoto.setImageBitmap(BitmapUtils.getRoundBitmap(BitmapUtils.getBitmapFromFile(mIconFile, -1)));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			if (hasIcon) {
				model.setPhotoPath(mIconFile.getPath());
				Intent intent = new Intent();
				intent.putExtra("MODEL", model);
				setResult(RESULT_OK, intent);
			}
			finishActivity();
			break;
		case R.id.tvRight:
			if (!isRequest) {
				isRequest = true;
				if (hasIcon) {
					UploadPhoto();
				} else {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("请上传头像").withEffect(Effectstype.Shake).show();
					}
					isRequest = false;
				}
			}
			break;
		case R.id.ivPhoto:
			ChoosePicAlert.showAlert(RegisterIcon.this, "修改个人头像", new String[] { "相机拍照", "相册选取" }, null, new ChoosePicAlert.OnAlertSelectId() {
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
		}
	}
	
	/**
	 * 上传头像
	 */
	private void UploadPhoto() {
		mLoadingDialog.setMessage("正在提交...");
		mLoadingDialog.show();
		RegisterUploadPhotoParam param = new RegisterUploadPhotoParam(RegisterIcon.this, mIconFile);
		MultipartRequest task = new MultipartRequest(RegisterIcon.this, param.getUrl(), new ResponseListener() {
			
			@Override
			public void success(int code, String msg, String result) {
				try {
					JSONObject object = new JSONObject(result);
					UploadUserInfo(object.getInt("imgId"));
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
	
	/**
	 * 完善资料（上传头像成功后，根据返回的头像ID，提交完善的资料）
	 * @param id
	 */
	private void UploadUserInfo(int id) {
		RegisterUploadUserInfoParam param = new RegisterUploadUserInfoParam(RegisterIcon.this, model.getPassword(), model.getNickname(), model.getSex(), model.getBirthday(), model.getAddress(), model.getEmail(), id);
		HttpStringPost task = new HttpStringPost(RegisterIcon.this, param.getUrl(), new ResponseListener() {
			
			@Override
			public void success(int code, String msg, String result) {
				getInterestString();
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
	 * 获取兴趣标签
	 */
	private void getInterestString() {
		mLoadingDialog.setMessage("获取标签中...");
		mLoadingDialog.show();
		RegisterGetInterestParam param = new RegisterGetInterestParam(RegisterIcon.this);
		HttpStringPost task = new HttpStringPost(RegisterIcon.this, param.getUrl(), new ResponseListener() {
			
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				Gson gson = new Gson();
				InterestStringList list = gson.fromJson(result, InterestStringList.class);
				if (list != null && list.tags != null) {
					Intent intent = new Intent(RegisterIcon.this, RegisterInterest.class);
					intent.putExtra("LIST", list.tags);
					startActivity(intent);
					finish();
				} else {
					Intent intent = new Intent(RegisterIcon.this, IndexHome.class);
					startActivity(intent);
					finish();
//					if (dialog != null && !dialog.isShowing()) {
//						dialog.setMessage("获取兴趣标签失败~~").withEffect(Effectstype.Shake).show();
//					}
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
				Intent intent = new Intent(RegisterIcon.this, IndexHome.class);
				startActivity(intent);
				finish();
//				if (dialog != null && !dialog.isShowing()) {
//					dialog.setMessage(msg).withEffect(Effectstype.Shake).show();
//				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
//				if (dialog != null && !dialog.isShowing()) {
//					dialog.setMessage(error.getMsg()).withEffect(Effectstype.Shake).show();
//				}
				Intent intent = new Intent(RegisterIcon.this, IndexHome.class);
				startActivity(intent);
				finish();
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
					mIconBmp = BitmapUtils.getBitmapFromUri(RegisterIcon.this, imgUri);
					new ProgressSaveImage().execute();
				} else {
					toast("图片截取失败，请重试");
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
		ivPhoto.setImageResource(R.drawable.ic_launcher);
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
				ivPhoto.setImageBitmap(BitmapUtils.getRoundBitmap(mIconBmp));
				tvTips.setText("重新上传");
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (hasIcon) {
				model.setPhotoPath(mIconFile.getPath());
				Intent intent = new Intent();
				intent.putExtra("MODEL", model);
				setResult(RESULT_OK);
				finishActivity();
			} else {
				onBackPressed();
			}
		}
		return true;
	}

}
