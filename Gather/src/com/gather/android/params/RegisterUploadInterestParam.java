package com.gather.android.params;

import java.util.ArrayList;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 上传兴趣标签
 *
 */
public class RegisterUploadInterestParam extends StringParams {

	public RegisterUploadInterestParam(Context context, ArrayList<Integer> tagIds) {
		super(context, "act/tagInfo/setUTags");
		if (tagIds != null && tagIds.size() != 0) {
			for (int i = 0; i < tagIds.size(); i++) {
				setParameter("tagIds", tagIds.get(i));
			}
		}
	}

}
