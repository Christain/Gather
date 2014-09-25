package com.gather.android.baseclass;

import java.util.HashMap;

import android.content.Context;

import com.gather.android.constant.Constant;

public class StringParams {
	
	private String url;
	private HashMap<String, String> params;
	
	public StringParams(Context context, String url) {
		this.params = new HashMap<String, String>();
		setUrl(url);
	}
	
	private void setUrl(String url) {
		if (url != null) {
			this.url = url;
		} else {
			this.url =  "";
		}
	}
	
	protected void initParameters() {
		this.params.clear();
	}

	public void setParameter(String key, String value) {
		if (key != null && !key.equals("") && value != null) {
			this.params.put(key, value);
		}
	}

	public void setParameter(String key, int value) {
		if (key != null && !key.equals("")) {
			this.params.put(key, String.valueOf(value));
		}
	}
	
	public void setParameter(String key, long value) {
		if (key != null && !key.equals("")) {
			this.params.put(key, String.valueOf(value));
		}
	}
	
	public void setParameter(String key, float value) {
		if (key != null && !key.equals("")) {
			this.params.put(key, String.valueOf(value));
		}
	}
	
	public void setParameter(String key, double value) {
		if (key != null && !key.equals("")) {
			this.params.put(key, String.valueOf(value));
		}
	}

	public boolean hasParameters() {
		return !params.isEmpty();
	}

	public String getUrl() {
		return new StringBuffer().append(Constant.DEFOULT_REQUEST_URL).append(url).toString();
	}

	public HashMap<String, String> getParameters() {
		return params;
	}

}
