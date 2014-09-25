package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 完善用户资料（包括头像和兴趣）在兴趣页面调用
 *
 */
public class RegisterUploadUserInfoParam extends StringParams {

	/**
	 * @param context
	 * @param userPass
	 * @param nickName
	 * @param sex
	 * @param birth （例：1997-07-01）
	 * @param address
	 * @param email
	 * @param headImgId 头像ID
	 */
	public RegisterUploadUserInfoParam(Context context, String userPass, String nickName, int sex, String birth, String address, String email, int headImgId) {
		super(context, "user/userInfo/regiInfo");
		setParameter("userPass", userPass);
		setParameter("nickName", nickName);
		setParameter("sex", sex);
		setParameter("birth", birth);
		setParameter("address", address);
		setParameter("email", email);
		setParameter("headImgId", headImgId);
	}

}
