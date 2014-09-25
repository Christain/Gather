package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 注册手机账号时获取验证码
 *
 */
public class RegisterPhoneGetNumParam extends StringParams {

	public RegisterPhoneGetNumParam(Context context, String phoneNum) {
		super(context, "user/userInfo/newPhone");
		setParameter("phoneNum", phoneNum);
	}

}
