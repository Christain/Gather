package com.gather.android.http;


public interface ResponseListener {
	
	public void success(int code, String msg, String result);
	
	public void relogin(String msg);
	
	public void error(int code, String msg);

}
