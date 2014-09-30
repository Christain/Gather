package com.gather.android.baseclass;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.gather.android.activity.LoginIndex;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.RequestManager;
import com.gather.android.http.ResponseListener;
import com.gather.android.manage.AppManage;
import com.gather.android.params.ExitParam;
import com.gather.android.preference.AppPreference;
import com.nostra13.universalimageloader.core.ImageLoader;

public abstract class BaseActivity extends ActionBarActivity {

	private Context context;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layoutResId());
		this.context = this;
		AppManage appManage = AppManage.getInstance();
		appManage.addActivity(this);
		onCreateActivity(savedInstanceState);
	}

	/**
	 * 网络请求
	 * 
	 * @param request
	 */
	protected void executeRequest(Request<?> request) {
		RequestManager.addRequest(request, this);
	}
	
	/**
	 * 重新登录
	 */
	protected void needLogin(String msg){
		DialogTipsBuilder dialog = DialogTipsBuilder.getInstance(context);
		dialog.withDuration(400).withEffect(Effectstype.Fall).setMessage(msg).isCancelableOnTouchOutside(false).setOnClick(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Exit();
				AppPreference.clearInfo(context);
				Intent intent = new Intent(context, LoginIndex.class);
				startActivity(intent);
				finishActivity();
			}
		}).setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
					return true;
				} else {
					return false;
				}
			}
		});
		dialog.show();
	}
	
	/**
	 * 退出登录
	 */
	protected void Exit(){
		ExitParam param = new ExitParam(context);
		HttpStringPost task = new HttpStringPost(context, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				
			}
			
			@Override
			public void relogin(String msg) {
				
			}
			
			@Override
			public void error(int code, String msg) {
				
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
			}
		}, param.getParameters());
		executeRequest(task);
	}

	@Override
	public void finish() {
		AppManage appManage = AppManage.getInstance();
		appManage.finishActivity(this);
	}

	public void finishActivity() {
		super.finish();
	}

	/**
	 * 退出程序
	 */
	public void exitApp() {
		AppManage appManage = AppManage.getInstance();
		appManage.exit(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		RequestManager.cancelAll(this);
	}

	/**
	 * toast message
	 * 
	 * @param text
	 */
	protected void toast(String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}


	protected abstract int layoutResId();


	protected abstract void onCreateActivity(Bundle savedInstanceState);

}
