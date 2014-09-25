package com.gather.android.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.application.GatherApplication;
import com.gather.android.dialog.DialogShareAct;
import com.gather.android.dialog.DialogShareAct.ShareClickListener;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.dialog.LoadingDialog.OnDismissListener;
import com.gather.android.http.HttpGetUtil;
import com.gather.android.http.HttpStringGet;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.ActDetailPicList;
import com.gather.android.model.ActModel;
import com.gather.android.params.AddInterestActParam;
import com.gather.android.params.AddShareActNumParam;
import com.gather.android.params.CancelInterestActParam;
import com.gather.android.utils.TimeUtil;
import com.gather.android.widget.ActDetailScrollView;
import com.gather.android.widget.InfiniteLoopViewPager;
import com.gather.android.widget.InfiniteLoopViewPagerAdapter;
import com.gather.android.widget.MyViewPager.OnPageChangeListener;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

@SuppressLint({ "HandlerLeak", "InflateParams" })
public class ActDetail extends SwipeBackActivity implements OnClickListener {
	/***** ActionBar ******/
	private ImageView ivLeft, ivRight;
	private TextView tvTitle, tvRight;
	private View actionbarView;

	/********** 自动循环图片 ********************/
	private InfiniteLoopViewPager mViewPager;
	private InfiniteLoopViewPagerAdapter pagerAdapter;
	private LinearLayout mLinearLayout;
	private Drawable mIndicatorUnfocused;
	private Drawable mIndicatorFocused;
	private List<ImageView> mImageList;
	private GatherApplication mApplication;
	private Handler mHandler;
	private int sleepTime = 4000;
//	private ImageView[] imageViews;
	private DisplayImageOptions options;

	/*********** 活动详情控件 *********************/
	private TextView tvDetailTitle, tvContent, tvAddressDetail, tvAddress, tvTime, tvPublisher, tvStatus, tvInterestOne, tvInterestTwo, tvDetailContent, tvAddressContent, tvErrorMsg;
	private LinearLayout llCheckDetail, llMenuBar, llShare, llInterest, llError;
	private ImageView ivActInterest, ivOnePic, ivErrorImg;
	private ActDetailScrollView scrollView;
	private ProgressBar picProgress;
	private FrameLayout flMain;

	private DisplayMetrics metrics;
	private LoadingDialog mLoadingDialog;
	private DialogTipsBuilder dialog;
	private int actId;
	private Animation alphaIn;
	private ActModel model;
	private ActDetailPicList picList;
	private boolean isRequest = false;

	@Override
	protected int layoutResId() {
		return R.layout.act_detail;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("ACT_ID")) {
			this.actId = intent.getExtras().getInt("ACT_ID");
			mApplication = (GatherApplication) getApplicationContext();
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
				tvTitle.setText("活动详情");
				tvRight.setVisibility(View.GONE);
			}

			this.mLoadingDialog = LoadingDialog.createDialog(ActDetail.this, true);
			this.mLoadingDialog.setDismissListener(new OnDismissListener() {
				@Override
				public void OnDismiss() {
					isRequest = false;
				}
			});
			this.dialog = DialogTipsBuilder.getInstance(ActDetail.this);
			this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).displayer(new FadeInBitmapDisplayer(100)).bitmapConfig(Bitmap.Config.RGB_565).build();
			this.alphaIn = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
			this.metrics = getResources().getDisplayMetrics();
			this.scrollView = (ActDetailScrollView) findViewById(R.id.scrollView);
			this.flMain = (FrameLayout) findViewById(R.id.flMain);
			this.flMain.setVisibility(View.INVISIBLE);
			this.mViewPager = (InfiniteLoopViewPager) findViewById(R.id.viewpager);
			this.tvDetailTitle = (TextView) findViewById(R.id.tvDetailTitle);
			this.tvContent = (TextView) findViewById(R.id.tvContent);
			this.tvAddressDetail = (TextView) findViewById(R.id.tvAddressDetail);
			this.tvAddress = (TextView) findViewById(R.id.tvAddress);
			this.tvTime = (TextView) findViewById(R.id.tvTime);
			this.tvPublisher = (TextView) findViewById(R.id.tvPublisher);
			this.tvStatus = (TextView) findViewById(R.id.tvStatus);
			this.tvInterestOne = (TextView) findViewById(R.id.tvInterestOne);
			this.tvInterestTwo = (TextView) findViewById(R.id.tvInterestTwo);
			this.tvDetailContent = (TextView) findViewById(R.id.tvDetailContent);
			this.tvAddressContent = (TextView) findViewById(R.id.tvAddressContent);
			this.tvErrorMsg = (TextView) findViewById(R.id.tvErrorMsg);
			this.llCheckDetail = (LinearLayout) findViewById(R.id.llCheckDetail);
			this.llMenuBar = (LinearLayout) findViewById(R.id.llMenuBar);
			this.llShare = (LinearLayout) findViewById(R.id.llShare);
			this.llInterest = (LinearLayout) findViewById(R.id.llInterest);
			this.llError = (LinearLayout) findViewById(R.id.llError);
			this.ivActInterest = (ImageView) findViewById(R.id.ivActInterest);
			this.ivOnePic = (ImageView) findViewById(R.id.ivOnePic);
			this.ivErrorImg = (ImageView) findViewById(R.id.ivErrorImg);
			this.picProgress = (ProgressBar) findViewById(R.id.picProgress);

			this.llCheckDetail.setOnClickListener(this);
			this.llShare.setOnClickListener(this);
			this.llInterest.setOnClickListener(this);
			this.ivErrorImg.setOnClickListener(this);
			this.ivOnePic.setOnClickListener(this);

			FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) this.mViewPager.getLayoutParams();
			param.width = metrics.widthPixels;
			param.height = param.width * 6 / 9;
			this.mViewPager.setLayoutParams(param);
			
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.ivOnePic.getLayoutParams();
			params.width = param.width;
			params.height= param.height;
			this.ivOnePic.setLayoutParams(params);

			getActDetail();
			getActPic();
		} else {
			finish();
			toast("查看详情失败~~");
		}
	}

	private void initView() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
					if (mApplication.isRun && !mApplication.isDown) {
						this.sendEmptyMessageDelayed(0, sleepTime);
					}
					break;
				case 1:
					if (mApplication.isRun && !mApplication.isDown) {
						this.sendEmptyMessageDelayed(0, sleepTime);
					}
					break;
				}
			}
		};
	}

	/**
	 * 获取活动详情
	 */
	private void getActDetail() {
		mLoadingDialog.setMessage("正在加载中...");
		mLoadingDialog.show();
		HttpGetUtil param = new HttpGetUtil(ActDetail.this, "act/actInfo/getInfo");
		param.setParam("actId", actId);
		HttpStringGet task = new HttpStringGet(ActDetail.this, param.toString(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				try {
					JSONObject object = new JSONObject(result);
					Gson gson = new Gson();
					model = gson.fromJson(object.getString("actInfo"), ActModel.class);
					setActDetail();
				} catch (JSONException e) {
					if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
						mLoadingDialog.dismiss();
					}
					finish();
					toast("详情信息出错~~");
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
				if (!flMain.isShown()) {
					flMain.setVisibility(View.VISIBLE);
				}
				scrollView.setVisibility(View.GONE);
				llMenuBar.setVisibility(View.GONE);
				llError.setVisibility(View.VISIBLE);
				llError.startAnimation(alphaIn);
				tvErrorMsg.setText(msg);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (!flMain.isShown()) {
					flMain.setVisibility(View.VISIBLE);
				}
				scrollView.setVisibility(View.GONE);
				llMenuBar.setVisibility(View.GONE);
				llError.setVisibility(View.VISIBLE);
				llError.startAnimation(alphaIn);
				tvErrorMsg.setText(error.getMsg());
			}
		});
		executeRequest(task);
	}

	/**
	 * 获取活动图片列表
	 */
	private void getActPic() {
		ivOnePic.setVisibility(View.GONE);
		mViewPager.setVisibility(View.INVISIBLE);
		picProgress.setVisibility(View.VISIBLE);
		HttpGetUtil param = new HttpGetUtil(ActDetail.this, "act/actInfo/getImgs");
		param.setParam("actId", actId);
		HttpStringGet task = new HttpStringGet(ActDetail.this, param.toString(), new ResponseListener() {

			@Override
			public void success(int code, String msg, String result) {
				Gson gson = new Gson();
				picList = gson.fromJson(result, ActDetailPicList.class);
				setPagerView();
			}

			@Override
			public void relogin(String msg) {
				needLogin(msg);
			}

			@Override
			public void error(int code, String msg) {
				toast(msg);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				toast(error.getMsg());
			}
		});
		executeRequest(task);
	}

	/**
	 * 加载详情信息
	 */
	private void setActDetail() {
		if (null != model) {
			tvDetailTitle.setText(model.getTitle());
			tvContent.setText(model.getIntro());
			tvAddressDetail.setText(model.getAddr_num());
			tvAddress.setText(setAddressInfo());
			tvTime.setText(setTimeInfo());
			tvPublisher.setText(model.getOrganizer());
			switch (model.getT_status()) {
			case 1:
				tvStatus.setText("即将开始");
				break;
			case 2:
				tvStatus.setText("正在进行中");
				break;
			case 3:
				tvStatus.setText("正在筹备中");
				break;
			case 4:
				tvStatus.setText("已结束");
				break;
			}
			switch (model.getAct_tags().size()) {
			case 1:
				tvInterestOne.setVisibility(View.VISIBLE);
				tvInterestTwo.setVisibility(View.GONE);
				tvInterestOne.setText(model.getAct_tags().get(0).getName());
				setInterestColor(tvInterestOne, model.getAct_tags().get(0).getId());
				break;
			case 2:
				tvInterestOne.setVisibility(View.VISIBLE);
				tvInterestTwo.setVisibility(View.VISIBLE);
				tvInterestOne.setText(model.getAct_tags().get(0).getName());
				setInterestColor(tvInterestOne, model.getAct_tags().get(0).getId());
				tvInterestTwo.setText(model.getAct_tags().get(1).getName());
				setInterestColor(tvInterestTwo, model.getAct_tags().get(1).getId());
				break;
			}
			tvDetailContent.setText(model.getDetail());
			tvAddressContent.setText(model.getAddr_route());
			if (model.getIs_loved() == 1) {
				ivActInterest.setImageResource(R.drawable.icon_act_interest);
			} else {
				ivActInterest.setImageResource(R.drawable.icon_act_interest);
			}
			if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
				mLoadingDialog.dismiss();
			}
			if (!flMain.isShown()) {
				flMain.setVisibility(View.VISIBLE);
				flMain.startAnimation(alphaIn);
			}
			if (!scrollView.isShown()) {
				scrollView.setVisibility(View.VISIBLE);
			}
			if (!llMenuBar.isShown()) {
				llMenuBar.setVisibility(View.VISIBLE);
			}
		} else {
			finish();
			toast("加载失败，请重试~~");
		}
	}

	/**
	 * 装载图片
	 */
	@SuppressWarnings("deprecation")
	private void setPagerView() {
		if (picList.getImgs().size() == 0) {
			ivOnePic.setVisibility(View.VISIBLE);
			mViewPager.setVisibility(View.GONE);
			picProgress.setVisibility(View.GONE);
		} else if (picList.getImgs().size() == 1) {
			ivOnePic.setVisibility(View.VISIBLE);
			mViewPager.setVisibility(View.GONE);
			picProgress.setVisibility(View.GONE);
//			imageLoader.displayImage(picList.getImgs().get(0).getImg_url(), ivOnePic, options);
			imageLoader.displayImage("http://c.hiphotos.baidu.com/image/pic/item/738b4710b912c8fce9a55ecafe039245d6882166.jpg", ivOnePic, options);
		} else {
			ivOnePic.setVisibility(View.GONE);
			initView();
			if (!mApplication.isRun) {
				mApplication.isRun = true;
				mHandler.sendEmptyMessageDelayed(0, sleepTime);
			}
			mLinearLayout = (LinearLayout) findViewById(R.id.slide_indicator);
			mIndicatorUnfocused = getResources().getDrawable(R.drawable.slide_indicator_unfocused);
			mIndicatorFocused = getResources().getDrawable(R.drawable.slide_indicator_focused);
			mImageList = new ArrayList<ImageView>();
			for (int i = 0; i < picList.getImgs().size(); i++) {
				ImageView imageView = new ImageView(ActDetail.this);
				imageView.setLayoutParams(new ViewGroup.LayoutParams(getResources().getDimensionPixelOffset(R.dimen.indicator_icon_size), getResources().getDimensionPixelOffset(R.dimen.indicator_icon_size)));
				imageView.setBackgroundDrawable(mIndicatorUnfocused);
				mImageList.add(imageView);
				mLinearLayout.addView(imageView);
			}
			mImageList.get(0).setBackgroundDrawable(mIndicatorFocused);
			
			pagerAdapter = new InfiniteLoopViewPagerAdapter(new ActDetailPicAdapter());
			mViewPager.setInfinateAdapter(mHandler, pagerAdapter);
			mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
//			mViewPager.setCurrentItem(0);
			mViewPager.setVisibility(View.VISIBLE);
			picProgress.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			finish();
			break;
		case R.id.llCheckDetail:

			break;
		case R.id.llShare:
			DialogShareAct shareDialog = new DialogShareAct(ActDetail.this, R.style.ShareDialog).setOnShareClickListener(new ShareClickListener() {
				@Override
				public void OnShareClickListener(int position) {
					switch (position) {
					case DialogShareAct.WECHAT:
						AddShareNum();
						break;
					case DialogShareAct.SQUARE:
						AddShareNum();
						break;
					case DialogShareAct.TENCENT:
						AddShareNum();
						break;
					case DialogShareAct.ZOON:
						AddShareNum();
						break;
					}
				}
			});
			shareDialog.show();
			break;
		case R.id.llInterest:
			if (!isRequest) {
				isRequest = true;
				switch (model.getIs_loved()) {
				case -1:
					isRequest = false;
					dialog.setMessage("不能再添加").withEffect(Effectstype.SlideBottom).show();
					break;
				case 0:
					AddInterestAct();
					break;
				case 1:
					CancelInterestAct();
					break;
				}
			}
			break;
		case R.id.ivErrorImg:
			if (!isRequest) {
				isRequest = true;
				getActPic();
				getActDetail();
			}
			break;
		case R.id.ivOnePic:
			Intent intent = new Intent(ActDetail.this, TouchGallery.class);
			intent.putExtra("LIST", picList.getImgs());
			intent.putExtra("POSITION", 0);
			startActivity(intent);
			overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
			break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mHandler != null) {
			mApplication.isRun = false;
			mHandler.removeCallbacksAndMessages(null);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mHandler != null) {
			mApplication.isRun = true;
			mHandler.sendEmptyMessageDelayed(0, sleepTime);
		}
	}

	/**
	 * 添加兴趣活动
	 */
	private void AddInterestAct() {
		mLoadingDialog.setMessage("添加中...");
		mLoadingDialog.show();
		AddInterestActParam param = new AddInterestActParam(ActDetail.this, actId);
		HttpStringPost task = new HttpStringPost(ActDetail.this, param.getUrl(), new ResponseListener() {

			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					isRequest = false;
					mLoadingDialog.dismiss();
				}
				model.setIs_loved(1);
				ivActInterest.setImageResource(R.drawable.icon_act_interest);
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
	private void CancelInterestAct() {
		mLoadingDialog.setMessage("正在取消...");
		mLoadingDialog.show();
		CancelInterestActParam param = new CancelInterestActParam(ActDetail.this, actId);
		HttpStringPost task = new HttpStringPost(ActDetail.this, param.getUrl(), new ResponseListener() {

			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					isRequest = false;
					mLoadingDialog.dismiss();
				}
				model.setIs_loved(-1);
				ivActInterest.setImageResource(R.drawable.icon_act_interest);
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
	private void AddShareNum() {
		AddShareActNumParam param = new AddShareActNumParam(ActDetail.this, actId);
		HttpStringPost task = new HttpStringPost(ActDetail.this, param.getUrl(), new ResponseListener() {
			
			@Override
			public void success(int code, String msg, String result) {
				
			}
			
			@Override
			public void relogin(String msg) {
				needLogin(msg);
			}
			
			@Override
			public void error(int code, String msg) {
				toast(msg);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				toast(error.getMsg());
			}
		}, param.getParameters());
		executeRequest(task);
	}

	/**
	 * 图片切换监听
	 */
	private class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int state) {

		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onPageSelected(int position) {
			int size = mImageList.size();
			if (position >= 100000 * size) {
				position = position - 100000 * size;
				for (int i = 0; i < size; i++) {
					if (i == position % size)
						mImageList.get(i).setBackgroundDrawable(mIndicatorFocused);
					else
						mImageList.get(i).setBackgroundDrawable(mIndicatorUnfocused);
				}
			} else {
				position = 100000 * size - position;
				for (int i = size - 1; i >= 0; i--) {
					int index;
					if (position%size == 0) {
						index = size;
					} else {
						index = position%size;
					}
					if (i == size - index)
						mImageList.get(i).setBackgroundDrawable(mIndicatorFocused);
					else
						mImageList.get(i).setBackgroundDrawable(mIndicatorUnfocused);
				}
			}
		}
	}

	/**
	 * 装载图片Adapter
	 */
	private class ActDetailPicAdapter extends com.gather.android.widget.MyPagerAdapter {
		@Override
		public int getCount() {
			return picList.getImgs().size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == (View) object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			ImageView img = new ImageView(ActDetail.this);
			img.setScaleType(ImageView.ScaleType.CENTER_CROP);
			img.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ActDetail.this, TouchGallery.class);
					intent.putExtra("LIST", picList.getImgs());
					intent.putExtra("POSITION", position);
					startActivity(intent);
					overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
				}
			});
			imageLoader.displayImage("http://c.hiphotos.baidu.com/image/pic/item/738b4710b912c8fce9a55ecafe039245d6882166.jpg", img, options);
            container.addView(img);
			return img;
		}
	}
	
	/**
	 * 活动地址显示规则
	 */
	private String setAddressInfo() {
		StringBuffer sb = new StringBuffer();
		if (!model.getAddr_city().equals("")) {
			sb.append(model.getAddr_city());
			if (!model.getAddr_area().equals("") || !model.getAddr_road().equals("")) {
				sb.append("，");
			} else {
				return sb.toString();
			}
		}
		if (!model.getAddr_area().equals("")) {
			sb.append(model.getAddr_area());
			if (!model.getAddr_road().equals("")) {
				sb.append("，");
			} else {
				return sb.toString();
			}
		}
		if (!model.getAddr_road().equals("")) {
			sb.append(model.getAddr_road());
		}
		return sb.toString();
	}
	
	/**
	 * 活动时间显示规则
	 */
	private String setTimeInfo(){
		StringBuffer sb = new StringBuffer();
		String begin_time = TimeUtil.getActDetailTime(model.getB_time());
		String end_time = TimeUtil.getActDetailTime(model.getE_time());
		if (!begin_time.equals("")) {
			sb.append(begin_time);
			if (!end_time.equals("")) {
				if (begin_time.substring(0, begin_time.indexOf(" ")).equals(end_time.substring(0, end_time.indexOf(" ")))) {
					sb.append(" 一 ");
					sb.append(end_time.substring(end_time.indexOf(" ")+1, end_time.length()));
				} else {
					sb.append("\n");
					sb.append(end_time);
				}
				begin_time = null;
				end_time = null;
				return sb.toString();
			} else {
				begin_time = null;
				end_time = null;
				return sb.toString();
			}
		} else {
			begin_time = null;
			end_time = null;
			return sb.toString();
		}
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

}
