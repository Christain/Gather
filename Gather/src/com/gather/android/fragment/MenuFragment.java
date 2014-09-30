package com.gather.android.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.activity.InterestActList;
import com.gather.android.activity.Message;
import com.gather.android.activity.RecommendAct;
import com.gather.android.activity.Setting;
import com.gather.android.activity.SignIn;
import com.gather.android.activity.UserInfo;
import com.gather.android.http.HttpGetUtil;
import com.gather.android.http.HttpStringGet;
import com.gather.android.http.RequestManager;
import com.gather.android.http.ResponseListener;
import com.gather.android.manage.VersionManage;
import com.gather.android.model.UserInfoModel;
import com.gather.android.preference.AppPreference;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class MenuFragment extends Fragment implements OnClickListener {

	private View rootView;
	private ImageView ivUserIcon, ivVersion, ivSex;
	private TextView tvUserName, tvUserSign, tvVersion;
	private LinearLayout llUser, llInterestAct, llMessage, llSetting, llRecommend, llSignIn, llVersion;
	
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private UserInfoModel userInfoModel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		this.rootView = inflater.inflate(R.layout.menu_fragment, (ViewGroup) getActivity().findViewById(R.id.slidingpanellayout), false);

		this.ivUserIcon = (ImageView) rootView.findViewById(R.id.ivUserIcon);
		this.ivVersion = (ImageView) rootView.findViewById(R.id.ivVersion);
		this.ivSex = (ImageView) rootView.findViewById(R.id.ivSex);
		this.tvUserName = (TextView) rootView.findViewById(R.id.tvUserName);
		this.tvUserSign = (TextView) rootView.findViewById(R.id.tvSign);
		this.tvVersion = (TextView) rootView.findViewById(R.id.tvVersion);
		this.llUser = (LinearLayout) rootView.findViewById(R.id.llUser);
		this.llInterestAct = (LinearLayout) rootView.findViewById(R.id.llInterestAct);
		this.llMessage = (LinearLayout) rootView.findViewById(R.id.llMessage);
		this.llSetting = (LinearLayout) rootView.findViewById(R.id.llSetting);
		this.llRecommend = (LinearLayout) rootView.findViewById(R.id.llRecommend);
		this.llSignIn = (LinearLayout) rootView.findViewById(R.id.llSignIn);
		this.llVersion = (LinearLayout) rootView.findViewById(R.id.llVersion);

		this.llUser.setOnClickListener(this);
		this.llInterestAct.setOnClickListener(this);
		this.llMessage.setOnClickListener(this);
		this.llSetting.setOnClickListener(this);
		this.llRecommend.setOnClickListener(this);
		this.llSignIn.setOnClickListener(this);
		this.llVersion.setOnClickListener(this);
		
		this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.icon_share_tencent).showImageForEmptyUri(R.drawable.icon_share_tencent).showImageOnFail(R.drawable.icon_share_tencent).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new RoundedBitmapDisplayer(90)).bitmapConfig(Bitmap.Config.RGB_565).build();
		this.tvVersion.setText("当前版本：" + VersionManage.getPackageInfo(getActivity()).versionName);
		this.ivVersion.setVisibility(View.GONE);
		this.setUserInfoFromSP();
		this.getUserInfo();
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
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.llUser:
			intent = new Intent(getActivity(), UserInfo.class);
			if (null != userInfoModel) {
				intent.putExtra("MODEL", userInfoModel);
			}
			startActivityForResult(intent, 100);
			break;
		case R.id.llInterestAct:
			intent = new Intent(getActivity(), InterestActList.class);
			startActivity(intent);
			break;
		case R.id.llMessage:
			intent = new Intent(getActivity(), Message.class);
			startActivity(intent);
			break;
		case R.id.llSetting:
			intent = new Intent(getActivity(), Setting.class);
			startActivity(intent);
			break;
		case R.id.llRecommend:
			intent = new Intent(getActivity(), RecommendAct.class);
			startActivity(intent);
			break;
		case R.id.llSignIn:
			intent = new Intent(getActivity(), SignIn.class);
			startActivity(intent);
			break;
		case R.id.llVersion:

			break;
		}
	}
	
	/**
	 * 获取个人信息
	 */
	private void getUserInfo() {
		HttpGetUtil param = new HttpGetUtil(getActivity(), "act/userInfo/getInfo");
		HttpStringGet task = new HttpStringGet(getActivity(), param.toString(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				try {
					JSONObject object = new JSONObject(result);
					Gson gson = new Gson();
					userInfoModel = gson.fromJson(object.getString("uInfo"), UserInfoModel.class);
					if (userInfoModel != null ) {
						AppPreference.saveUserInfo(getActivity(), userInfoModel);
						setUserInfo();
					} else {
						Toast.makeText(getActivity(), "获取个人信息失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(getActivity(), "个人信息解析失败", Toast.LENGTH_SHORT).show();
				}
			}
			
			@Override
			public void relogin(String msg) {
				Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void error(int code, String msg) {
				Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getActivity(), error.getMsg(), Toast.LENGTH_SHORT).show();
			}
		});
		RequestManager.addRequest(task, getActivity());
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == getActivity().RESULT_OK) {
			switch (requestCode) {
			case 100:
				if (data != null && data.hasExtra("MODEL")) {
					userInfoModel = (UserInfoModel) data.getSerializableExtra("MODEL");
					setUserInfo();
				}
				break;
			}
		}
	}
	
	/**
	 * 显示个人信息
	 */
	private void setUserInfo() {
		imageLoader.displayImage(userInfoModel.getHead_img_url(), ivUserIcon, options);
		tvUserName.setText(userInfoModel.getNick_name());
		if (userInfoModel.getSex() == 1) {
			ivSex.setImageResource(R.drawable.icon_sex_male);
		} else {
			ivSex.setImageResource(R.drawable.icon_sex_female);
		}
	}
	
	/**
	 * 从SharePreference显示个人信息
	 */
	private void setUserInfoFromSP(){
		imageLoader.displayImage(AppPreference.getUserPersistent(getActivity(), AppPreference.USER_PHOTO), ivUserIcon, options);
		tvUserName.setText(AppPreference.getUserPersistent(getActivity(), AppPreference.NICK_NAME));
		if (AppPreference.getUserPersistentInt(getActivity(), AppPreference.USER_SEX) == 1) {
			ivSex.setImageResource(R.drawable.icon_sex_male);
		} else {
			ivSex.setImageResource(R.drawable.icon_sex_female);
		}
	}
}
