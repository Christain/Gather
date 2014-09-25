package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 修改密码
 *
 */
public class ResetPasswordParam extends StringParams {

	public ResetPasswordParam(Context context, String newPass) {
		super(context, "user/userInfo/phRePass");
		setParameter("newPass", newPass);
	}

}
