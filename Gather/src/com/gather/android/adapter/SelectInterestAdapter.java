package com.gather.android.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.custom.vg.list.CustomAdapter;
import com.gather.android.R;
import com.gather.android.model.InterestStringModel;

@SuppressLint("InflateParams")
public class SelectInterestAdapter extends CustomAdapter {

	private Context mContext;
	private LayoutInflater inflater;
	private ArrayList<InterestStringModel> list;
	private boolean isActSelect = false;//判断是否是活动选择

	public SelectInterestAdapter(Context context, ArrayList<InterestStringModel> list) {
		this.mContext = context;
		this.list = list;
		this.isActSelect = false;
	}
	
	public SelectInterestAdapter(Context context, ArrayList<InterestStringModel> list, String act_select) {
		this.mContext = context;
		this.list = list;
		this.isActSelect = true;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public InterestStringModel getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (!isActSelect) {
				convertView = inflater.inflate(R.layout.item_select_interest, null);
			} else {
				convertView = inflater.inflate(R.layout.item_act_select_interest, null);
			}
			holder = new ViewHolder();
			holder.tvMessage = (TextView) convertView.findViewById(R.id.tvMessage);
			holder.tvBackground = (TextView) convertView.findViewById(R.id.tvBackground);
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		InterestStringModel model = getItem(position);
		switch (position % 8) {
		case 0:
			holder.tvMessage.setBackgroundColor(0xFFF5C22F);
			break;
		case 1:
			holder.tvMessage.setBackgroundColor(0xFFE69433);
			break;
		case 2:
			holder.tvMessage.setBackgroundColor(0xFFDC3932);
			break;
		case 3:
			holder.tvMessage.setBackgroundColor(0xFF5997B5);
			break;
		case 4:
			holder.tvMessage.setBackgroundColor(0xFF88B764);
			break;
		case 5:
			holder.tvMessage.setBackgroundColor(0xFFF59574);
			break;
		case 6:
			holder.tvMessage.setBackgroundColor(0xFFAD7895);
			break;
		case 7:
			holder.tvMessage.setBackgroundColor(0xFF8DABBA);
			break;
		}
		holder.tvMessage.setText(model.getName().toString());
		holder.tvBackground.setText(model.getName());
		if (model.isSelect()) {
			holder.tvBackground.setVisibility(View.VISIBLE);
			holder.image.setVisibility(View.VISIBLE);
		} else {
			holder.tvBackground.setVisibility(View.GONE);
			holder.image.setVisibility(View.GONE);
		}
		return convertView;
	}

	public class ViewHolder {
		public TextView tvMessage, tvBackground;
		public ImageView image;
	}
	
	public void notifyDataChange(ArrayList<InterestStringModel> list){
		this.list = list;
		notifyDataSetChanged();
	}

}
