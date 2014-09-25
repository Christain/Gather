package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 找回密码时获取验证码
 *
 */
public class FindPasswordGetNumParam extends StringParams {

	public FindPasswordGetNumParam(Context context, String phoneNum) {
		super(context, "user/userInfo/getPhCode");
		setParameter("phoneNum", phoneNum);
	}

}
