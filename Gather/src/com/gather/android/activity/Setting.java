package com.gather.android.activity;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.DialogChoiceBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.dialog.LoadingDialog.OnDismissListener;
import com.gather.android.preference.AppPreference;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("InflateParams")
public class Setting extends SwipeBackActivity implements OnClickListener {
	
	private View actionbarView;
	private ImageView ivLeft, ivRight;
	private TextView tvTitle, tvRight;
	
	/***********设置控件*************/
	private TextView tvRecommend, tvClear, tvAbout, tvExit;
	private RelativeLayout rlUPdate;
	private ImageView ivUpdate;
	private LoadingDialog mLoadingDialog;
	private boolean isRequest = false;

	@Override
	protected int layoutResId() {
		return R.layout.setting;
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
			this.tvTitle.setText("设置");
			this.tvRight.setVisibility(View.GONE);
		}
		
		this.mLoadingDialog = LoadingDialog.createDialog(Setting.this, true);
		this.mLoadingDialog.setDismissListener(new OnDismissListener() {
			@Override
			public void OnDismiss() {
				isRequest = false;
			}
		});
		this.tvRecommend = (TextView) findViewById(R.id.tvRecommend);
		this.tvClear = (TextView) findViewById(R.id.tvClear);
		this.tvAbout = (TextView) findViewById(R.id.tvAbout);
		this.tvExit = (TextView) findViewById(R.id.tvExit);
		this.rlUPdate = (RelativeLayout) findViewById(R.id.rlUpdate);
		this.ivUpdate = (ImageView) findViewById(R.id.ivVersion);
		
		this.tvRecommend.setOnClickListener(this);
		this.tvClear.setOnClickListener(this);
		this.tvAbout.setOnClickListener(this);
		this.tvExit.setOnClickListener(this);
		this.rlUPdate.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			finish();
			break;
		case R.id.tvRecommend:
			
			break;
		case R.id.tvClear:
			if (!isRequest) {
				final DialogChoiceBuilder clear = DialogChoiceBuilder.getInstance(Setting.this);
				clear.setMessage("您确定要清理缓存？").withDuration(400).withEffect(Effectstype.SlideBottom).setOnClick(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						isRequest  = true;
						new ProgressClear().execute();
						clear.dismiss();
					}
				}).show();
			}
			break;
		case R.id.tvAbout:
			
			break;
		case R.id.tvExit:
			DialogChoiceBuilder dialog = DialogChoiceBuilder.getInstance(Setting.this);
			dialog.setMessage("您确定要退出登录？").withDuration(400).withEffect(Effectstype.SlideBottom).setOnClick(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Exit();
					AppPreference.clearInfo(Setting.this);
					Intent intent = new Intent(Setting.this, LoginIndex.class);
					startActivity(intent);
					finish();
				}
			}).show();
			break;
		case R.id.rlUpdate:
			
			break;
		}
	}
	
	/**
	 * 清理缓存
	 */
	private class ProgressClear extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mLoadingDialog.setMessage("正在清理缓存");
			mLoadingDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			ImageLoader imageLoader = ImageLoader.getInstance(); 
			imageLoader.clearMemoryCache();
			imageLoader.clearDiskCache();
			File file = new File(Constant.UPLOAD_FILES_DIR_PATH);
			if (file != null && file.exists()) {
				File files[] = file.listFiles();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						if (files[i].isDirectory()) {
							File childs[] = files[i].listFiles();
							if (childs != null) {
								for (int j = 0; j < childs.length; j++) {
									childs[j].delete();
								}
							}
						}
						files[i].delete();
					}
					return true;
				}
			} else {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					isRequest = false;
					mLoadingDialog.dismiss();
				}
				toast("缓存文件不存在");
				return false;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
				isRequest = false;
				mLoadingDialog.dismiss();
			}
			if (result) {
				toast("清理完成");
			} else {
				toast("清理缓存失败，请重试");
			}
		}
	}

}
