package com.gather.android.params;

import java.io.File;

import android.content.Context;

import com.gather.android.baseclass.MultipartParams;

/**
 * 活动相关图片上传（成功后返回图片ID）
 *
 */
public class UploadActImageParam extends MultipartParams {

	public UploadActImageParam(Context context, File actImage) {
		super(context, "act/actInfo/actImgUp");
		setParameter("actImg", actImage);
	}

}
