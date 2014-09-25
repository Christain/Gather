package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 获取兴趣标签
 *
 */
public class RegisterGetInterestParam extends StringParams {

	public RegisterGetInterestParam(Context context) {
		super(context, "act/tagInfo/getUTags");
	}

}
