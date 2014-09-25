package com.gather.android.fragment;

import java.util.ArrayList;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.activity.IndexHome;
import com.gather.android.activity.LoginIndex;
import com.gather.android.activity.SelectCity;
import com.gather.android.adapter.HomeActAdapter;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.DialogActSelect;
import com.gather.android.dialog.DialogActSelect.OnActivityClickListener;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.dialog.LoadingDialog.OnDismissListener;
import com.gather.android.http.HttpStringGet;
import com.gather.android.http.RequestManager;
import com.gather.android.http.ResponseListener;
import com.gather.android.listener.OnAdapterLoadMoreOverListener;
import com.gather.android.listener.OnAdapterRefreshOverListener;
import com.gather.android.model.InterestStringList;
import com.gather.android.preference.AppPreference;
import com.gather.android.widget.XListView;
import com.gather.android.widget.XListView.IXListViewListener;
import com.google.gson.Gson;

public class HomeFragment extends Fragment implements OnClickListener {

	private View rootView;
	private ImageView ivMenu, ivSelect;
	private LinearLayout llCity;
	private XListView listView;
	private HomeActAdapter actAdapter;
	private boolean isRequest = false;
	private LoadingDialog mLoadingDialog;
	private DialogTipsBuilder dialog;
	private ArrayList<Integer> list;
	private int timeType;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		this.rootView = inflater.inflate(R.layout.home_fragment, (ViewGroup) getActivity().findViewById(R.id.slidingpanellayout), false);

		this.list = new ArrayList<Integer>();
		this.dialog = DialogTipsBuilder.getInstance(getActivity());
		this.mLoadingDialog = LoadingDialog.createDialog(getActivity(), true);
		this.mLoadingDialog.setDismissListener(new OnDismissListener() {
			@Override
			public void OnDismiss() {
				isRequest = false;
			}
		});
		this.ivMenu = (ImageView) rootView.findViewById(R.id.ivMenu);
		this.ivSelect = (ImageView) rootView.findViewById(R.id.ivSelect);
		this.llCity = (LinearLayout) rootView.findViewById(R.id.llCity);
		this.listView = (XListView) rootView.findViewById(R.id.listview);

		DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
		this.listView.setPullLoadEnable(true);
		this.listView.setPullRefreshEnable(true);
		this.listView.setLoadMoreNeedHigh(metrics.heightPixels);
		this.listView.stopLoadMoreMessageBox();
		this.listView.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				actAdapter.getHomeAct(list, timeType);
			}

			@Override
			public void onLoadMore() {
				actAdapter.loadMore();
			}
		});

		this.actAdapter = new HomeActAdapter(getActivity());
		this.actAdapter.setRefreshOverListener(new OnAdapterRefreshOverListener() {
			@Override
			public void refreshOver(int code, String msg) {
				listView.stopRefresh();
				switch (code) {
				case 0:
					if (msg.equals(SuperAdapter.ISNULL)) {
						listView.setFooterImageView("还没有活动", R.drawable.no_result);
					} else {
						listView.setText(msg);
					}
					break;
				case 5:
					needLogin(msg);
					break;
				default:
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage(msg).withEffect(Effectstype.Shake).show();
					}
					break;
				}
			}
		});
		this.actAdapter.setLoadMoreOverListener(new OnAdapterLoadMoreOverListener() {
			@Override
			public void loadMoreOver(int code, String msg) {
				listView.stopLoadMore();
				switch (code) {
				case 0:
					listView.setText(msg);
					break;
				case 5:
					needLogin(msg);
					break;
				default:
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage(msg).withEffect(Effectstype.Shake).show();
					}
					break;
				}
			}
		});
		this.listView.setAdapter(actAdapter);
		this.listView.onClickRefush();
		this.actAdapter.getHomeAct(list, timeType);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup p = (ViewGroup) rootView.getParent();
		if (p != null) {
			p.removeAllViews();
		}
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		this.ivMenu.setOnClickListener(this);
		this.ivSelect.setOnClickListener(this);
		this.llCity.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivMenu:
			if (((IndexHome) getActivity()).isOpen()) {
				((IndexHome) getActivity()).showContent();
			} else {
				((IndexHome) getActivity()).showMenu();
			}
			break;
		case R.id.ivSelect:
			if (!isRequest) {
				isRequest = true;
				getSelectInterest();
			}
			break;
		case R.id.llCity:
			Intent cityIntent = new Intent(getActivity(), SelectCity.class);
			startActivity(cityIntent);
			break;
		}
	}

	/**
	 * 获取活动筛选的兴趣
	 */
	private void getSelectInterest() {
		mLoadingDialog.setMessage("加载中...");
		mLoadingDialog.show();
		HttpStringGet get = new HttpStringGet(getActivity(), Constant.DEFOULT_REQUEST_URL + "act/tagInfo/getSltTags", new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					isRequest = false;
					mLoadingDialog.dismiss();
				}
				Gson gson = new Gson();
				InterestStringList mlist = gson.fromJson(result, InterestStringList.class);
				if (mlist != null && mlist.tags != null) {
					if (list == null) {
						list = new ArrayList<Integer>();
					}
					if (list.size() != 0) {
						String ids = list.toString();
						for (int j = 0; j < mlist.tags.size(); j++) {
							if (ids.contains(String.valueOf(mlist.tags.get(j).getId()))) {
								mlist.tags.get(j).setSelect(true);
							}
						}
						ids = null;
					}
				} 
				DialogActSelect dialogSelect = null;
				if (mlist != null && mlist.tags != null) {
					dialogSelect = new DialogActSelect(getActivity(), R.style.dialog_date, mlist.tags, timeType);
				} else {
					dialogSelect = new DialogActSelect(getActivity(), R.style.dialog_date, null, timeType);
				}
				dialogSelect.setActivityListener(new OnActivityClickListener() {
					@Override
					public void onSelectListener(ArrayList<Integer> idList, int type) {
						if (idList == null) {// 没有选择任何标签
							list.clear();
						} else {
							list = idList;
						}
						timeType = type;
						listView.onClickRefush();
						actAdapter.getHomeAct(list, timeType);
					}
				});
				dialogSelect.withDuration(400).withEffect(Effectstype.Slidetop).show();
			}

			@Override
			public void relogin(String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage(msg).withEffect(Effectstype.Shake).show();
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
		});
		RequestManager.addRequest(get, getActivity());
	}
	
	/**
	 * 重新登录
	 */
	protected void needLogin(String msg){
		DialogTipsBuilder dialog = DialogTipsBuilder.getInstance(getActivity());
		dialog.withDuration(400).withEffect(Effectstype.Fall).setMessage(msg).isCancelableOnTouchOutside(false).setOnClick(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AppPreference.clearInfo(getActivity());
				Intent intent = new Intent(getActivity(), LoginIndex.class);
				startActivity(intent);
				getActivity().finish();
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

}
