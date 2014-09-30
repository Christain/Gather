package com.gather.android.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.examples.toolbox.updated.FLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.gather.android.constant.Constant;
import com.gather.android.preference.AppPreference;

public class MultipartRequest extends Request<String> {

	private MultipartEntity entity = new MultipartEntity();
	private HashMap<String, Object> mMap;
	private ErrorListener errorListener;
	private ResponseListener listener;
	private long startTime;
	private String url;
	private boolean isSuccess;
	private String cookies;
	private Context context;

	public MultipartRequest(Context context, String url, ResponseListener listener, Response.ErrorListener errorListener, HashMap<String, Object> parameters) {
		super(Method.POST, url, errorListener);
		this.context = context;
		this.listener = listener;
		this.errorListener = errorListener;
		this.isSuccess = false;
		buildMultipartEntity(parameters);
		mMap = parameters;
		if (Constant.SHOW_LOG) {
			startTime = System.currentTimeMillis();
			this.url = url;
		}
		cookies = AppPreference.getCookie(context);
	}

	private void buildMultipartEntity(HashMap<String, Object> parameters) {
		entity = encodePOSTUrl(entity, parameters);
	}

	private MultipartEntity encodePOSTUrl(MultipartEntity mEntity, HashMap<String, Object> parameters) {
		if (parameters != null && !parameters.isEmpty() && parameters.size() > 0) {
			for (Map.Entry<String, Object> entry : parameters.entrySet()) {
				if (entry.getValue() instanceof File) {
					File file = (File) entry.getValue();
					ContentBody fileBody = new FileBody(file);
					mEntity.addPart(entry.getKey(), fileBody);
				} else if (entry.getValue() instanceof String) {
					String value = (String) entry.getValue();
					try {
						mEntity.addPart(entry.getKey(), new StringBody(value, Charset.forName(HTTP.UTF_8)));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return mEntity;
	}

	@Override
	public String getBodyContentType() {
		return entity.getContentType().getValue();
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			entity.writeTo(bos);
		} catch (IOException e) {
			FLog.e("IOException writing to ByteArrayOutputStream");
		}
		return bos.toByteArray();
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		if (cookies != null && cookies.length() > 0) {
			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("Cookie", cookies);
			return headers;
		}
		return super.getHeaders();
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		Response<String> superResponse = netWorkResponse(response);
		Map<String, String> responseHeaders = response.headers;
		String rawCookies = responseHeaders.get("Set-Cookie");
		if (rawCookies != null) {
			AppPreference.updateCookie(context, rawCookies);
			cookies = rawCookies;
		}
		return superResponse;
	}

	private Response<String> netWorkResponse(NetworkResponse response) {
		String parsed;
		try {
			parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
		} catch (UnsupportedEncodingException e) {
			parsed = new String(response.data);
		}
		return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
	}

	@Override
	protected void deliverResponse(String response) {
		JSONObject object = null;
		String msg = null;
		try {
			object = new JSONObject(response);
			int code = object.getInt("code");
			msg = object.getString("msg");
			if (code == 0) {
				isSuccess = true;
				listener.success(code, msg, object.getString("body"));
			} else if (code == 5) {
				isSuccess = false;
				listener.relogin(msg);
			} else {
				isSuccess = false;
				listener.error(code, msg);
			}
			msg = null;
		} catch (JSONException e) {
			isSuccess = false;
			msg = null;
			object = null;
			listener.error(-1, "服务器返回错误");
			e.printStackTrace();
		}
		if (Constant.SHOW_LOG) {
			StringBuffer sb = new StringBuffer();
			sb.append("------------------------------------start----------------------------------\n");
			sb.append("URL---> ");
			sb.append(url);
			sb.append("\n");
			sb.append("cookie---> ");
			sb.append(cookies);
			sb.append("\n");
			if (!mMap.isEmpty()) {
				sb.append("Parameters: ");
				Iterator<java.util.Map.Entry<String, Object>> iterator = (Iterator<Entry<String, Object>>) mMap.entrySet().iterator();
				while (iterator.hasNext()) {
					java.util.Map.Entry<String, Object> entry = iterator.next();
					if (entry.getValue() instanceof String) {
						sb.append("\n   ");
						sb.append(entry.getKey());
						sb.append(" = ");
						sb.append(entry.getValue());
					} else if (entry.getValue() instanceof File) {
						sb.append("\n   File_Path > ");
						sb.append(entry.getKey());
						sb.append(" = ");
						sb.append(((File) entry.getValue()).getAbsolutePath());
					}
				}
				iterator = null;
			}
			sb.append("\n>>> Response: ");
			if (object != null) {
				sb.append("\n" + object.toString());
			} else {
				sb.append("\n" + response);
			}
			sb.append("\n--------------------------------- ");
			double time = (System.currentTimeMillis() - startTime);
			sb.append(time);
			sb.append("ms ----------------------------------");
			if (isSuccess) {
				Log.i("Request", sb.toString());
			} else {
				// Toast.makeText(context, sb.toString(),
				// Toast.LENGTH_SHORT).show();
				Log.e("Request", sb.toString());
			}
			sb = null;
		}
		object = null;
		mMap.clear();
	}

	@Override
	public void deliverError(VolleyError error) {
		if (Constant.SHOW_LOG) {
			StringBuffer sb = new StringBuffer();
			sb.append("-----------------------------------start----------------------------------\n");
			sb.append("URL---> ");
			sb.append(url);
			sb.append("\n");
			sb.append("cookie---> ");
			sb.append(cookies);
			sb.append("\n");
			if (!mMap.isEmpty()) {
				sb.append("Parameters: ");
				Iterator<Entry<String, Object>> iterator = mMap.entrySet().iterator();
				while (iterator.hasNext()) {
					java.util.Map.Entry<String, Object> entry = iterator.next();
					if (entry.getValue() instanceof String) {
						sb.append("\n   ");
						sb.append(entry.getKey());
						sb.append(" = ");
						sb.append(entry.getValue());
					} else if (entry.getValue() instanceof File) {
						sb.append("\n   File_Path > ");
						sb.append(entry.getKey());
						sb.append(" = ");
						sb.append(((File) entry.getValue()).getAbsolutePath());
					}
				}
				iterator = null;
			}
			sb.append("\n>>> ErrorCode:  ");
			if (error.networkResponse != null) {
				sb.append(error.networkResponse.statusCode);
			} else {
				sb.append(error.getMessage());
			}
			sb.append("\n--------------------------------- ");
			double time = (System.currentTimeMillis() - startTime);
			sb.append(time);
			sb.append("ms ----------------------------------");
			Log.e("Request", sb.toString());
			sb = null;
		}
		if (error.getMessage() == null) {
			error.setMessage("服务器访问出错了~~");
		}
		errorListener.onErrorResponse(error);
		mMap.clear();
	}
}
