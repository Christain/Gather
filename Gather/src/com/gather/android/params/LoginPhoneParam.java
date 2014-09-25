package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 手机（本平台）登录
 *
 */

public class LoginPhoneParam extends StringParams {

	/**
	 * @param context
	 * @param type 0用户名，1手机号，2email
	 * @param loginName
	 * @param userPass
	 */
	public LoginPhoneParam(Context context, int loginType, String loginName, String userPass) {
		super(context, "user/userInfo/login");
		setParameter("loginType", loginType);
		setParameter("loginName", loginName);
		setParameter("userPass", userPass);
	}

}
