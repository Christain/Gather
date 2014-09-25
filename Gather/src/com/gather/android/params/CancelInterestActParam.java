package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 取消对活动的兴趣
 */
public class CancelInterestActParam extends StringParams {

	public CancelInterestActParam(Context context, int actId) {
		super(context, "act/actInfo/delLovAct");
		setParameter("actId", actId);
	}
}
