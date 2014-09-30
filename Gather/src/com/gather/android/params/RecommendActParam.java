package com.gather.android.params;

import android.content.Context;

import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.MultipartParams;

/**
 * 上传推荐的活动（反馈）
 *
 */
public class RecommendActParam extends MultipartParams {

	public RecommendActParam(Context context, int imgId, String actName, String actTime, String actAddress, String actContact, String remark) {
		super(context, "act/actInfo/uRecommend");
		setParameter("imgId", imgId);
		setParameter("actName", actName);
		setParameter("actTime", actTime);
		setParameter("actAddress", actAddress);
		setParameter("actContact", actContact);
		setParameter("remark", remark);
		GatherApplication app = (GatherApplication) context.getApplicationContext();
		if (app.mLocation != null) {
			setParameter("lon", app.mLocation.getLongitude());
			setParameter("lat", app.mLocation.getLatitude());
			setParameter("address", app.mLocation.getAddrStr());
		}
	}

}
