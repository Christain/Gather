package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 添加分享活动的次数(分享成功后调用)
 */
public class AddShareActNumParam extends StringParams {

	public AddShareActNumParam(Context context, int actId) {
		super(context, "act/actInfo/addShareAct");
		setParameter("actId", actId);
	}
}
