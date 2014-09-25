package com.gather.android.adapter;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.activity.ActDetail;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.dialog.DialogShareAct;
import com.gather.android.dialog.DialogShareAct.ShareClickListener;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.HttpGetUtil;
import com.gather.android.http.HttpStringGet;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.RequestManager;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.ActList;
import com.gather.android.model.ActModel;
import com.gather.android.params.AddInterestActParam;
import com.gather.android.params.AddShareActNumParam;
import com.gather.android.params.CancelInterestActParam;
import com.gather.android.utils.TimeUtil;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

@SuppressLint("InflateParams")
public class HomeActAdapter extends SuperAdapter {

	private Context context;
	private ResponseListener listener;
	private Response.ErrorListener errorListener;
	private ArrayList<Integer> idList;
	private int TimeType, page, limit = 10, totalNum, maxPage, isOver;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private DisplayMetrics metrics;
	private LoadingDialog mLoadingDialog;
	private DialogTipsBuilder dialog;

	public HomeActAdapter(Context context) {
		super(context);
		this.metrics = context.getResources().getDisplayMetrics();
		this.dialog = DialogTipsBuilder.getInstance(context);
		this.mLoadingDialog = LoadingDialog.createDialog(context, true);
		this.mLoadingDialog.setDismissListener(new com.gather.android.dialog.LoadingDialog.OnDismissListener() {
			@Override
			public void OnDismiss() {
				isRequest = false;
			}
		});
		this.idList = new ArrayList<Integer>(2);
		this.context = context;
		this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).displayer(new FadeInBitmapDisplayer(100)).bitmapConfig(Bitmap.Config.RGB_565).build();
		this.initListener();
	}

	private void initListener() {
		listener = new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (page == 1) {
					JSONObject object = null;
					try {
						object = new JSONObject(result);
						totalNum = object.getInt("total_num");
						if (totalNum % limit == 0) {
							maxPage = totalNum / limit;
						} else {
							maxPage = (totalNum / limit) + 1;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					} finally {
						object = null;
					}
				}
				Gson gson = new Gson();
				ActList list = gson.fromJson(result, ActList.class);
				if (list != null && list.getActList() != null) {
					switch (loadType) {
					case REFRESH:
						if (totalNum == 0) {
							refreshOver(code, ISNULL);
						} else if (page == maxPage) {
							isOver = 1;
							refreshOver(code, ISOVER);
						} else {
							page++;
							refreshOver(code, CLICK_MORE);
						}
						refreshItems(list.getActList());
						break;
					case LOADMORE:
						if (page != maxPage) {
							page++;
							loadMoreOver(code, CLICK_MORE);
						} else {
							isOver = 1;
							loadMoreOver(code, ISOVER);
						}
						addItems(list.getActList());
						break;
					}
					isRequest = false;
				}
			}

			@Override
			public void relogin(String msg) {
				switch (loadType) {
				case REFRESH:
					refreshOver(5, msg);
					break;
				case LOADMORE:
					loadMoreOver(5, msg);
					break;
				}
				isRequest = false;
			}

			@Override
			public void error(int code, String msg) {
				switch (loadType) {
				case REFRESH:
					refreshOver(code, msg);
					break;
				case LOADMORE:
					loadMoreOver(code, msg);
					break;
				}
				isRequest = false;
			}
		};
		errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				switch (loadType) {
				case REFRESH:
					refreshOver(-1, error.getMsg());
					break;
				case LOADMORE:
					loadMoreOver(-1, error.getMsg());
					break;
				}
				isRequest = false;
			}
		};
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_list_act, null);
			holder = new ViewHolder();
			holder.flItemAll = (FrameLayout) convertView.findViewById(R.id.flItemAll);
			holder.divider = (View) convertView.findViewById(R.id.divider);
			holder.viewBackground = (View) convertView.findViewById(R.id.viewBackground);
			holder.ivActPic = (ImageView) convertView.findViewById(R.id.ivActPic);
			holder.ivActStatus = (ImageView) convertView.findViewById(R.id.ivActStatus);
			holder.ivActInterest = (ImageView) convertView.findViewById(R.id.ivActInterest);
			holder.tvActTitle = (TextView) convertView.findViewById(R.id.tvActTitle);
			holder.tvActTime = (TextView) convertView.findViewById(R.id.tvActTime);
			holder.tvActAddress = (TextView) convertView.findViewById(R.id.tvActAddress);
			holder.tvActInterestOne = (TextView) convertView.findViewById(R.id.tvActInterestOne);
			holder.tvActInterestTwo = (TextView) convertView.findViewById(R.id.tvActInterestTwo);
			holder.tvActShareNum = (TextView) convertView.findViewById(R.id.tvActShareNum);
			holder.tvActInterestNum = (TextView) convertView.findViewById(R.id.tvActInterestNum);
			holder.llActShare = (LinearLayout) convertView.findViewById(R.id.llActShare);
			holder.llActInterest = (LinearLayout) convertView.findViewById(R.id.llActInterest);

			FrameLayout.LayoutParams params = (LayoutParams) holder.ivActPic.getLayoutParams();
			params.width = metrics.widthPixels;
			params.height = params.width * 17 / 36;
			holder.ivActPic.setLayoutParams(params);
			
			FrameLayout.LayoutParams param = (LayoutParams) holder.viewBackground.getLayoutParams();
			param.width = params.width;
			param.height = params.height;
			holder.viewBackground.setLayoutParams(param);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ActModel model = (ActModel) getItem(position);
		if (position == 0) {
			holder.divider.setVisibility(View.GONE);
		} else {
			holder.divider.setVisibility(View.VISIBLE);
		}
		holder.tvActTitle.setText(model.getTitle());
		holder.tvActTime.setText(TimeUtil.getActListTime(model.getB_time()));
		holder.tvActAddress.setText(model.getAddr_num());
		imageLoader.displayImage("http://c.hiphotos.baidu.com/image/pic/item/738b4710b912c8fce9a55ecafe039245d6882166.jpg", holder.ivActPic, options);
		switch (model.getAct_tags().size()) {
		case 1:
			holder.tvActInterestOne.setVisibility(View.VISIBLE);
			setInterestColor(holder.tvActInterestOne, model.getAct_tags().get(0).getId());
			holder.tvActInterestTwo.setVisibility(View.GONE);
			holder.tvActInterestOne.setText(model.getAct_tags().get(0).getName());
			break;
		case 2:
			holder.tvActInterestOne.setVisibility(View.VISIBLE);
			setInterestColor(holder.tvActInterestOne, model.getAct_tags().get(0).getId());
			holder.tvActInterestTwo.setVisibility(View.VISIBLE);
			setInterestColor(holder.tvActInterestTwo, model.getAct_tags().get(1).getId());
			holder.tvActInterestOne.setText(model.getAct_tags().get(0).getName());
			holder.tvActInterestTwo.setText(model.getAct_tags().get(1).getName());
			break;
		}
		holder.tvActShareNum.setText(model.getShared_num() + "");
		holder.tvActInterestNum.setText(model.getLoved_num() + "");
		if (model.getIs_loved() == 1) {
			holder.ivActInterest.setImageResource(R.drawable.icon_act_interest);
		} else {
			holder.ivActInterest.setImageResource(R.drawable.icon_act_interest);
		}
		switch (model.getT_status()) {
		case 1:// 即将开始

			break;
		case 2:// 进行中

			break;
		case 3:// 筹备中

			break;
		case 4:// 已结束

			break;
		}
		holder.llActShare.setOnClickListener(new OnShareClickListener(model));
		holder.llActInterest.setOnClickListener(new OnInterestClickListener(model));
		holder.flItemAll.setOnClickListener(new OnDetailClickListener(model));
		return convertView;
	}

	private static class ViewHolder {
		public ImageView ivActPic, ivActStatus, ivActInterest;
		public TextView tvActTitle, tvActTime, tvActAddress, tvActInterestOne, tvActInterestTwo, tvActShareNum, tvActInterestNum;
		public LinearLayout llActShare, llActInterest;
		public View divider, viewBackground;
		public FrameLayout flItemAll;
	}

	/**
	 * 分享
	 */
	private class OnShareClickListener implements OnClickListener {

		private ActModel model;

		public OnShareClickListener(ActModel model) {
			this.model = model;
		}

		@Override
		public void onClick(View arg0) {
			DialogShareAct shareDialog = new DialogShareAct(context, R.style.ShareDialog).setOnShareClickListener(new ShareClickListener() {
				@Override
				public void OnShareClickListener(int position) {
					switch (position) {
					case DialogShareAct.WECHAT:
						AddShareNum(model);
						break;
					case DialogShareAct.SQUARE:
						AddShareNum(model);
						break;
					case DialogShareAct.TENCENT:
						AddShareNum(model);
						break;
					case DialogShareAct.ZOON:
						AddShareNum(model);
						break;
					}
				}
			});
			shareDialog.show();
		}
	}

	/**
	 * 收藏
	 */
	private class OnInterestClickListener implements OnClickListener {

		private ActModel model;

		public OnInterestClickListener(ActModel model) {
			this.model = model;
		}

		@Override
		public void onClick(View arg0) {
			if (!isRequest) {
				isRequest = true;
				switch (model.getIs_loved()) {
				case -1:
					isRequest = false;
					dialog.setMessage("不能再添加").withEffect(Effectstype.SlideBottom).show();
					break;
				case 0:
					AddInterestAct(model);
					break;
				case 1:
					CancelInterestAct(model);
					break;
				}
			}
		}
	}

	/**
	 * 查看详情
	 */
	private class OnDetailClickListener implements OnClickListener {

		private ActModel model;

		public OnDetailClickListener(ActModel model) {
			this.model = model;
		}

		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(context, ActDetail.class);
			intent.putExtra("ACT_ID", model.getId());
			context.startActivity(intent);
		}
	}
	
	/**
	 * 添加兴趣活动
	 */
	private void AddInterestAct(final ActModel model) {
		mLoadingDialog.setMessage("添加中...");
		mLoadingDialog.show();
		AddInterestActParam param = new AddInterestActParam(context, model.getId());
		HttpStringPost task = new HttpStringPost(context, param.getUrl(), new ResponseListener() {

			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					isRequest = false;
					mLoadingDialog.dismiss();
				}
				model.setIs_loved(1);
				model.setLoved_num(model.getLoved_num() + 1);
				notifyDataSetChanged();
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
	 * 取消兴趣活动
	 */
	private void CancelInterestAct(final ActModel model) {
		mLoadingDialog.setMessage("正在取消...");
		mLoadingDialog.show();
		CancelInterestActParam param = new CancelInterestActParam(context, model.getId());
		HttpStringPost task = new HttpStringPost(context, param.getUrl(), new ResponseListener() {

			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					isRequest = false;
					mLoadingDialog.dismiss();
				}
				model.setIs_loved(-1);
				model.setLoved_num(model.getLoved_num() - 1);
				notifyDataSetChanged();
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
	 * 添加活动分享数(分享成功后调用)
	 */
	private void AddShareNum(final ActModel model) {
		AddShareActNumParam param = new AddShareActNumParam(context, model.getId());
		HttpStringPost task = new HttpStringPost(context, param.getUrl(), new ResponseListener() {
			
			@Override
			public void success(int code, String msg, String result) {
				model.setShared_num(model.getShared_num() + 1);
				notifyDataSetChanged();
			}
			
			@Override
			public void relogin(String msg) {
				needLogin(msg);
			}
			
			@Override
			public void error(int code, String msg) {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(context, error.getMsg(), Toast.LENGTH_SHORT).show();
			}
		}, param.getParameters());
		executeRequest(task);
	}

	/**
	 * 随机给标签颜色
	 */
	private void setInterestColor(TextView textView, int id) {
		switch (id % 8) {
		case 0:
			textView.setBackgroundColor(0xFFF5C22F);
			break;
		case 1:
			textView.setBackgroundColor(0xFFE69433);
			break;
		case 2:
			textView.setBackgroundColor(0xFFDC3932);
			break;
		case 3:
			textView.setBackgroundColor(0xFF5997B5);
			break;
		case 4:
			textView.setBackgroundColor(0xFF88B764);
			break;
		case 5:
			textView.setBackgroundColor(0xFFF59574);
			break;
		case 6:
			textView.setBackgroundColor(0xFFAD7895);
			break;
		case 7:
			textView.setBackgroundColor(0xFF8DABBA);
			break;
		}
	}

	@Override
	public void refresh() {

	}

	@Override
	public void loadMore() {
		if (isOver == 1) {
			loadMoreOver(0, ISOVER);
		} else {
			if (!isRequest) {
				this.isRequest = true;
				this.loadType = LOADMORE;
				HttpGetUtil param = new HttpGetUtil(context, "act/actInfo/sltActs");
				if (idList != null && idList.size() != 0) {
					for (int i = 0; i < idList.size(); i++) {
						param.setParam("tagIds", idList.get(i));
					}
				}
				if (TimeType != 0) {
					param.setParam("actTimeStatus", TimeType);
				}
				param.setParam("page", page);
				param.setParam("size", limit);
				HttpStringGet task = new HttpStringGet(context, param.toString(), listener, errorListener);
				RequestManager.addRequest(task, context);
			}
		}
	}

	public void getHomeAct(ArrayList<Integer> idList, int TimeType) {
		if (!isRequest) {
			this.isRequest = true;
			this.loadType = REFRESH;
			this.page = 1;
			this.idList = idList;
			this.TimeType = TimeType;
			HttpGetUtil param = new HttpGetUtil(context, "act/actInfo/sltActs");
			if (idList != null && idList.size() != 0) {
				for (int i = 0; i < idList.size(); i++) {
					param.setParam("tagIds", idList.get(i));
				}
			}
			if (TimeType != 0) {
				param.setParam("actTimeStatus", TimeType);
			}
			param.setParam("page", page);
			param.setParam("size", limit);
			HttpStringGet task = new HttpStringGet(context, param.toString(), listener, errorListener);
			RequestManager.addRequest(task, context);
		}
	}

}
