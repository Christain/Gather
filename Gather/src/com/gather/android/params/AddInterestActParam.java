package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 把活动添加为兴趣活动
 */
public class AddInterestActParam extends StringParams {

	public AddInterestActParam(Context context, int actId) {
		super(context, "act/actInfo/addLovAct");
		setParameter("actId", actId);
	}

}
