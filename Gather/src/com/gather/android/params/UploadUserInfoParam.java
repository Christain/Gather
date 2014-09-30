package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.MultipartParams;

/**
 * 修改个人信息
 */
public class UploadUserInfoParam extends MultipartParams {

	public UploadUserInfoParam(Context context, String nickName, int sex, String birth, String address, String real_name, String contact_phone, int headImgId) {
		super(context, "user/userInfo/updateInfo");
		setParameter("nickName", nickName);
		setParameter("sex", sex);
		if (birth.contains(" 00:00:00")) {
			birth = birth.substring(0, birth.indexOf(" "));
		}
		setParameter("birth", birth);
		setParameter("address", address);
		setParameter("real_name", real_name);
		setParameter("contact_phone", contact_phone);
		if (headImgId != 0) {
			setParameter("headImgId", headImgId);
		}
	}

}
