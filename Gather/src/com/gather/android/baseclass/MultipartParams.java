package com.gather.android.baseclass;

import java.io.File;
import java.util.HashMap;

import android.content.Context;

import com.gather.android.constant.Constant;

public class MultipartParams {
	
	private String url;
	private HashMap<String, Object> params;
	
	public MultipartParams(Context context, String url) {
		this.params = new HashMap<String, Object>();
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
	
	public void setParameter(String key, File value) {
		if (key != null && !key.equals("") && value != null) {
			this.params.put(key, value);
		}
	}

	public boolean hasParameters() {
		return !params.isEmpty();
	}

	public String getUrl() {
		return new StringBuffer().append(Constant.DEFOULT_REQUEST_URL).append(url).toString();
	}

	public HashMap<String, Object> getParameters() {
		return params;
	}

}
