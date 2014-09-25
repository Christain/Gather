package com.gather.android.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.custom.vg.list.CustomListView;
import com.custom.vg.list.OnItemClickListener;
import com.gather.android.R;
import com.gather.android.adapter.SelectInterestAdapter;
import com.gather.android.dialog.DialogChoiceBuilder;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.dialog.LoadingDialog.OnDismissListener;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.InterestStringModel;
import com.gather.android.params.RegisterUploadInterestParam;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.gather.android.widget.swipeback.SwipeBackLayout;

@SuppressLint("InflateParams")
public class RegisterInterest extends SwipeBackActivity implements OnClickListener {
	
	private ImageView ivLeft, ivRight;
	private TextView tvTitle, tvRight;
	private View actionbarView;
	private CustomListView gridview;
	private SelectInterestAdapter adapter;
	private LoadingDialog mLoadingDialog;
	private DialogTipsBuilder dialog;
	private boolean isRequest = false;
	private ArrayList<InterestStringModel> list;


	@Override
	protected int layoutResId() {
		return R.layout.register_interest;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("LIST")) {
			this.list = (ArrayList<InterestStringModel>) intent.getSerializableExtra("LIST");
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
				tvTitle.setText("兴趣选择");
				tvRight.setText("完成");
				tvRight.setOnClickListener(this);
			}
			
			SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
			mSwipeBackLayout.setEnableGesture(false);
			this.mLoadingDialog = LoadingDialog.createDialog(RegisterInterest.this, true);
			this.dialog = DialogTipsBuilder.getInstance(RegisterInterest.this);
			this.gridview = (CustomListView) findViewById(R.id.gridview);
			this.gridview.setDividerHeight(getResources().getDimensionPixelOffset(R.dimen.divider_register_size));
			this.gridview.setDividerWidth(getResources().getDimensionPixelOffset(R.dimen.divider_register_size));
			this.adapter = new SelectInterestAdapter(RegisterInterest.this, list);
			this.gridview.setAdapter(adapter);
			this.gridview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
					InterestStringModel model = adapter.getItem(position);
					if (null != model) {
						if (model.isSelect()) {
							model.setSelect(false);
						} else {
							model.setSelect(true);
						}
						adapter.notifyDataSetChanged();
					}
				}
			});
			this.mLoadingDialog.setDismissListener(new OnDismissListener() {
				@Override
				public void OnDismiss() {
					isRequest = false;
				}
			});
		} else {
			finish();
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			DialogChoiceBuilder dialogChoice = DialogChoiceBuilder.getInstance(RegisterInterest.this);
			dialogChoice.setMessage("您确定要跳过兴趣选择吗？").withDuration(400).withEffect(Effectstype.SlideBottom).setOnClick(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(RegisterInterest.this, IndexHome.class);
					startActivity(intent);
					finish();
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}
			}).show();
			break;
		case R.id.tvRight:
			if (!isRequest) {
				isRequest = true;
				ArrayList<Integer> selectId = new ArrayList<Integer>();
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).isSelect()) {
						selectId.add(list.get(i).getId());
					}
				}
				if (selectId != null && selectId.size() != 0) {
					UploadInterest(selectId);
				} else {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("您还没有选择兴趣~~").withEffect(Effectstype.Shake).show();
					}
					isRequest = false;
				}
			}
			break;
		}
	}
	
	/**
	 * 提交用户兴趣
	 */
	private void UploadInterest(ArrayList<Integer> selectId){
		mLoadingDialog.setMessage("提交兴趣中...");
		mLoadingDialog.show();
		RegisterUploadInterestParam param = new RegisterUploadInterestParam(RegisterInterest.this, selectId);
		HttpStringPost task = new HttpStringPost(RegisterInterest.this, param.getUrl(), new ResponseListener() {
			
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				Intent intent = new Intent(RegisterInterest.this, IndexHome.class);
				startActivity(intent);
				finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			DialogChoiceBuilder dialog = DialogChoiceBuilder.getInstance(RegisterInterest.this);
			dialog.setMessage("您确定要跳过兴趣选择吗？").withDuration(400).withEffect(Effectstype.SlideBottom).setOnClick(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(RegisterInterest.this, IndexHome.class);
					startActivity(intent);
					finish();
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}
			}).show();
		}
		return true;
	}

}
