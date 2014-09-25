package com.gather.android.http;

import com.gather.android.constant.Constant;

import android.content.Context;

public class HttpGetUtil {
	
	private StringBuffer sb;
	
	public HttpGetUtil(Context context, String url) {
		sb = new StringBuffer();
		sb.append(Constant.DEFOULT_REQUEST_URL);
		sb.append(url);
	}
	
	public void setParam(String key, Object value) {
		sb.append("&");
		sb.append(key);
		sb.append("=");
		sb.append(value);
	}
	
	public String toString() {
		return sb.toString();
	}

}
