package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 验证验证码 Ps：注册和找回密码的验证接口一样
 *
 */
public class RegisterPhoneIdentifyNumParam extends StringParams {

	public RegisterPhoneIdentifyNumParam(Context context, String phoneNum, String idfyCode) {
		super(context, "user/userInfo/validPhCode");
		setParameter("phoneNum", phoneNum);
		setParameter("idfyCode", idfyCode);
	}

}
