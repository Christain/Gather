package com.gather.android.adapter;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.http.HttpGetUtil;
import com.gather.android.http.HttpStringGet;
import com.gather.android.http.RequestManager;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.ActList;
import com.google.gson.Gson;

@SuppressLint("InflateParams")
public class SignInAdapter extends SuperAdapter {

	private Context context;
	private ResponseListener listener;
	private Response.ErrorListener errorListener;
	private int page, limit = 10, totalNum, maxPage, isOver;

	public SignInAdapter(Context context) {
		super(context);
		this.context = context;
		this.initListener();
	}

	private void initListener() {
		listener = new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (page == 1) {
					JSONObject object = null;
					try {
						object = new JSONObject(result);
						totalNum = object.getInt("totalNum");
						if (totalNum % limit == 0) {
							maxPage = totalNum / limit;
						} else {
							maxPage = (totalNum / limit) + 1;
						}
					} catch (JSONException e) {
						e.printStackTrace();
						refreshOver(-1, "数据解析出错");
						isRequest = false;
						return;
					} finally {
						object = null;
					}
				}
				Gson gson = new Gson();
				ActList list = gson.fromJson(result, ActList.class);
				if (list != null && list.getActList() != null) {
					switch (loadType) {
					case REFRESH:
						if (totalNum == 0) {
							refreshOver(code, ISNULL);
						} else if (page == maxPage) {
							isOver = 1;
							refreshOver(code, ISOVER);
						} else {
							page++;
							refreshOver(code, CLICK_MORE);
						}
						refreshItems(list.getActList());
						break;
					case LOADMORE:
						if (page != maxPage) {
							page++;
							loadMoreOver(code, CLICK_MORE);
						} else {
							isOver = 1;
							loadMoreOver(code, ISOVER);
						}
						addItems(list.getActList());
						break;
					}
					isRequest = false;
				}
			}

			@Override
			public void relogin(String msg) {
				switch (loadType) {
				case REFRESH:
					refreshOver(5, msg);
					break;
				case LOADMORE:
					loadMoreOver(5, msg);
					break;
				}
				isRequest = false;
			}

			@Override
			public void error(int code, String msg) {
				switch (loadType) {
				case REFRESH:
					refreshOver(code, msg);
					break;
				case LOADMORE:
					loadMoreOver(code, msg);
					break;
				}
				isRequest = false;
			}
		};
		errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				switch (loadType) {
				case REFRESH:
					refreshOver(-1, error.getMsg());
					break;
				case LOADMORE:
					loadMoreOver(-1, error.getMsg());
					break;
				}
				isRequest = false;
			}
		};
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_sign_in, null);
			holder = new ViewHolder();
			holder.llItemAll = (LinearLayout) convertView.findViewById(R.id.llItemAll);
			holder.tvActTitle = (TextView) convertView.findViewById(R.id.tvActTitle);
			holder.tvActTime = (TextView) convertView.findViewById(R.id.tvActTime);
			holder.tvActLocation = (TextView) convertView.findViewById(R.id.tvActLocation);
			holder.tvActType = (TextView) convertView.findViewById(R.id.tvActType);
			holder.llMark = (LinearLayout) convertView.findViewById(R.id.llMark);
			
			holder.llItemAll.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					
				}
			});
 
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		return convertView;
	}

	private static class ViewHolder {
		public TextView tvActTitle, tvActTime, tvActLocation, tvActType;
		public LinearLayout llItemAll, llMark;
	}

	@Override
	public void refresh() {

	}

	@Override
	public void loadMore() {
		if (isOver == 1) {
			loadMoreOver(0, ISOVER);
		} else {
			if (!isRequest) {
				this.isRequest = true;
				this.loadType = LOADMORE;
				HttpGetUtil param = new HttpGetUtil(context, "act/actInfo/getUActs");
				param.setParam("page", page);
				param.setParam("size", limit);
				HttpStringGet task = new HttpStringGet(context, param.toString(), listener, errorListener);
				RequestManager.addRequest(task, context);
			}
		}
	}

	public void getSignInList() {
		if (!isRequest) {
			this.isRequest = true;
			this.loadType = REFRESH;
			this.page = 1;
			HttpGetUtil param = new HttpGetUtil(context, "act/actInfo/getUActs");
			param.setParam("page", page);
			param.setParam("size", limit);
			HttpStringGet task = new HttpStringGet(context, param.toString(), listener, errorListener);
			RequestManager.addRequest(task, context);
		}
	}

}
