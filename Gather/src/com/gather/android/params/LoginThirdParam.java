package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 第三方登录
 *
 */
public class LoginThirdParam extends StringParams {

	/**
	 * @param context
	 * @param loginType 0用户名，1手机号，2email，3sina，4qq
	 * @param openid
	 * @param token
	 * @param expiresIn 过期时间
	 */
	public LoginThirdParam(Context context, int loginType, String openid, String token, String expiresIn) {
		super(context, "user/userInfo/login");
		setParameter("loginType", loginType);
		setParameter("openid", openid);
		setParameter("token", token);
		setParameter("expiresIn", expiresIn);
	}

}
