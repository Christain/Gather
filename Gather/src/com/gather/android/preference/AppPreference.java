package com.gather.android.preference;

import com.gather.android.model.UserInfoModel;

import android.R.integer;
import android.R.menu;
import android.content.Context;
import android.content.SharedPreferences;

public class AppPreference {
	
	private final static String NAME = "APP_PREFERENCE";
	private static SharedPreferences preferences;
	
	/*******************用户信息**************************/
	public final static String USER_ID = "USER_ID";						//用户ID
	public static final String USER_PHOTO = "USER_PHOTO";				//头像
	public static final String USER_SEX = "USER_SEX";					//性别
	public static final String NICK_NAME = "NICK_NAME";					//昵称
	public static final String REAL_NAME = "REAL_NAME";					//用户姓名
	public static final String USER_PWD = "USER_PWD";					//密码
	public static final String TELPHONE = "TELPHONE";					//电话
	public static final String USER_EMAIL = "USER_EMAIL";				//邮箱
	public static final String USER_BIRTHDAY = "USER_BIRTHDAY";			//生日
	public static final String ADDRESS = "ADDRESS";						//地址
	public static final String IS_REGISTER = "IS_REGISTER";				//是否完善资料
	public static final String CONTACT_PHONE = "CONTACT_PHONE";			//联系电话
	public static final String CONTACT_QQ = "CONTACT_QQ";				//联系QQ
	public static final String LOGIN_TYPE = "LOGIN_TYPE";				//登录方式
	public static final String SINA_ID = "SINA_ID";						//新浪openid
	public static final String SINA_TOKEN = "SINA_TOKEN";				//新浪access_token
	public static final String SINA_EXPIRES = "SINA_EXPIRES";			//新浪expires_in过期时间
	public static final String QQ_ID = "QQ_ID";							//腾讯openid
	public static final String QQ_TOKEN = "QQ_TOKEN";					//腾讯access_token
	public static final String QQ_EXPIRES = "QQ_EXPIRES";				//腾讯expires_in过期时间
	public static final String LAST_LOGIN_TIME = "LAST_LOGIN_TIME";		//最后登录时间
	
	
	/*******************登录平台TYPE**************************/
	public static final int TYPE_SELF = 100;							//自己平台登录
	public static final int TYPE_SINA = 101;							//新浪登录
	public static final int TYPE_QQ = 102;								//QQ登录
	
	/**********************Cookie**********************************/
	public static final String COOKIE = "COOKIE";
	
	
	
	
	
	
	
	private static void getInstance(Context context) {
		if (preferences == null) {
			preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		}
	}

	public static String getUserPersistent(Context context, String key) {
		getInstance(context);
		return preferences.getString(key, "");
	}

	public static int getUserPersistentInt(Context context, String key) {
		getInstance(context);
		return preferences.getInt(key, 0);
	}

	public static void save(Context context, String key, String value) {
		getInstance(context);
		SharedPreferences.Editor mEditor = preferences.edit();
		mEditor.putString(key, value);
		mEditor.commit();
	}

	public static void save(Context context, String key, int value) {
		getInstance(context);
		SharedPreferences.Editor mEditor = preferences.edit();
		mEditor.putInt(key, value);
		mEditor.commit();
	}

	public static void clearInfo(Context context) {
		getInstance(context);
		SharedPreferences.Editor mEditor = preferences.edit();
		mEditor.clear();
		mEditor.commit();
	}
	
	/**
	 * 更新第三方登录TYPE, openid, access_token
	 */

	public static void saveThirdLoginInfo(Context context, int login_type, String open_id, String access_token, String expires_in) {
		getInstance(context);
		SharedPreferences.Editor mEditor = preferences.edit();
		if (login_type == TYPE_QQ) {
			mEditor.putInt(LOGIN_TYPE, TYPE_QQ);
			mEditor.putString(QQ_ID, open_id);
			mEditor.putString(QQ_TOKEN, access_token);
			mEditor.putString(QQ_EXPIRES, expires_in);
		} else if (login_type == TYPE_SINA) {
			mEditor.putInt(LOGIN_TYPE, TYPE_SINA);
			mEditor.putString(SINA_ID, open_id);
			mEditor.putString(SINA_TOKEN, access_token);
			mEditor.putString(SINA_EXPIRES, expires_in);
		}
		mEditor.commit();
	}
	
	/**
	 * 更新手机号登录信息
	 */
	public static void savePhoneLoginInfo(Context context, String phoneNum) {
		getInstance(context);
		SharedPreferences.Editor mEditor = preferences.edit();
		mEditor.putInt(LOGIN_TYPE, TYPE_SELF);
		mEditor.putString(TELPHONE, phoneNum);
		mEditor.commit();
	}
	
	/**
	 * 保存个人信息
	 * @param context
	 * @param model
	 */
	public static void saveUserInfo(Context context, UserInfoModel model){
		getInstance(context);
		SharedPreferences.Editor mEditor = preferences.edit();
		mEditor.putInt(USER_ID, model.getUid());
		mEditor.putInt(USER_SEX, model.getSex());
		mEditor.putString(USER_BIRTHDAY, model.getBirth());
		mEditor.putString(NICK_NAME, model.getNick_name());
		mEditor.putString(REAL_NAME, model.getReal_name());
		mEditor.putString(USER_PHOTO, model.getHead_img_url());
		mEditor.putString(TELPHONE, model.getPho_num());
		mEditor.putString(USER_EMAIL, model.getEmail());
		mEditor.putString(ADDRESS, model.getAddress());
		mEditor.putInt(IS_REGISTER, model.getIs_regist());
		mEditor.putString(CONTACT_PHONE, model.getContact_phone());
		mEditor.putString(SINA_EXPIRES, model.getSina_expires_in());
		mEditor.putString(SINA_TOKEN, model.getSina_token());
		mEditor.putString(SINA_ID, model.getSina_openid());
		mEditor.putString(QQ_EXPIRES, model.getQq_expires_in());
		mEditor.putString(QQ_TOKEN, model.getQq_token());
		mEditor.putString(QQ_ID, model.getQq_openid());
		mEditor.putString(CONTACT_QQ, model.getContact_qq());
		mEditor.putString(LAST_LOGIN_TIME, model.getLast_login_time());
		mEditor.commit();
	}
	
	/**
	 * 更新Cookie
	 */
	public static void updateCookie(Context context, String cookie) {
		getInstance(context);
		SharedPreferences.Editor mEditor = preferences.edit();
		mEditor.putString(COOKIE, cookie);
		mEditor.commit();
	}
	
	/**
	 * 获取Cookie
	 */
	public static String getCookie(Context context) {
		getInstance(context);
		return preferences.getString(COOKIE, "");
	}
	
}
