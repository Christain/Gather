package com.gather.android.params;

import java.io.File;

import android.content.Context;

import com.gather.android.baseclass.MultipartParams;

/**
 * 上传头像，成功后返回头像Id，然后才是完善资料
 *
 */
public class RegisterUploadPhotoParam extends MultipartParams{

	public RegisterUploadPhotoParam(Context context, File photo) {
		super(context, "user/userInfo/headImgUp");
		setParameter("headImg", photo);
	}

}
